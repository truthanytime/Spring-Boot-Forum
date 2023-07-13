package com.github.chipolaris.bootforum.jsf.bean;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.github.chipolaris.bootforum.domain.Comment;
import com.github.chipolaris.bootforum.domain.Discussion;
import com.github.chipolaris.bootforum.jsf.util.JSFUtils;
import com.github.chipolaris.bootforum.service.GenericService;
import com.github.chipolaris.bootforum.service.IndexService;

@Component @Scope("view")
public class SearchComment {
	
	private Logger logger = LoggerFactory.getLogger(SearchComment.class);
	
	@Resource
	private IndexService indexService;
	
	@Resource
	private GenericService genericService;
	
	private CommentSearchLazyModel commentSearchLazyModel;
	
	public CommentSearchLazyModel getCommentSearchLazyModel() {
		return commentSearchLazyModel;
	}
	public void setCommentSearchLazyModel(CommentSearchLazyModel commentSearchLazyModel) {
		this.commentSearchLazyModel = commentSearchLazyModel;
	}
	
	private String keywords;

	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	
	public void onLoad() {
		
		if(this.keywords != null) {
			
			this.commentSearchLazyModel = 
					new CommentSearchLazyModel(this.indexService, this.keywords);
		}
	}
	
	public void search() {
		
		logger.info("Searching for " + this.keywords);
		
		if("".equals(this.keywords)) {
			JSFUtils.addErrorStringMessage("messages", "Please enter a keyword to search");
			return;
		}
		
		this.commentSearchLazyModel = 
			new CommentSearchLazyModel(this.indexService, this.keywords);
	}
	
	public void viewSelectedComment() {
		this.selectedComment = 
			genericService.findEntity(Comment.class, selectedComment.getId()).getDataObject();
	}
	
	public Discussion getDiscussion(Long discussionId) {
		
		return genericService.getEntity(Discussion.class, discussionId).getDataObject();
	}
	
	// 
	private Comment selectedComment;
	
	public Comment getSelectedComment() {
		return selectedComment;
	}
	public void setSelectedComment(Comment selectedComment) {
		this.selectedComment = selectedComment;
	}
}
