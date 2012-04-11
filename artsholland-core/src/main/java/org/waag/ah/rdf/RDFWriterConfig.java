package org.waag.ah.rdf;

import java.util.HashMap;
import java.util.Map;

public class RDFWriterConfig {
	private String format;
	private boolean prettyPrint;
	private String languageTag;
	private Map<String, String> metaData = new HashMap<String, String>();
	private String baseUri;
	
	public void setFormat(String format) {
		// TODO: This is, of course, a hack :)
		if (format.equals("application/json")) {
			format = RDFJSONFormat.MIMETYPE;
		}
		this.format = format;
	}
	
	public String getFormat() {
		return format;
	}
	
	public void setPrettyPrint(boolean prettyPrint) {
		this.prettyPrint = prettyPrint;
	}
	
	public boolean isPrettyPrint() {
		return prettyPrint;
	}
	
	public void setLanguageTag(String languageTag) {
		this.languageTag = languageTag;
	}
	
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

	public final Map<String, String> getMetaData() {
		return metaData;
	}
	
	public final String getMetaData(String key) {
		return metaData.get(key);
	}
	
	public void setBaseUri(String baseUri) {
		this.baseUri = baseUri;
	}

	public String getBaseUri() {
		return this.baseUri;
	}
}
