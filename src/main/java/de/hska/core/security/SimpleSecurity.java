package de.hska.core.security;

import javax.annotation.Resource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.NamedThreadLocal;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

public class SimpleSecurity{
	public static RedisTemplate<String, Object> template;
	
	private static final ThreadLocal<UserInfo> user = new NamedThreadLocal<UserInfo>("microblog-id");
	private static class UserInfo {
		String name;
	}
	
	public static void setUser(String name) {
		UserInfo userInfo = new UserInfo();
		userInfo.name = name;
		user.set(userInfo);
	}
	public static boolean isUserSignedIn(String name) {
		BoundHashOperations hashOps = template.boundHashOps("uid:" + name + ":auth");
		String auth = (String)hashOps.get("auth");
		
		if(auth == null)
			return false;
		
		return true;
	}
	public static boolean isSignedIn() {
		UserInfo userInfo = user.get();
		if(userInfo == null) {
			return false; 
		}
		
		return true;
	}
	public static String getName() { 
		return user.get().name; 
	}
}
