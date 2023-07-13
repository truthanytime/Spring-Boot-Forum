package com.github.truthanytime.bootforum.jsf.bean;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.github.truthanytime.bootforum.domain.AvatarOption;
import com.github.truthanytime.bootforum.jsf.util.JSFUtils;
import com.github.truthanytime.bootforum.service.SystemConfigService;

@Component @Scope("view")
public class ManageAvatarOption {

	private static final Logger logger = LoggerFactory.getLogger(ManageAvatarOption.class);
	
	@Resource
	private SystemConfigService systemConfigService;
	
	@Resource
	private LoggedOnSession userSession;
	
	private AvatarOption avatarOption;
	public AvatarOption getAvatarOption() {
		return avatarOption;
	}
	
	@PostConstruct
	private void init() {
		this.avatarOption = systemConfigService.getAvatarOption().getDataObject();
	}
	
	public void update() {
		
		logger.info("Update Comment Option");
		
		this.avatarOption.setUpdateBy(userSession.getUser().getUsername());
		systemConfigService.updateAvatarOption(this.avatarOption);
		
		JSFUtils.addInfoStringMessage(null, "Avatar Option Updated");
	}
}
