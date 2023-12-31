package com.github.truthanytime.bootforum.event;

import org.springframework.context.ApplicationEvent;

import com.github.truthanytime.bootforum.domain.ForumGroup;

public class ForumGroupAddEvent extends ApplicationEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ForumGroup forumGroup;
	
	public ForumGroupAddEvent(Object source, ForumGroup forumGroup) {
		super(source);
		this.forumGroup = forumGroup;
	}

	public ForumGroup getForumGroup() {
		return forumGroup;
	}
	public void setForumGroup(ForumGroup forumGroup) {
		this.forumGroup = forumGroup;
	}
}
