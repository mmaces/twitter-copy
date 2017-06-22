package de.hska.persistence.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.hska.core.security.SimpleSecurity;
import de.hska.persistence.domain.Post;
import de.hska.persistence.domain.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.*;

@Repository
public class MainRepository {

	@Autowired
	private RedisTemplate<String, Object> template;
	@Autowired
	private ObjectMapper mapper;

	/** User Operations
	 *
 	 */

	public boolean addUser(User user) {
		BoundHashOperations hashOps = template.boundHashOps(user.getUsername());

		// Möglichkeit 1 zum Zugriff auf User
		hashOps.put("username", user.getUsername());
		hashOps.put("password", user.getPassword());
		hashOps.put("email", user.getEmail());

		// Speicher des neu Registrierten users in extra Struktur
		template.opsForSet().add("user", user.getUsername());

		// Möglichkeit 2 zum Zugriff auf User
		try {
			template.opsForValue().set(user.getUsername(), mapper.writeValueAsString(user));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		return true;
	}

	public User getUser(String username) {
		User tmp = null;
		try {
			String user = (String) template.opsForValue().get(username);
			if(user != null)
				tmp = mapper.readValue(user, User.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tmp;
	}

	public ArrayList<String> searchUsers(String query) {
		Iterator usersIterator = template.opsForSet().members("user").iterator();
		ArrayList<String> result = new ArrayList<>();
		
		while(usersIterator.hasNext()) {
			String user = (String) usersIterator.next();
			user = user.toLowerCase();
			if (user.startsWith(query.toLowerCase())) {
				result.add(user);
			}
		}

		return result;
	}

	/** Post Operations
	 *
	 */

	public Post addPost(String postText, String uname) {
		Post post = new Post();
		post.setPostText(postText);
		post.setUsername(uname);
		post.setDate(System.currentTimeMillis());
		String uuid = UUID.randomUUID().toString();
		post.setPostId(uuid);
		
		try {
			template.opsForValue().set(uuid, mapper.writeValueAsString(post));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return new Post();
		}
		
		template.opsForList().leftPush("postIds:" + uname, uuid);

		try {
			template.opsForList().leftPush("allPosts", mapper.writeValueAsString(post));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		template.convertAndSend("newPostQueue", post.getPostId());

		return post;
	}
	
	public ArrayList<String> getPostIds(String uname) {
		// Get all elements of list
		return (ArrayList)template.opsForList().range("postIds:" + uname, 0, -1);
	}
	
	public Post getPost(String uuid) {
		try {
			return mapper.readValue(template.opsForValue().get(uuid).toString(), Post.class);
		} catch (IOException e) {
			e.printStackTrace();
			return new Post();
		} 
	}

	public ArrayList<Post> getAllPosts() {
		ArrayList<Post> posts = new ArrayList<Post>();
		List<Object> allPosts = template.opsForList().range("allPosts", 0, -1);

		for(Object post : allPosts) {
			String postString = (String) post;
			Post postObject = null;
			try {
				postObject = mapper.readValue(postString, Post.class);
			} catch (IOException e) {
				e.printStackTrace();
			}
			posts.add(postObject);
		}

		return posts;
	}
	
	public ArrayList<Post> getAllPosts(String uname) {
		ArrayList<Post> posts = new ArrayList<Post>();
		List<Object> following = template.opsForList().range("following:" + uname, 0, -1);
		following.add(uname);

		for(Object username : following) {
			for (String postId : getPostIds((String)username)) {
				posts.add(getPost(postId));
			}
		}

		Collections.sort(posts, (o1, o2) -> (int) (o2.getDate() - o1.getDate()));

		return posts;
	}

	public ArrayList<Post> getPaginatedPosts(String uname, int from, int to) {
		ArrayList<Post> posts;
		if(uname == null) {
			posts = getAllPosts();
		}else{
			posts = getAllPosts(uname);
		}

		if(posts.size() < to)
			to = posts.size();
		return new ArrayList<>(posts.subList(from, to));
	}

	/** Follower and Following Operations
	 *
	 */

	public boolean follow(String uname) {
		template.opsForList().leftPush("follower:" + uname, SimpleSecurity.getName());
		template.opsForList().leftPush("following:" + SimpleSecurity.getName(), uname);

		return true;
	}

	public boolean unfollow(String uname) {
		template.opsForList().remove("follower:" + uname, 0, SimpleSecurity.getName());
		template.opsForList().remove("following:" + SimpleSecurity.getName(), 0, uname);

		return true;
	}

	public ArrayList<String> getFollower(String uname) {
		List<Object> followers = template.opsForList().range("follower:" + uname, 0, -1);
		ArrayList<String> result = new ArrayList<>();

		for(Object follower : followers) {
			result.add((String) follower);
		}

		return result;
	}

	public ArrayList<String> getFollowing(String uname) {
		List<Object> followers = template.opsForList().range("following:" + uname, 0, -1);
		ArrayList<String> result = new ArrayList<>();

		for(Object follower : followers) {
			result.add((String) follower);
		}

		return result;
	}

	public boolean isFollowerOf(String uname) {
		List<Object> followers = template.opsForList().range("follower:" + uname, 0, -1);
		for(Object follower : followers) {
			if(follower.equals(SimpleSecurity.getName()))
				return true;
		}
		return false;
	}

	public Long sizeOfFollower(String uname) {
		return template.opsForList().size("follower:" + uname);
	}

	public Long sizeOfFollowing(String uname) {
		return template.opsForList().size("following:" + uname);
	}
}
