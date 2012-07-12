package org.waag.ah;

import java.util.Map;

public interface WriterConfig {
	public String getContentType();
	public boolean isPrettyPrint();
	public String getLanguageTag();
	public Map<String, String> getMetaData();
	public String getMetaData(String key);
	public String getBaseUri();
	public boolean isWrapResults();
	
	public WriterContentTypeConfig getContentTypeConfig();
	public String getResponseContentType();
	public void setResponseContentType(String responseContentType); 
	public boolean isOverrideResponseContentType();
	public String getJSONPCallback();
	public boolean isJSONP();
}
