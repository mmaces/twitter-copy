package de.hska.core.interceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import de.hska.core.security.SimpleSecurity;
import de.hska.persistence.repository.RedisRepository;

public class SimpleCookieInterceptor extends HandlerInterceptorAdapter{
	@Autowired 
	private RedisRepository repo;

	// Is executed before invoking the handler
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		Cookie[] cookies = request.getCookies();
		
		if (!ObjectUtils.isEmpty(cookies)) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("auth")) {
					String auth = cookie.getValue();
					
					if (auth != null) {
						String username = repo.getUserByAuth(auth);
						if (username != null) {
							SimpleSecurity.setUser(username);
						}
					}
				}
			}
		}
		
		return true;
	}

	// Is executed after rendering the view
	@Override
	public void afterCompletion(
			HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		SimpleSecurity.unsetUser();
	}
}
