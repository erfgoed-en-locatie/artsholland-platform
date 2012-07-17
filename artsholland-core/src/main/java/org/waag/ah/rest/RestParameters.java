package org.waag.ah.rest;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class RestParameters {
	
	private long perPage = 0;
	private long page = 0;
	private String languageTag = "en";	
	//private XMLGregorianCalendar dateFrom = null;
	//private XMLGregorianCalendar dateTo = null;
	private boolean hideMetadata = false;
	private boolean prettyPrint = false;
	private boolean countTotals = false;
	private boolean plainText = false;

	public static long DEFAULT_RESULT_LIMIT = 10;

	private LinkedList<String> uriPathParts = new LinkedList<String>();
	private Map<String, String[]> parameterMap;
	private String jsonpCallback;
		
	public boolean isPrettyPrint() {
		return prettyPrint;
	}

	public void setPrettyPrint(boolean prettyPrint) {
		this.prettyPrint = prettyPrint;
	}
	
	public boolean isHideMetadata() {
		return hideMetadata;
	}

	public void setHideMetadata(boolean hideMetadata) {
		this.hideMetadata = hideMetadata;
	}
	
	public void setPerPage(long perPage) {
		this.perPage = perPage;
	}
	
	public long getPerPage() {
		if (perPage == 0) {
			return DEFAULT_RESULT_LIMIT;
		}
		return perPage;
	}

	public long getPage() {
		if (page < 1) {
			return 1;
		}
		return page;
	}

	public void setPage(long page) {
		this.page = page;
	}

	public String getLanguageTag() {
		return languageTag ;		
	}
	
	public void setLanguageTag(String languageTag) {
		this.languageTag = languageTag;		
	}
/*
	public void setDateFrom(String dateFrom) {
		if (dateFrom != null && dateFrom.length() > 0) {
			this.dateFrom = XMLDatatypeUtil.parseCalendar(dateFrom);
		}
	}
	
	public XMLGregorianCalendar getDateFrom() {
		return dateFrom;
	}
	
	public void setDateTo(String dateTo) {
		if (dateTo != null && dateTo.length() > 0) {
			this.dateTo = XMLDatatypeUtil.parseCalendar(dateTo);
		}
	}

	public XMLGregorianCalendar getDateTo() {
		return dateTo;
	}
*/
	
	public boolean getHideMetadata() {
		return hideMetadata;
	}	
	
	public void setURIPathParts(String... uriParts) {
		String joined = StringUtils.join(uriParts, "/").replaceAll("/+", "/");
		setURIPathParts(new LinkedList<String>(Arrays.asList(joined.split("/"))));
	}
	
	public void setURIPathParts(LinkedList<String> uriPathParts) {
		this.uriPathParts = uriPathParts;
	}

	public LinkedList<String> getURIPathParts() {
		return uriPathParts;
	}
	
	public void setURIParameterMap(Map<String, String[]> parameterMap) {
		this.parameterMap = parameterMap;		
	}

	public Map<String, String[]> getURIParameterMap() {
		return parameterMap;		
	}
	
//	private boolean isPagingEnabled() {
//		return resultLimit > 0;
//	}

	public boolean getPretty() {
		return prettyPrint;
	}
	
	public void setPretty(boolean pretty) {
		this.prettyPrint = pretty;
	}

	public boolean isCountTotals() {
		return countTotals;
	}

	public void setCountTotals(boolean countTotals) {
		this.countTotals = countTotals;
	}

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

}

//package org.waag.ah.rest;
//
//import java.util.LinkedList;
//import java.util.Map;
//
//import javax.xml.datatype.XMLGregorianCalendar;
//
//public interface RestParameters {
//	long getResultLimit();
//	long getPage();
//	String getLanguageTag();	
//	XMLGregorianCalendar getDateFrom();
//	XMLGregorianCalendar getDateTo();
//	LinkedList<String> getURIPathParts();
//	Map<String, String[]> getURIParameterMap();	
//}
