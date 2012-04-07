package org.waag.ah.rest;

import java.util.LinkedList;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

public interface RESTParameters {
	long getResultLimit();
	long getPage();
	String getLanguageTag();	
	XMLGregorianCalendar getDateFrom();
	XMLGregorianCalendar getDateTo();
	LinkedList<String> getURIPathParts();
	Map<String, String[]> getURIParameterMap();	
}
