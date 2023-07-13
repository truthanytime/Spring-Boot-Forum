package com.github.chipolaris.bootforum.jsf.bean;

import java.text.MessageFormat;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.github.chipolaris.bootforum.domain.Comment;
import com.github.chipolaris.bootforum.domain.CommentOption;
import com.github.chipolaris.bootforum.domain.Discussion;
import com.github.chipolaris.bootforum.domain.DiscussionStat;
import com.github.chipolaris.bootforum.domain.Forum;
import com.github.chipolaris.bootforum.event.DiscussionAddEvent;
import com.github.chipolaris.bootforum.jsf.util.JSFUtils;
import com.github.chipolaris.bootforum.service.AckCodeType;
import com.github.chipolaris.bootforum.service.DiscussionService;
import com.github.chipolaris.bootforum.service.GenericService;
import com.github.chipolaris.bootforum.service.ServiceResponse;
import com.github.chipolaris.bootforum.service.SystemConfigService;

@Component("addDiscussion")
@Scope("view")
public class AddDiscussion {

	private static final Logger logger = LoggerFactory.getLogger(AddDiscussion.class);
	
	@Resource
	private GenericService genericService;
	
	@Resource
	private DiscussionService discussionService;
	
	@Resource
	private SystemConfigService systemConfigService;
	
	@Resource
	private LoggedOnSession userSession;
	
	@Resource
	private ApplicationEventPublisher applicationEventPublisher;
	
	private Long forumId;

	public Long getForumId() {
		return forumId;
	}
	public void setForumId(Long forumId) {
		this.forumId = forumId;
	}
	
	private Discussion discussion;
	private Comment comment;
	private Forum forum;

	private String loadingErrorMessage;
	
	public String getLoadingErrorMessage() {
		return loadingErrorMessage;
	}
	public void setLoadingErrorMessage(String loadingErrorMessage) {
		this.loadingErrorMessage = loadingErrorMessage;
	}
	
	private CommentOption commentOption;
	
	public CommentOption getCommentOption() {
		return commentOption;
	}
	public void setCommentOption(CommentOption commentOption) {
		this.commentOption = commentOption;
	}
	
	public Forum getForum() {
		return forum;
	}
	public void setForum(Forum forum) {
		this.forum = forum;
	}
	public Discussion getDiscussion() {
		return discussion;
	}

	public void setDiscussion(Discussion discussion) {
		this.discussion = discussion;
	}

	public Comment getComment() {
		return comment;
	}

	public void setComment(Comment comment) {
		this.comment = comment;
	}

	public void onLoad() {
		
		this.forum = genericService.getEntity(Forum.class, this.forumId).getDataObject();
		
		this.commentOption = systemConfigService.getCommentOption().getDataObject();

		if (this.forum != null) {

			if (!forum.isActive()) {
				this.setLoadingErrorMessage(JSFUtils.getMessageBundle().getString("forum.is.closed"));
				this.forum = null;
				return;
			}

			this.discussion = new Discussion();
			this.discussion.setStat(new DiscussionStat());
			this.discussion.setForum(forum);
			forum.getDiscussions().add(discussion);

			comment = new Comment();

			this.commentAttachmentManagement = new UploadedFileManager(
					commentOption.getMaxDiscussionAttachment(), commentOption.getMaxByteDiscussionAttachment());
			this.commentThumbnailManagement = new UploadedFileManager(
					commentOption.getMaxDiscussionThumbnail(), commentOption.getMaxByteDiscussionThumbnail());
		} 
		else {
			this.setLoadingErrorMessage(JSFUtils.getMessageResource("forum.not.found"));
		}
	}
	
	public String submit() {
		
		logger.info("add discussion for " + forum.getTitle());
		
		comment.setIpAddress(JSFUtils.getRemoteIPAddress());
		ServiceResponse<Discussion> response = 
				discussionService.addDiscussion(discussion, comment, userSession.getUser(),
						commentThumbnailManagement.getUploadedFileList(), commentAttachmentManagement.getUploadedFileList());
		
		if(response.getAckCode() == AckCodeType.SUCCESS) {
		
			JSFUtils.addInfoStringMessage(null, 
					MessageFormat.format(JSFUtils.getMessageBundle().getString("new.discussion.added"), discussion.getTitle()));
			
			// publish DiscussionAddEvent for listeners to process
			applicationEventPublisher.publishEvent(new DiscussionAddEvent(this, this.discussion, userSession.getUser()));
			
			return "/discussion?faces-redirect=true&id=" + discussion.getId();
		}
		else {
		
			JSFUtils.addServiceErrorMessage(response);
			
			return  null;
		}
	}
	
	private UploadedFileManager commentAttachmentManagement;
	
	public UploadedFileManager getCommentAttachmentManagement() {
		return commentAttachmentManagement;
	}
	public void setCommentAttachmentManagement(UploadedFileManager commentAttachmentManagement) {
		this.commentAttachmentManagement = commentAttachmentManagement;
	}
	
	private UploadedFileManager commentThumbnailManagement;
	
	public UploadedFileManager getCommentThumbnailManagement() {
		return commentThumbnailManagement;
	}
	public void setCommentThumbnailManagement(UploadedFileManager commentThumbnailManagement) {
		this.commentThumbnailManagement = commentThumbnailManagement;
	}
	
	@PreDestroy
	public void preDestroy() {
		
		this.commentAttachmentManagement.cleanup();
		this.commentThumbnailManagement.cleanup();
	}

}

