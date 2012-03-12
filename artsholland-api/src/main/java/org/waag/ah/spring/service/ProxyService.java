package org.waag.ah.spring.service;

import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.stereotype.Service;

@Service("proxyService")
abstract class ProxyService {
//	private Logger logger = Logger.getLogger(ProxyService.class);

	private String baseUrl;
	private HttpMethod method;
	
	abstract protected void beforeProxyRequest(HttpServletRequest request,
			HttpServletResponse response, HttpMethod method);
	
	abstract protected void afterProxyRequest(HttpServletRequest request,
			HttpServletResponse response, HttpMethod method);	
	
	public void proxyQuery(HttpServletRequest request,
			HttpServletResponse response, String path) throws IOException {
ServletOutputStream output = response.getOutputStream();
		
		HttpClient client = new HttpClient();
				
		String url = getUrl(path);
		
		if (request.getMethod().equals("GET")) {

			method = new GetMethod(url);

		} else if (request.getMethod().equals("POST")) {

			method = new PostMethod(url);

		} else {

			throw new NotImplementedException(
					"This proxy only supports GET and POST methods.");
		}

		beforeProxyRequest(request, response, method);		
		
		// Execute the method
		client.executeMethod(method);

		afterProxyRequest(request, response, method);

		output.write(method.getResponseBody());
		output.flush();
		output.close();
	}

	public void proxyQuery(HttpServletRequest request,
			HttpServletResponse response) throws IOException {		

		ServletOutputStream output = response.getOutputStream();
		
		HttpClient client = new HttpClient();
				

		if (request.getMethod().equals("GET")) {

			method = new GetMethod(baseUrl);

		} else if (request.getMethod().equals("POST")) {

			method = new PostMethod(baseUrl);

		} else {

			throw new NotImplementedException(
					"This proxy only supports GET and POST methods.");
		}

		beforeProxyRequest(request, response, method);		
		
		// Execute the method
		client.executeMethod(method);

		afterProxyRequest(request, response, method);

		output.write(method.getResponseBody());
		output.flush();
		output.close();
	}
	
	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String url) {
		this.baseUrl = url;
	}
	
	private String getUrl(String path) {		
		return getBaseUrl() + path;
	}

}
