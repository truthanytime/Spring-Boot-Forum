package com.github.chipolaris.bootforum.jsf.bean;

import java.io.IOException;

import javax.annotation.Resource;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.github.chipolaris.bootforum.domain.Comment;
import com.github.chipolaris.bootforum.domain.CommentOption;
import com.github.chipolaris.bootforum.domain.FileInfo;
import com.github.chipolaris.bootforum.event.CommentEditEvent;
import com.github.chipolaris.bootforum.event.CommentFileEvent;
import com.github.chipolaris.bootforum.jsf.util.JSFUtils;
import com.github.chipolaris.bootforum.service.AckCodeType;
import com.github.chipolaris.bootforum.service.CommentService;
import com.github.chipolaris.bootforum.service.GenericService;
import com.github.chipolaris.bootforum.service.ServiceResponse;
import com.github.chipolaris.bootforum.service.SystemConfigService;
import com.github.chipolaris.bootforum.service.UploadedFileData;

@Component
@Scope("view")
public class EditComment {

	private static final Logger logger = LoggerFactory.getLogger(EditComment.class);
	
	@Resource
	private GenericService genericService;
	
	@Resource
	private CommentService commentService;
	
	@Resource
	private SystemConfigService systemConfigService;
	
	@Resource
	private LoggedOnSession userSession;
	
	@Resource
	private ApplicationEventPublisher applicationEventPublisher;
	
	private CommentOption commentOption;
	
	public CommentOption getCommentOption() {
		return commentOption;
	}
	
	private Long id;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	private String loadingErrorMessage;
	public String getLoadingErrorMessage() {
		return loadingErrorMessage;
	}
	public void setLoadingErrorMessage(String loadingErrorMessage) {
		this.loadingErrorMessage = loadingErrorMessage;
	}

	private Comment comment;
	public Comment getComment() {
		return comment;
	}
	public void setComment(Comment comment) {
		this.comment = comment;
	}
	
	private boolean isFirstComment;
	public boolean isFirstComment() {
		return isFirstComment;
	}
	
	public void onLoad() {
		
		this.comment = genericService.getEntity(Comment.class, id).getDataObject();
		
		if(this.comment != null) {
			if(this.comment.getDiscussion().isClosed()) {
				this.setLoadingErrorMessage(JSFUtils.getMessageBundle().getString("discussion.is.closed"));
				
				this.comment = null;
			}
			else if(!this.comment.getCreateBy().equals(userSession.getUser().getUsername())) {
				
				this.setLoadingErrorMessage(JSFUtils.getMessageBundle().getString("comment.cannot.be.edited.by.current.user"));
				
				this.comment = null;
			}
			else {
				this.isFirstComment = commentService.isFirstComment(comment).getDataObject();
				this.commentOption = systemConfigService.getCommentOption().getDataObject();
			}
		}
	}
	
	public String submit(boolean isContinue) {
		
		logger.info("Edit comment (id): " + comment.getId());
		
		comment.setIpAddress(JSFUtils.getRemoteIPAddress());
		ServiceResponse<Comment> serviceResponse = genericService.updateEntity(comment);
		
		if(serviceResponse.getAckCode() == AckCodeType.FAILURE) {
			JSFUtils.addServiceErrorMessage(null, serviceResponse);
			return null;
		}
		else {
			JSFUtils.addInfoStringMessage(null, "Comment updated");
			this.comment = serviceResponse.getDataObject();
			
			this.applicationEventPublisher.publishEvent(new CommentEditEvent(this, comment));
			
			if(isContinue) {
				return null;
			}
			
			return "/discussion?faces-redirect=true&id=" + this.comment.getDiscussion().getId(); 
		}
	}
	
	public void uploadThumbnail(FileUploadEvent event) {
		
		int maxThumbnailCount = this.isFirstComment ? 
				commentOption.getMaxDiscussionThumbnail() : commentOption.getMaxCommentThumbnail();
		
		if(this.comment.getThumbnails().size() < maxThumbnailCount) {
			
			UploadedFile uploadedFile = event.getFile(); 
			
			int maxThumbnailSize = this.isFirstComment ? 
					commentOption.getMaxByteDiscussionThumbnail() : commentOption.getMaxByteCommentThumbnail();
			
			if(uploadedFile.getSize() > maxThumbnailSize) {
				JSFUtils.addErrorStringMessage("messages", 
						String.format("Can't upload file of over %d bytes", maxThumbnailSize));
			}
			else {
				UploadedFileData uploadedFileData = toUploadedFileData(uploadedFile);			
				
				ServiceResponse<Comment> serviceResponse = commentService.addCommentThumbnail(this.comment, uploadedFileData);
				
				if(serviceResponse.getAckCode() == AckCodeType.FAILURE) {
					JSFUtils.addErrorStringMessage(null, "Unable to upload thumbnail");
				}
				else {
					this.comment = serviceResponse.getDataObject();
					JSFUtils.addInfoStringMessage(null, "Thumbnail added");
					
					applicationEventPublisher.publishEvent(
							new CommentFileEvent(this, CommentFileEvent.Type.THUMBNAIL, 
									CommentFileEvent.Action.ADD, comment, userSession.getUser()));
				}
			}
		}
		else {
			JSFUtils.addErrorStringMessage("messages", 
					String.format("Can't upload file, maximum %d total files has been reached", maxThumbnailCount));
		}
	}
	
