package org.waag.spring.service;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.memory.UserAttribute;
import org.springframework.security.core.userdetails.memory.UserAttributeEditor;
import org.springframework.util.Assert;

public class ApiUserDetailsService implements UserDetailsService {
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(ApiUserDetailsService.class);
	private Properties userProperties;
	
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
		
		UserAttributeEditor configAttribEd = new UserAttributeEditor();
		configAttribEd.setAsText(userPropsValue);
		UserAttribute userAttributes = (UserAttribute) configAttribEd.getValue(); 
		
		UserDetails user = new User(apiKey, 
				userAttributes.getPassword(), 
				userAttributes.isEnabled(), 
				true, 
				true, 
				true, 
				userAttributes.getAuthorities());
		return user;
	}
}
