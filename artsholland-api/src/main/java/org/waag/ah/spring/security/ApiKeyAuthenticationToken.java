package org.waag.ah.spring.security;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class ApiKeyAuthenticationToken extends AbstractAuthenticationToken {
	private static final long serialVersionUID = 2705032794280937476L;
	private Object principal;
    
	public ApiKeyAuthenticationToken(Object principal,
			Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
        
        if ((principal == null) || "".equals(principal)) {
            throw new IllegalArgumentException("Cannot pass null or empty values to constructor");
        }
        
        this.principal = principal;
        setAuthenticated(true);
    }
    
	@Override
	public Object getCredentials() {
		return "";
	}

	@Override
	public Object getPrincipal() {
		return principal;
	}	
}
