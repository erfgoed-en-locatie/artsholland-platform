package org.waag.ah.rest;

import java.util.LinkedList;

public interface RESTParameters {
	public long getResultLimit();
	public long getPage();
	public String getLanguage();
	
	// TODO: find better name
	public LinkedList<String> getSplitPath();	
}
