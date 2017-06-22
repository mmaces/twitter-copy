package de.hska.controller;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.hska.core.security.SimpleSecurity;
import de.hska.persistence.domain.User;
import de.hska.persistence.repository.MainRepository;
import de.hska.persistence.repository.RedisRepository;

@Controller
public class LoginController {
	@Autowired 
	private RedisRepository repository;
	private final Duration TIMEOUT = Duration.ofMinutes(15);
	@Autowired
	private MainRepository mainRepo;
	
	@RequestMapping(value={"/", "/index", "/index.html", "/login"}, method=RequestMethod.GET) 
	public String getLoginView() {
		return "login";
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(@ModelAttribute("user") User user, HttpServletResponse response, Model model) {
		// Is the user currently authenticated?
		if (repository.auth(user.getUsername(), user.getPassword())) {
			String auth = repository.addAuth(user.getUsername(), TIMEOUT.getSeconds(), TimeUnit.SECONDS);
			Cookie cookie = new Cookie("auth", auth);
			cookie.setMaxAge((int) TIMEOUT.getSeconds());
			response.addCookie(cookie);

			model.addAttribute("from", 0);
			model.addAttribute("to", 20);

			SimpleSecurity.setUser(user.getUsername());
			return "redirect:/auth/timeline";
		}

		return "login";
	}
	
	@RequestMapping(value="/register", method = RequestMethod.POST)
	public String register(@ModelAttribute("user") User user, HttpServletResponse response, Model model) {
		mainRepo.addUser(user);
		String auth = repository.addAuth(user.getUsername(), TIMEOUT.getSeconds(), TimeUnit.SECONDS);
		Cookie cookie = new Cookie("auth", auth);
		cookie.setMaxAge((int) TIMEOUT.getSeconds());
		response.addCookie(cookie);

		model.addAttribute("from", 0);
		model.addAttribute("to", 20);

		SimpleSecurity.setUser(user.getUsername());
		return "redirect:/auth/timeline";
	}
	
	@RequestMapping(value = {"/logout", "/auth/logout"}, method = RequestMethod.GET)
	public String logout() {
		if (SimpleSecurity.isSignedIn()) {
			repository.deleteAuth(SimpleSecurity.getName());
		}
		return "redirect:/login";
	}
}