package de.hska.persistence.repository;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import de.hska.persistence.domain.User;


@Repository
public class RedisRepository {
	@Autowired 
	private RedisTemplate<String, Object> template;
	@Autowired
	private MainRepository repo;
	
	// Login
	public boolean auth(String uname, String pass) {
		User user = (User) repo.getUser(uname);
		return user != null ? user.getPassword().equals(pass) : false;
	}
	
	// Add Cookie Value to Redis .. (beim Login)
	public String addAuth(String uname, long timeout, TimeUnit tUnit) {
		User user = (User) repo.getUser(uname);
		String auth = UUID.randomUUID().toString();
		template.boundHashOps("uid:" + user.getUsername() + ":auth").put("auth", auth);
		template.expire("uid:" + user.getUsername() + ":auth", timeout, tUnit);
		template.opsForValue().set("auth:" + auth + ":uid", user.getUsername(), timeout, tUnit);
		return auth;
	}
	
	public String getAuth(String uname) {
		return (String)template.opsForHash().get("uid:" + uname + ":auth", "auth");
	}
	
	public String getUserByAuth(String auth) {
		return (String)template.opsForValue().get("auth:" + auth + ":uid");
	}
	
	// Delete Cookie Value from Redis... (beim Logout)
	public void deleteAuth(String uname) {
		String authKey = "uid:" + uname + ":auth";
		String auth = (String) template.boundHashOps(authKey).get("auth");
		List<String> keysToDelete = Arrays.asList(authKey, "auth:"+auth+":uid");
		template.delete(keysToDelete);
	}
}
