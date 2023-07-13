package com.github.truthanytime.bootforum.event;

import org.springframework.context.ApplicationEvent;

import com.github.truthanytime.bootforum.domain.Comment;

public class CommentDeleteEvent extends ApplicationEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Comment comment;

	public CommentDeleteEvent(Object source, Comment comment) {
		super(source);
		this.comment = comment;
	}

	public Comment getComment() {
		return comment;
	}
	public void setComment(Comment comment) {
		this.comment = comment;
	}
}
