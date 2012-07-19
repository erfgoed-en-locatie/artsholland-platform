package org.waag.ah.spring.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.util.RequestMatcher;

public class ApiKeyRequestMatcher implements RequestMatcher {

	@Override
	public boolean matches(HttpServletRequest request) {
		return request.getParameter(ApiKeyServices.SPRING_SECURITY_API_KEY) != null;
	}
}
