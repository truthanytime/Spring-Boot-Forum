package com.github.chipolaris.bootforum.event;

import org.springframework.context.ApplicationEvent;

import com.github.chipolaris.bootforum.domain.User;

public class UserRegistrationEvent extends ApplicationEvent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private User user;

	public UserRegistrationEvent(Object source, User user) {
		super(source);
		this.setUser(user);
	}

	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
}
