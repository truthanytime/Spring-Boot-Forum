package com.github.truthanytime.bootforum.event;

import org.springframework.context.ApplicationEvent;

import com.github.truthanytime.bootforum.domain.RegistrationOption;

public class RegistrationOptionLoadEvent extends ApplicationEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private RegistrationOption registrationOption;

	public RegistrationOptionLoadEvent(Object source, RegistrationOption registrationOption) {
		super(source);
		this.setRegistrationOption(registrationOption);
	}

	public RegistrationOption getRegistrationOption() {
		return registrationOption;
	}
	public void setRegistrationOption(RegistrationOption registrationOption) {
		this.registrationOption = registrationOption;
	}
}
