package com.github.truthanytime.bootforum.jsf.converter;

import javax.faces.convert.EnumConverter;

import org.springframework.stereotype.Component;

import com.github.truthanytime.bootforum.enumeration.AccountStatus;
import com.github.truthanytime.bootforum.enumeration.UserRole;

@Component("enumConverters")
public class EnumConverters {

	private EnumConverter userRole = new EnumConverter(UserRole.class);
	
	public EnumConverter getUserRole() {
		return this.userRole;
	}
	
	private EnumConverter accountStatus = new EnumConverter(AccountStatus.class);
	
	public EnumConverter getAccountStatus() {
		return this.accountStatus;
	}
}
