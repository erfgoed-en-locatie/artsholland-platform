package org.waag.ah.spring.service;

import java.util.ArrayList;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URIException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waag.ah.PlatformConfig;

@Service("uitbaseService")
public class UitbaseService extends ProxyService implements InitializingBean  {
	private Logger logger = Logger.getLogger(UitbaseService.class);
	
	private String UITBASE_ENDPOINT;
	private String UITBASE_APIKEY;		

	@Autowired
	PlatformConfig platformConfig;

	@Override
	public void afterPropertiesSet() throws Exception {
		UITBASE_ENDPOINT = platformConfig.getString("importer.source.uitbase.v4.endpoint");
		UITBASE_APIKEY = platformConfig.getString("importer.source.uitbase.v4.apiKey");
		setBaseUrl(UITBASE_ENDPOINT);
	}
	
	@Override
	protected void beforeProxyRequest(HttpServletRequest request, HttpServletResponse response,
			HttpMethod method) {
		
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {

			String headerName = headerNames.nextElement();
			
			if (!"Host".equalsIgnoreCase(headerName)) {
				method.setRequestHeader(headerName, request.getHeader(headerName));
			}
		}
		
		Enumeration<String> paramNames = request.getParameterNames();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		while (paramNames.hasMoreElements()) { 
			String paramName = paramNames.nextElement();
			
			// Copy all except apiKey parameter to proxy request:
			if (!"apiKey".equalsIgnoreCase(paramName)) {				
				params.add(new NameValuePair(paramName, request.getParameter(paramName)));				
			}	
		}
		
		params.add(new NameValuePair("key", UITBASE_APIKEY));		
		
		NameValuePair paramsArray[] = new NameValuePair[params.size()];		
		method.setQueryString(params.toArray(paramsArray));	
		try {
			logger.info("URI: "+method.getURI());
		} catch (URIException e) {
		}
	}

	@Override	
	protected void afterProxyRequest(HttpServletRequest request, HttpServletResponse response,
			HttpMethod method) {
		
		// Set the content type, as it comes from the server
		Header[] headers = method.getResponseHeaders();
		for (Header header : headers) {
			if (
					!"Host".equalsIgnoreCase(header.getName()) && 
					!"Transfer-Encoding".equalsIgnoreCase(header.getName())) {
//				logger.info("Copying header: "+header.getName()+" : "+header.getValue());
				response.setHeader(header.getName(), header.getValue());
			}		
		}		
	}
}
