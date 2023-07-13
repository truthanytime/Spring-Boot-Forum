package com.github.chipolaris.bootforum.event;

import org.springframework.context.ApplicationEvent;

import com.github.chipolaris.bootforum.domain.Forum;

public class ForumDeleteEvent extends ApplicationEvent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Forum forum;
	
	public ForumDeleteEvent(Object source, Forum forum) {
		super(source);
		this.forum = forum;
	}

	public Forum getForum() {
		return forum;
	}
	public void setForum(Forum forum) {
		this.forum = forum;
	}
}
