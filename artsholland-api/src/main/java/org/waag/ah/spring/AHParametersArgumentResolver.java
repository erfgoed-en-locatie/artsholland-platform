package org.waag.ah.spring;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

public abstract class AHParametersArgumentResolver implements
		HandlerMethodArgumentResolver {
	final static Logger logger = LoggerFactory
			.getLogger(AHParametersArgumentResolver.class);

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
	protected long getLongValue(Map<String, String[]> paramMap, String key) {
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
	protected String getStringValue(Map<String, String[]> paramMap, String key) {
		if (paramMap.containsKey(key) && paramMap.get(key).length == 1) {
			return paramMap.get(key)[0];
		}
		return null;
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
	protected boolean getBooleanValue(Map<String, String[]> paramMap, String key) {
		if (paramMap.containsKey(key)) {
			String value = paramMap.get(key)[0];
			return (value.equals("1") || value.toLowerCase().equals("true")) ? true : false;
		}
		return false;
	}
}
