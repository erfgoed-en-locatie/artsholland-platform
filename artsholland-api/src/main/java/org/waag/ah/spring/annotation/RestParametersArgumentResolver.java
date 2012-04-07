package org.waag.ah.spring.annotation;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;
import org.waag.ah.rest.RESTParametersImpl;

public class RestParametersArgumentResolver implements WebArgumentResolver {

	public Object resolveArgument(MethodParameter methodParameter,
			NativeWebRequest nativeWebRequest) throws Exception {
		RestRequestParameters restParams = methodParameter
				.getParameterAnnotation(RestRequestParameters.class);

		if (restParams != null) {
			HttpServletRequest request = (HttpServletRequest) nativeWebRequest
					.getNativeRequest();
			
			Map<String, String[]> paramMap = request.getParameterMap();
			RESTParametersImpl params = new RESTParametersImpl();
			
			if (paramMap.containsKey("lang") && paramMap.get("lang").length == 1) {
				params.setLanguageTag(paramMap.get("lang")[0]);
			} else {
				params.setLanguageTag(request.getLocale().getLanguage());
			}

			params.setURIParameterMap(request.getParameterMap());

			int startIndex = 0;
			if (restParams.urlStartIndex() > 0) {
				startIndex = request.getRequestURI().indexOf("/", 1)+1;
			}
			params.setURIPathParts(request.getRequestURI().substring(startIndex));
			
			if (restParams.paging() == true) {
				params.setResultLimit(getLongValue(paramMap, "limit"));
				params.setPage(getLongValue(paramMap, "page"));
			}
			
			params.setDateFrom(getStringValue(paramMap, "dateFrom"));
			params.setDateTo(getStringValue(paramMap, "dateTo"));
			
			return params;
		}
		return UNRESOLVED;
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
