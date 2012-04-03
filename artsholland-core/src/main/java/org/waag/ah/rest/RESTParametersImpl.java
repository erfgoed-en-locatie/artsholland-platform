package org.waag.ah.rest;

import java.util.LinkedList;
import java.util.Map;


public class RESTParametersImpl implements RESTParameters {
	private long resultLimit = 10;
	private long page = 1;
	private String languageTag = "nl";	
	LinkedList<String> uriPathParts = new LinkedList<String>();
	private Map<String, String[]> parameterMap;
	
	public void setResultLimit(long resultLimit) {
		this.resultLimit = resultLimit;
	}
	
	@Override
	public long getResultLimit() {
		return resultLimit;
	}

	@Override
	public long getPage() {
		return page;
	}

	public void setPage(long page) {
		this.page = page;
	}

	@Override
	public String getLanguageTag() {
		return languageTag ;		
	}
	
	public void setLanguageTag(String languageTag) {
		this.languageTag = languageTag;		
	}

	@Override
	public LinkedList<String> getURIPathParts() {
		return uriPathParts;
	}
	
	public void setURIPathParts(LinkedList<String> uriPathParts) {
		this.uriPathParts = uriPathParts;
	}

	@Override
	public Map<String, String[]> getURIParameterMap() {
		return parameterMap;		
	}
	
	public void setURIParameterMap(Map<String, String[]> parameterMap) {
		this.parameterMap = parameterMap;		
	}

}
