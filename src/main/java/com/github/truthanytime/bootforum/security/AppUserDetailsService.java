package com.github.truthanytime.bootforum.security;

import java.util.Collections;

import javax.annotation.Resource;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.truthanytime.bootforum.dao.GenericDAO;
import com.github.truthanytime.bootforum.domain.User;

@Service
@Transactional
public class AppUserDetailsService implements UserDetailsService {

	@Resource
	private GenericDAO genericDAO;
	
	@Override
	@Transactional(readOnly=true)
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		
		User user = genericDAO.getEntity(User.class, Collections.singletonMap("username", username));
		
		if(user == null) {
			throw new UsernameNotFoundException("Can't find username: " + username);
		}
		
		return new AppUserDetails(user);
	}
}
