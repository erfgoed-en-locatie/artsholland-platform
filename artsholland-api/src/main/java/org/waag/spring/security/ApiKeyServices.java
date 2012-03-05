package org.waag.spring.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.Assert;

public class ApiKeyServices implements RememberMeServices, InitializingBean {
	private static final Logger logger = LoggerFactory.getLogger(ApiKeyServices.class);
	public static final String SPRING_SECURITY_API_KEY = "apiKey";
	
	private UserDetailsService userDetailsService;
	private UserDetailsChecker userDetailsChecker = new AccountStatusUserDetailsChecker();
	private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();
	private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();

	public ApiKeyServices(UserDetailsService userDetailsService) {
        Assert.notNull(userDetailsService, "UserDetailsService cannot be null");
        this.userDetailsService = userDetailsService;
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(userDetailsService, "A UserDetailsService is required");
    }
    
    public final Authentication autoLogin(HttpServletRequest request, HttpServletResponse response) {
    	String apiKey = obtainApiKey(request);
        if (apiKey == null) {
            return null;
        }
        
        UserDetails user = null;

        try {
            user = userDetailsService.loadUserByUsername(apiKey);
            userDetailsChecker.check(user);
            return createSuccessfulAuthentication(request, user);
        } catch (AccountStatusException statusInvalid) {
            logger.debug("Invalid UserDetails: " + statusInvalid.getMessage());
        }

        return null;
    }

	@Override
	public void loginFail(HttpServletRequest request,
			HttpServletResponse response) {
	}

	@Override
	public void loginSuccess(HttpServletRequest request,
			HttpServletResponse response,
			Authentication successfulAuthentication) {
	}

	protected String obtainApiKey(HttpServletRequest request) {
		return request.getParameter(SPRING_SECURITY_API_KEY);
	}
	
    protected Authentication createSuccessfulAuthentication(HttpServletRequest request, UserDetails user) {
    	ApiKeyAuthenticationToken auth = new ApiKeyAuthenticationToken(user,
                authoritiesMapper.mapAuthorities(user.getAuthorities()));
        auth.setDetails(authenticationDetailsSource.buildDetails(request));
        return auth;
    }	
}
