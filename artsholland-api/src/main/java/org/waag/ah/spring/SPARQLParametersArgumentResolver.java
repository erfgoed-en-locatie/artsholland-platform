package org.waag.ah.spring;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.waag.ah.rest.SPARQLParameters;
import org.waag.ah.spring.annotation.SPARQLRequestParameters;

public class SPARQLParametersArgumentResolver extends 
	AHParametersArgumentResolver {
	final static Logger logger = LoggerFactory
			.getLogger(SPARQLParametersArgumentResolver.class);
	
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return SPARQLParameters.class.isAssignableFrom(parameter.getParameterType());
	}	
	
	@Override
	public Object resolveArgument(MethodParameter parameter,
			ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
			WebDataBinderFactory binderFactory) throws Exception {
		SPARQLRequestParameters sparqlParams = parameter
				.getParameterAnnotation(SPARQLRequestParameters.class);

		if (sparqlParams != null) {
			HttpServletRequest request = (HttpServletRequest) webRequest
					.getNativeRequest();
			
			Map<String, String[]> paramMap = request.getParameterMap();
			SPARQLParameters params = new SPARQLParameters();
			
			params.setQuery(getStringValue(paramMap, "query"));
			params.setJSONPCallback(getStringValue(paramMap, "callback"));			
			params.setPlainText(getBooleanValue(paramMap, "plaintext"));
			
			return params;
		}
		return null;
	}	
	
	
}
