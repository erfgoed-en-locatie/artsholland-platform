package org.waag.ah.spring.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class DataUrlInterceptor extends HandlerInterceptorAdapter {
	final static Logger logger = LoggerFactory.getLogger(DataUrlInterceptor.class);
	
	@Autowired
	private PropertiesConfiguration config;

//	public DataUrlInterceptor(PropertiesConfiguration config) {
//		this.config = config;
//	}

	@Override
	public boolean preHandle(HttpServletRequest request,
	        HttpServletResponse response, Object handler) throws Exception {
		logger.info("INTERCEPTOR CALLED:"+request.getRequestURL());
	    if (request.getRequestURI().startsWith("/data/")) {
			String uri = request.getRequestURI().replaceFirst("^/data",
					config.getString("platform.baseUri"));
	        request.getRequestDispatcher(uri).forward(request, response);
	        return false;
	    } else {
	        return true;
	    }
	}
	
	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		logger.info("INTERCEPTOR CALLED: POSTHANDLE");
		super.postHandle(request, response, handler, modelAndView);
	}
}
