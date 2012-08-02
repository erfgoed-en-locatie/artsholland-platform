package org.waag.ah.rest;

// TODO: move to different package
public class SPARQLParameters {
	
	private boolean plainText = false;
	private String jsonpCallback;
	private String query;
		
	public void setJSONPCallback(String jsonpCallback) {
		this.jsonpCallback = jsonpCallback;		
	}
	
	public String getJSONPCallback() {
		return jsonpCallback;		
	}

	public boolean isPlainText() {
		return plainText;
	}

	public void setPlainText(boolean plainText) {
		this.plainText = plainText;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

}
