package com.github.truthanytime.bootforum.jsf.bean;

import com.github.truthanytime.bootforum.domain.User;
import com.github.truthanytime.bootforum.security.AppUserDetails;

public interface LoggedOnSession {

	void initialize(AppUserDetails appUserDetails);

	User getUser();

}