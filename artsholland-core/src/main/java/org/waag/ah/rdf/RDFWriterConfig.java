package org.waag.ah.rdf;

import java.util.HashMap;
import java.util.Map;

import org.waag.ah.WriterConfig;
import org.waag.ah.WriterContentTypeConfig;

public class RDFWriterConfig implements WriterConfig {
	private String contentType;
	private boolean prettyPrint = false;
	private String languageTag;
	private Map<String, String> metaData = new HashMap<String, String>();
	private String baseUri;
	private boolean wrapResults = false;
	
	private WriterContentTypeConfig contentTypeConfig;
	private String responseContentType;	
	
	private String jsonpCallback;
	private boolean jsonp = false;
		
	public void setContentType(String contentType) {
		// TODO: This is, of course, a hack :)
		/*if (format.equals("application/json")) {
			contentType = RDFJSONFormat.MIMETYPE;
		}*/
		this.contentType = contentType;
	}

	@Override
	public String getContentType() {
		return contentType;
	}
	
	/*@Override
	public String getContentTypeForQueryType(QueryType queryType) {
		return contentTypeConfig.getContentTypeForQueryType(getContentType(), queryType);
	}*/
	
	public void setPrettyPrint(boolean prettyPrint) {
		this.prettyPrint = prettyPrint;
	}
	
	@Override
	public boolean isPrettyPrint() {
		return prettyPrint;
	}
	
	public void setLanguageTag(String languageTag) {
		this.languageTag = languageTag;
	}
	
	@Override
	public String getLanguageTag() {
		return languageTag;
	}

	public void setMetaData(String key, long value) {
		setMetaData(key, String.valueOf(value));
	}
	
	public void setMetaData(String key, String value) {
		if (metaData.containsKey(key) && value == null) {
			metaData.remove(key);
		} else if(value != null) {
			this.metaData.put(key, value);
		} else {
			throw new IllegalArgumentException("Metadata cannot be a null value");
		}
	}

	@Override
	public final Map<String, String> getMetaData() {
		return metaData;
	}
	
	@Override
	public final String getMetaData(String key) {
		return metaData.get(key);
	}
	
	public void setBaseUri(String baseUri) {
		this.baseUri = baseUri;
	}

	@Override
	public String getBaseUri() {
		return this.baseUri;
	}

	@Override
	public boolean isWrapResults() {
		return wrapResults;
	}

	public void setWrapResults(boolean wrapResults) {
		this.wrapResults = wrapResults;
	}

	@Override
	public WriterContentTypeConfig getContentTypeConfig() {
		return contentTypeConfig;
	}
	
	public void setContentTypeConfig(WriterContentTypeConfig writerContentTypeConfig) {
		this.contentTypeConfig = writerContentTypeConfig;
	}

	public String getResponseContentType() {
		return responseContentType;
	}

	public void setResponseContentType(String responseContentType) {
		this.responseContentType = responseContentType;
	}

	public boolean isOverrideResponseContentType() {
		return (responseContentType != null && responseContentType.length() > 0);		
	}

	public String getJSONPCallback() {
		return jsonpCallback;
	}

	public void setJSONPCallback(String jsonpCallback) {
		this.jsonpCallback = jsonpCallback;
	}

	public boolean isJSONP() {
		return jsonp;
	}

	public void setJSONP(boolean jsonp) {
		this.jsonp = jsonp;
	}

}
