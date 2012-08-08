package org.waag.ah.spring.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.memory.UserAttribute;
import org.springframework.util.Assert;
import org.waag.ah.spring.model.AppImpl;

public class ApiUserDetailsService implements UserDetailsService {
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(ApiUserDetailsService.class);
	private Properties userProperties;
	
	@Autowired
	private AppService appService;
	
	public ApiUserDetailsService(Properties userProperties) {
		super();
		Assert.notNull(userProperties, "UserProperties cannot be null");
		this.userProperties = userProperties;
	}
	
	@Override
	public UserDetails loadUserByUsername(String apiKey)
			throws UsernameNotFoundException {
		String userPropsValue = userProperties.getProperty(apiKey);

		if (userPropsValue == null) {
			throw new UsernameNotFoundException(apiKey);
		}
		
		AppImpl app = appService.findByApiKey(apiKey);		
		
		List<String> authorities = new ArrayList<String>();
		authorities.add(app.getRole());
		UserAttribute userAttrib = new UserAttribute();		
		userAttrib.setAuthoritiesAsString(authorities);
		userAttrib.setPassword(apiKey);
		
		UserDetails user = new User(apiKey, 
				userAttrib.getPassword(), 
				userAttrib.isEnabled(), 
				true, 
				true, 
				true, 
				userAttrib.getAuthorities());
		return user;
	}
}
