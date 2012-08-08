package org.waag.ah.spring.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
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
	protected List<Header> getProxyHeaders(List<Header> requestHeaders) {		
		List<Header> proxyHeaders = new ArrayList<Header>();
		for (Header requestHeader : requestHeaders) {			
			if (!"Host".equalsIgnoreCase(requestHeader.getName())) {
				proxyHeaders.add(new BasicHeader(requestHeader.getName(), requestHeader.getValue()));
			}
		}
		return proxyHeaders;		
	}

	@Override
	protected List<NameValuePair> getProxyParameters(
			List<NameValuePair> requestParameters) {
		List<NameValuePair> proxyParameters = new ArrayList<NameValuePair>();
		for (NameValuePair proxyParameter : proxyParameters) {			
			if (!"apiKey".equalsIgnoreCase(proxyParameter.getName())) {
				proxyParameters.add(new BasicNameValuePair(proxyParameter.getName(), proxyParameter.getValue()));
			}
		}
		proxyParameters.add(new BasicNameValuePair("key", UITBASE_APIKEY));
		return proxyParameters;		
	}

	@Override
	protected List<Header> getResponseHeaders(List<Header> proxyHeaders) {
		List<Header> responseHeaders = new ArrayList<Header>();
		for (Header proxyHeader : proxyHeaders) {			
			if (!"Host".equalsIgnoreCase(proxyHeader.getName()) && 
					!"Transfer-Encoding".equalsIgnoreCase(proxyHeader.getName())) {
				responseHeaders.add(new BasicHeader(proxyHeader.getName(), proxyHeader.getValue()));
			}
		}
		return responseHeaders;	
	}
	
}
