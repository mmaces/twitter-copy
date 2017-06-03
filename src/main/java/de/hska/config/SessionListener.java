package de.hska.config;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class SessionListener implements HttpSessionListener{

	@Override
	public void sessionCreated(HttpSessionEvent event) {
		System.out.println("Session created!");
		event.getSession().setMaxInactiveInterval(5);
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		System.out.println("Session destroyed!");
		
	}

}