	public void uploadAttachment(FileUploadEvent event) {
		
		int maxAttachmentCount = this.isFirstComment ? 
				commentOption.getMaxDiscussionAttachment() : commentOption.getMaxCommentAttachment();
		
		if(this.comment.getAttachments().size() < maxAttachmentCount) {
			
			UploadedFile uploadedFile = event.getFile();
			
			int maxAttachmentSize = this.isFirstComment ?
					commentOption.getMaxByteDiscussionAttachment() : commentOption.getMaxByteCommentAttachment();
			
			if(uploadedFile.getSize() > maxAttachmentSize) {
				JSFUtils.addErrorStringMessage("messages", 
						String.format("Can't upload file of over %d bytes", maxAttachmentSize));
			}
			else {
				UploadedFileData uploadedFileData = toUploadedFileData(uploadedFile);
				
				ServiceResponse<Comment> serviceResponse = commentService.addCommentAttachment(this.comment, uploadedFileData);
				
				if(serviceResponse.getAckCode() == AckCodeType.FAILURE) {
					JSFUtils.addErrorStringMessage(null, "Unable to upload attachment");
				}
				else {
					this.comment = serviceResponse.getDataObject();
					JSFUtils.addInfoStringMessage(null, "Attachment added");
					
					applicationEventPublisher.publishEvent(
							new CommentFileEvent(this, CommentFileEvent.Type.ATTACHMENT, 
									CommentFileEvent.Action.ADD, comment, userSession.getUser()));
				}
			}
		}
		else {
			JSFUtils.addErrorStringMessage("messages", 
				String.format("Can't upload file, maximum %d total files has been reached", maxAttachmentCount));
		}
	}
	
	private UploadedFileData toUploadedFileData(UploadedFile uploadedFile) {
		String storedFilename = System.currentTimeMillis() + "." + FilenameUtils.getExtension(uploadedFile.getFileName());
		
		UploadedFileData uploadedFileData = new UploadedFileData();
		uploadedFileData.setFileName(storedFilename);
		uploadedFileData.setOrigFileName(uploadedFile.getFileName());
		uploadedFileData.setContentType(uploadedFile.getContentType());
		
		// for Servlet 2.5 & common io/upload, the following work:
		//		attachment.setBytes(uploadedFile.getContents());
		// however, for Servlet 3.0, must use IOUtils.toByteArray(uploadedFile.getInputstream())
		// to extract bytes content:
		//   http://stackoverflow.com/questions/18049671/pfileupload-always-give-me-null-contents
		// TODO:clean up the following code
		try {
			uploadedFileData.setContents(IOUtils.toByteArray(uploadedFile.getInputStream()));
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return uploadedFileData;
	}
	
	public void deleteThumbnail() {
		
		if(this.selectedThumbnail != null) {
			JSFUtils.addInfoStringMessage(null, "Delete thumbnail " + selectedThumbnail.getId());
			
			ServiceResponse<Boolean> serviceResponse = commentService.deleteCommentThumbnail(comment, selectedThumbnail);
			
			if(serviceResponse.getAckCode() != AckCodeType.FAILURE) {
				this.selectedThumbnail = null; // reset selectedThumbnail
				JSFUtils.addInfoStringMessage(null, "Thumbnail deleted");
				
				applicationEventPublisher.publishEvent(
						new CommentFileEvent(this, CommentFileEvent.Type.THUMBNAIL, 
								CommentFileEvent.Action.DELETE, comment, userSession.getUser()));
			}
			else {
				JSFUtils.addServiceErrorMessage(serviceResponse);
			}
		}
		else {
			JSFUtils.addErrorStringMessage(null, "No thumbnail is selected");
		}
	}
	
	public void deleteAttachment() {
		
		if(this.selectedAttachment != null) {
			JSFUtils.addInfoStringMessage(null, "Delete attachment " + selectedAttachment.getId());
			
			ServiceResponse<Boolean> serviceResponse = commentService.deleteCommentAttachment(comment, selectedAttachment);
			
			if(serviceResponse.getAckCode() != AckCodeType.FAILURE) {
				this.selectedAttachment = null; // reset selectedAttachment
				JSFUtils.addInfoStringMessage(null, "Attachment deleted");
				
				applicationEventPublisher.publishEvent(
						new CommentFileEvent(this, CommentFileEvent.Type.ATTACHMENT, 
								CommentFileEvent.Action.DELETE, comment, userSession.getUser()));
			}
			else {
				JSFUtils.addServiceErrorMessage(serviceResponse);
			}
		}
		else {
			JSFUtils.addErrorStringMessage(null, "No attachment is selected");
		}
	}
	
	private FileInfo selectedThumbnail;
	private FileInfo selectedAttachment;
	
	public FileInfo getSelectedThumbnail() {
		return selectedThumbnail;
	}
	public void setSelectedThumbnail(FileInfo selectedThumbnail) {
		this.selectedThumbnail = selectedThumbnail;
	}
	public FileInfo getSelectedAttachment() {
		return selectedAttachment;
	}
	public void setSelectedAttachment(FileInfo selectedAttachment) {
		this.selectedAttachment = selectedAttachment;
	}
}
