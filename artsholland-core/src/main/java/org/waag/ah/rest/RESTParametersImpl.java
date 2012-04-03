package org.waag.ah.rest;

import java.util.LinkedList;


public class RESTParametersImpl implements RESTParameters {
	private long resultLimit = 10;
	private long page = 1;
	private String language = "nl";	
	LinkedList<String> splitPath = new LinkedList<String>();
	
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
	public String getLanguage() {
		return language ;		
	}
	
	public void setLanguage(String language) {
		this.language = language;		
	}

	@Override
	public LinkedList<String> getSplitPath() {
		return splitPath;
	}
	
	public void setSplitPath(LinkedList<String> splitPath) {
		this.splitPath = splitPath;
	}

}
