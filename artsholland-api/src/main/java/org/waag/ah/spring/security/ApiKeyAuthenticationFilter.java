package org.waag.ah.spring.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;

public class ApiKeyAuthenticationFilter extends GenericFilterBean implements ApplicationEventPublisherAware { 
	private static final Logger logger = LoggerFactory.getLogger(ApiKeyAuthenticationFilter.class);
	private AuthenticationManager authenticationManager;
	private ApiKeyServices apiKeyServices;
	private ApplicationEventPublisher eventPublisher;
	private AuthenticationSuccessHandler successHandler;
	
	public ApiKeyAuthenticationFilter(
			AuthenticationManager authenticationManager,
			ApiKeyServices apiKeyServices) {
		this.authenticationManager = authenticationManager;
		this.apiKeyServices = apiKeyServices;
	}

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(authenticationManager, "authenticationManager must be specified");
        Assert.notNull(apiKeyServices, "apiKeyServices must be specified");
    }
    
	@Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

    	try {
    		Authentication apiKeyAuth = apiKeyServices.autoLogin(request, response);
    		if (apiKeyAuth != null) {
	        	apiKeyAuth = authenticationManager.authenticate(apiKeyAuth);
	            SecurityContextHolder.getContext().setAuthentication(apiKeyAuth);
	            onSuccessfulAuthentication(request, response, apiKeyAuth);
	
	            if (logger.isDebugEnabled()) {
		            logger.debug("SecurityContextHolder populated with api key: '"
		                + SecurityContextHolder.getContext().getAuthentication() + "'");
	            }
	
	            if (this.eventPublisher != null) {
	                eventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(
	                        SecurityContextHolder.getContext().getAuthentication(), this.getClass()));
	            }
	
	            if (successHandler != null) {
	                successHandler.onAuthenticationSuccess(request, response, apiKeyAuth);
	                return;
	            }    
    		}
    	} catch (UsernameNotFoundException e) {
    		SecurityContextHolder.getContext().setAuthentication(null);
        } catch (AuthenticationException authenticationException) {
            if (logger.isDebugEnabled()) {
                logger.debug("SecurityContextHolder not populated with api key, as "
                        + "AuthenticationManager rejected Authentication returned by ApiKeyServices",
                        authenticationException);
            }
            apiKeyServices.loginFail(request, response);
            onUnsuccessfulAuthentication(request, response, authenticationException);
        }        

        chain.doFilter(request, response);
    }

	@Override
	public void setApplicationEventPublisher(
			ApplicationEventPublisher applicationEventPublisher) {
		this.eventPublisher = applicationEventPublisher;
	}
	
    protected void onSuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            Authentication authResult) {
    }

    protected void onUnsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException failed) {
    }
    
    public void setAuthenticationSuccessHandler(AuthenticationSuccessHandler successHandler) {
        Assert.notNull(successHandler, "successHandler cannot be null");
        this.successHandler = successHandler;
    }
}
