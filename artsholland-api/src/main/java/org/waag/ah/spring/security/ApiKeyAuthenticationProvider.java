package org.waag.ah.spring.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.util.Assert;

public class ApiKeyAuthenticationProvider 
		implements AuthenticationProvider, InitializingBean, MessageSourceAware {
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(ApiKeyAuthenticationProvider.class);
	protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(this.messages, "A message source must be set");
	}

    public Authentication authenticate(Authentication authentication) 
    		throws AuthenticationException {
        if (!supports(authentication.getClass())) {
            return null;
        }
        if (authentication.getPrincipal() == null) {
        	throw new BadCredentialsException(messages.getMessage("ApiKeyAuthenticationProvider.incorrectKey",
                  "The presented ApiKeyAuthenticationToken does not contain a valid key"));
        }
        return authentication;
    }
    
	@Override
	public boolean supports(Class<?> authentication) {
		return (ApiKeyAuthenticationToken.class.isAssignableFrom(authentication));
	}

	@Override
	public void setMessageSource(MessageSource messageSource) {
		this.messages = new MessageSourceAccessor(messageSource);
	}
}
