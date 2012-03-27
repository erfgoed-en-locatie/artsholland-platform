package org.waag.ah.rest;


public class RESTParametersImpl implements RESTParameters {
	private String objectURI;
	private long resultLimit = 10;
	private long page = 1;
	private String language = "nl";	
	
	public void setObjectURI(String objectName) {
		this.objectURI = objectName;
	}

	@Override
	public String getObjectURI() {
		return objectURI;
	}

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
}
