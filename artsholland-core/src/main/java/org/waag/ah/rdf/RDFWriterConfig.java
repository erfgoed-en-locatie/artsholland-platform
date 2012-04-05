package org.waag.ah.rdf;

import java.util.Map;

public class RDFWriterConfig {
	private boolean prettyPrint;
	private String languageTag;
	private Map<String, Number> pagination;
	
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
	
	public void setPagination(Map<String, Number> pagination) {
		this.pagination = pagination;
	}

	public Map<String, Number> getPagination() {
		return pagination;
	}
}
