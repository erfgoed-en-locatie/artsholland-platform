package org.waag.ah.rest;

import java.util.LinkedList;
import java.util.Map;

public interface RESTParameters {
	public long getResultLimit();
	public long getPage();
	public String getLanguageTag();	
	public LinkedList<String> getURIPathParts();
	Map<String, String[]> getURIParameterMap();	
}
