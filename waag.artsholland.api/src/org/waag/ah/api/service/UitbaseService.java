package org.waag.ah.api.service;

import java.util.ArrayList;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.jboss.logging.Logger;
import org.springframework.stereotype.Service;

@Service("uitbaseService")
public class UitbaseService extends ProxyService {

	private Logger logger = Logger.getLogger(UitbaseService.class);

	// Versie 3
	//private final String UITBASE_ENDPOINT = "http://test.publisher.uitburo.nl/agenda/search.do";
	//private final String UITBASE_APIKEY = "a9d8fc27e5cbde7bca8402b53fe5a725";
	
	private final String UITBASE_ENDPOINT = "http://accept.ps4.uitburo.nl/api/";
	private final String UITBASE_APIKEY = "505642b12881b9a60688411a333bc78b";		

	public UitbaseService() {
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
			
			/*
			if ("Accept".equalsIgnoreCase(headerName)) {
				method.setRequestHeader(headerName, request.getHeader(headerName));
			}
			*/

		}
		
		Enumeration<String> paramNames = request.getParameterNames();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		while (paramNames.hasMoreElements()) { 
			String paramName = paramNames.nextElement();
			
			if ("key".equalsIgnoreCase(paramName)) {
				/*
				 * TODO: check if key is valid ArtsHolland API key, etc, etc, etc.
				 */
			} else {
				params.add(new NameValuePair(paramName, request.getParameter(paramName)));				
			}	
		}
		
		params.add(new NameValuePair("key", UITBASE_APIKEY));		
		
		NameValuePair paramsArray[] = new NameValuePair[params.size()];		
		method.setQueryString(params.toArray(paramsArray));	
		
	}

	@Override	
	protected void afterProxyRequest(HttpServletRequest request, HttpServletResponse response,
			HttpMethod method) {
		
		// Set the content type, as it comes from the server
		Header[] headers = method.getResponseHeaders();
		for (Header header : headers) {
			//response.setHeader(header.getName(), header.getValue());
				
			if (!"Host".equalsIgnoreCase(header.getName())) {
				response.setHeader(header.getName(), header.getValue());
			}	
				
			/*
			if ("Content-Type".equalsIgnoreCase(header.getName())) {
				response.setContentType(header.getValue());
			}	*/		
		}		
	}

}
