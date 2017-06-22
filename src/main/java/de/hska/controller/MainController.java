package de.hska.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import de.hska.core.security.SimpleSecurity;
import de.hska.persistence.domain.Post;
import de.hska.persistence.repository.MainRepository;

@Controller
public class MainController {
	@Autowired
	private MainRepository mainRepo;
	
	@RequestMapping(value="/auth/timeline", method=RequestMethod.GET) 
	public String getTimelines(@ModelAttribute("from") int from,
							   @ModelAttribute("to") int to, Model model) {
		if(SimpleSecurity.isSignedIn()) {
			ArrayList<Post> allPosts = mainRepo.getAllPosts();
			model.addAttribute("posts", mainRepo.getPaginatedPosts(null, from, to));

			model.addAttribute("postsListSize", allPosts.size());
			model.addAttribute("username", SimpleSecurity.getName());
			model.addAttribute("from", from);
			model.addAttribute("to", to);

			return "timeline";
		}
		return "redirect:/login";
	}
	
	@RequestMapping(value="/auth/privateTimeline", method=RequestMethod.GET)
	public String getPrivateTimeline(@ModelAttribute("user") String username, @ModelAttribute("from") int from,
									 @ModelAttribute("to") int to, Model model) {
		if(SimpleSecurity.isSignedIn()) {
			ArrayList<Post> allPosts = mainRepo.getAllPosts(username);
			model.addAttribute("posts", mainRepo.getPaginatedPosts(username, from, to));
			model.addAttribute("postsListSize", allPosts.size());
			model.addAttribute("username", username);
			model.addAttribute("from", from);
			model.addAttribute("to", to);

			return "privateTimeline";
		}
		return "redirect:/login";
	}
	
	@RequestMapping(value="/auth/search/{query}", method=RequestMethod.GET)
	public @ResponseBody ArrayList<String> getListOfUsernames(@PathVariable("query") String query) {
		return mainRepo.searchUsers(query);
	}
	
	@RequestMapping(value="/auth/post", method=RequestMethod.POST)
	public @ResponseBody Post savePost(@Param("postText") String postText) {
		return mainRepo.addPost(postText, SimpleSecurity.getName());
	}

	@RequestMapping(value="/auth/user/follower/{user}", method=RequestMethod.GET)
	public @ResponseBody ArrayList<String> getFollower(@PathVariable("user") String username) {
		return mainRepo.getFollower(username);
	}
	
	@RequestMapping(value="/auth/user/following/{user}", method=RequestMethod.GET)
	public @ResponseBody ArrayList<String> getFollowing(@PathVariable("user") String username) {
		return mainRepo.getFollowing(username);
	}
	
	@RequestMapping(value="/auth/user/follow/{user}", method=RequestMethod.PUT)
	public @ResponseBody boolean follow(@PathVariable("user") String username) {
		return mainRepo.follow(username);
	}

	@RequestMapping(value="/auth/user/follow/{user}", method=RequestMethod.DELETE)
	public @ResponseBody boolean unfollow(@PathVariable("user") String username) {
		return mainRepo.unfollow(username);
	}
	
	@RequestMapping(value="/auth/user/follower/size/{user}", method=RequestMethod.GET)
	public @ResponseBody Long sizeOfFollower(@PathVariable("user") String username) {
		return mainRepo.sizeOfFollower(username);
	}
	
	@RequestMapping(value="/auth/user/following/size/{user}", method=RequestMethod.GET)
	public @ResponseBody Long sizeOfFollowing(@PathVariable("user") String username) {
		return mainRepo.sizeOfFollowing(username);
	}

	@RequestMapping(value = "/auth/follower/{username}", method = RequestMethod.GET)
	public @ResponseBody String isFollowerOf(@PathVariable("username") String username) {
		return String.valueOf(mainRepo.isFollowerOf(username)) + ":" + SimpleSecurity.getName();
	}
}
