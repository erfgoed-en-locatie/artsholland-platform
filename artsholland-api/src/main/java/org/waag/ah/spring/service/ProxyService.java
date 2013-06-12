package org.waag.ah.spring.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import javax.ejb.Singleton;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.net.io.Util;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

@Service("proxyService")
@Singleton
// @Service altijd Singleton?
abstract class ProxyService {
	private Logger logger = Logger.getLogger(ProxyService.class);

	private String baseUrl;

	private static DefaultHttpClient client;

	public ProxyService() {

		final ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager();
		final HttpRoute route = new HttpRoute(new HttpHost(
				"http://accept.ps4.uitburo.nl/"));
		cm.setDefaultMaxPerRoute(100);
		cm.setMaxForRoute(route, 100);
		cm.setMaxTotal(100);
		client = new DefaultHttpClient(cm);

	}

	abstract protected List<Header> getProxyHeaders(List<Header> requestHeaders);

	abstract protected List<NameValuePair> getProxyParameters(
			List<NameValuePair> requestParameters);

	abstract protected List<Header> getResponseHeaders(List<Header> proxyHeaders);

	public void proxyQuery(HttpServletRequest request,
			HttpServletResponse response, String path) throws IOException {

		HttpUriRequest proxyRequest;

		List<Header> proxyHeaders = getProxyHeaders(getHeaders(request));
		List<NameValuePair> proxyParameters = getProxyParameters(getParameters(request));

		String url = getUrl(path, proxyParameters);		

		logger.info("Proxying request: " + url);

		if (request.getMethod().equals("GET")) {
			proxyRequest = new HttpGet(url);
		} else if (request.getMethod().equals("POST")) {
			proxyRequest = new HttpPost(url);
		} else {
			throw new NotImplementedException(
					"This proxy only supports GET and POST methods.");
		}
		
		setHeaders(proxyRequest, proxyHeaders);

		HttpResponse proxyResponse = client.execute(proxyRequest);
		HttpEntity entity = proxyResponse.getEntity();

		List<Header> responseHeaders = getResponseHeaders(getHeaders(proxyResponse));
		setHeaders(response, responseHeaders);

		if (entity != null) {
			InputStream input = entity.getContent();
			try {
				ServletOutputStream output = response.getOutputStream();

				Util.copyStream(input, output);

				output.flush();
				output.close();
			} catch (IOException ex) {
				throw ex;
			} catch (RuntimeException ex) {
				proxyRequest.abort();
				throw ex;
			} finally {
				input.close();
			}
		}
	}

	private String getUrl(String path, List<NameValuePair> parameters) {				
		String url = getUrl(path);
		if (parameters.size() > 0) {
			url += "?" + URLEncodedUtils.format(parameters, "utf-8");
		}
		return url;
	}

	private void setHeaders(HttpUriRequest request, List<Header> headers) {
		request.setHeaders(headers.toArray(new BasicHeader[headers.size()]));
	}

	private void setHeaders(HttpServletResponse response,
			List<Header> headers) {
		for (Header header : headers) {
			response.setHeader(header.getName(), header.getValue());
		}
	}

	private List<Header> getHeaders(HttpResponse proxyResponse) {
		return Arrays.asList(proxyResponse.getAllHeaders());
	}

	private List<NameValuePair> getParameters(HttpServletRequest request) {
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();

		@SuppressWarnings("unchecked")
		Enumeration<String> paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String paramName = paramNames.nextElement();
			parameters.add(new BasicNameValuePair(paramName, request
					.getParameter(paramName)));
		}

		return parameters;
	}

	private List<Header> getHeaders(HttpServletRequest request) {
		List<Header> headers = new ArrayList<Header>();

		@SuppressWarnings("unchecked")
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String headerName = headerNames.nextElement();
			headers.add(new BasicHeader(headerName, request.getHeader(headerName)));
		}

		return headers;
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
