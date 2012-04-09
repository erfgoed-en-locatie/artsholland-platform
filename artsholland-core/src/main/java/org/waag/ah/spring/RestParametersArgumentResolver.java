package org.waag.ah.spring;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.waag.ah.rest.RESTParameters;
import org.waag.ah.rest.RESTParametersImpl;
import org.waag.ah.spring.annotation.RestRequestParameters;

public class RestParametersArgumentResolver implements
		HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return RESTParameters.class.isAssignableFrom(parameter.getParameterType());
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
			RESTParametersImpl params = new RESTParametersImpl();
			
			if (paramMap.containsKey("lang") && paramMap.get("lang").length == 1) {
				params.setLanguageTag(paramMap.get("lang")[0]);
			} else {
				params.setLanguageTag(request.getLocale().getLanguage());
			}

			params.setURIParameterMap(request.getParameterMap());
			params.setURIPathParts(normalizeRequestUri(request, restParams));
			
			if (restParams.paging() == true) {
				params.setResultLimit(getLongValue(paramMap, "limit"));
				params.setPage(getLongValue(paramMap, "page"));
			}
			
			params.setDateFrom(getStringValue(paramMap, "dateFrom"));
			params.setDateTo(getStringValue(paramMap, "dateTo"));
			
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
	
	/**
	 * Return requested value from map as long.
	 *  
	 * @param paramMap
	 * @param key
	 * @return long
	 *
	 * @author	Raoul Wissink <raoul@raoul.net>
	 * @since	Apr 7, 2012
	 */
	private long getLongValue(Map<String, String[]> paramMap, String key) {
		if (paramMap.containsKey(key) && paramMap.get(key).length == 1) {
			return new Integer(paramMap.get(key)[0]).longValue();
		}
		return 0l;
	}
	
	/**
	 * Return requested value from map as string.
	 * 
	 * @param paramMap
	 * @param key
	 * @return
	 *
	 * @author	Raoul Wissink <raoul@raoul.net>
	 * @since	Apr 7, 2012
	 */
	private String getStringValue(Map<String, String[]> paramMap, String key) {
		if (paramMap.containsKey(key) && paramMap.get(key).length == 1) {
			return paramMap.get(key)[0];
		}
		return null;
	}
}
