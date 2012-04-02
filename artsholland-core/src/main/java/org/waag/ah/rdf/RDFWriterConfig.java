package org.waag.ah.rdf;

import java.util.Map;

public class RDFWriterConfig {
	private boolean prettyPrint;
	private String languageTag;
	private Map<String, Number> metaData;
	
	public void setPrettyPrint(boolean prettyPrint) {
		this.prettyPrint = prettyPrint;
	}
	
	public boolean getPrettyPrint() {
		return prettyPrint;
	}
	
	public void setLanguageTag(String languageTag) {
		this.languageTag = languageTag;
	}
	
	public String getLanguageTag() {
		return languageTag;
	}
	
	public void setMetaData(Map<String, Number> metaData) {
		this.metaData = metaData;
	}

	public Map<String, Number> getMetaData() {
		return metaData;
	}
}
