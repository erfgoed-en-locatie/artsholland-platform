package org.waag.ah.spring;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.waag.ah.rest.RestParameters;
import org.waag.ah.spring.annotation.RestRequestParameters;

public class RestParametersArgumentResolver extends 
	AHParametersArgumentResolver {
	final static Logger logger = LoggerFactory
			.getLogger(RestParametersArgumentResolver.class);
	
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return RestParameters.class.isAssignableFrom(parameter.getParameterType());
	}
	
	@Override
	public Object resolveArgument(MethodParameter parameter,
			ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
			WebDataBinderFactory binderFactory) throws Exception {
		RestRequestParameters restParams = parameter
				.getParameterAnnotation(RestRequestParameters.class);

		if (restParams != null) {
			HttpServletRequest request = (HttpServletRequest) webRequest
					.getNativeRequest();
			
			Map<String, String[]> paramMap = request.getParameterMap();
			RestParameters params = new RestParameters();
			
			params.setLanguageTag(getStringValue(paramMap, "lang"));
			if (params.getLanguageTag() == null) {
				params.setLanguageTag(request.getLocale().getLanguage());
			}			
			params.setJSONPCallback(getStringValue(paramMap, "callback"));
			
			params.setURIParameterMap(request.getParameterMap());
			params.setURIPathParts(normalizeRequestUri(request, restParams));
			
			if (restParams.paging() == true) {
				params.setPerPage(getLongValue(paramMap, "per_page"));
				params.setPage(getLongValue(paramMap, "page"));
				//params.setPagedQuery(params.getPage() > 0);
			}
			
			params.setOrdered(getBooleanValue(paramMap, "ordered"));
			params.setPretty(getBooleanValue(paramMap, "pretty"));
			params.setPlainText(getBooleanValue(paramMap, "plaintext"));
			params.setCountTotals(getBooleanValue(paramMap, "count"));
			
//			logger.info("PRETTY: " + params.getPretty());
			
			return params;
		}
		return null;
	}
	
	private String normalizeRequestUri(HttpServletRequest request,
			RestRequestParameters params) {
		String url =  request.getRequestURI();
		int startIndex = 0;
		if (params.prefixLength() > 0) {
			startIndex = url.indexOf("/", 1)+1;
		}
		url = request.getRequestURI().substring(startIndex);
		url = url.replaceFirst("\\.[a-zA-Z]+$", "");
		return url;
	}
	
}
