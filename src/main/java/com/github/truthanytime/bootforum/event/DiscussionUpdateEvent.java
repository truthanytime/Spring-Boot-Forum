package com.github.truthanytime.bootforum.event;

import org.springframework.context.ApplicationEvent;

import com.github.truthanytime.bootforum.domain.Discussion;

public class DiscussionUpdateEvent extends ApplicationEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Discussion discussion;

	public DiscussionUpdateEvent(Object source, Discussion discussion) {
		super(source);
		this.discussion = discussion;
	}

	public Discussion getDiscussion() {
		return discussion;
	}
	public void setDiscussion(Discussion discussion) {
		this.discussion = discussion;
	}
}
