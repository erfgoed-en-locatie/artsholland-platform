package org.waag.ah.rest;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.openrdf.model.datatypes.XMLDatatypeUtil;

public class RestParameters {
	
//	public static enum RequestType {
//		REST,
//		DATA
//	};
	
	private long resultLimit = 0;
	private long page = 0;
	private String languageTag = "en";	
	private XMLGregorianCalendar dateFrom = null;
	private XMLGregorianCalendar dateTo = null;
	private boolean hideMetadata = false;
	private boolean prettyPrint = false;
	
	//private RequestType requestType = null;
	//private RelationQuantity quantity = null;

	public static long DEFAULT_RESULT_LIMIT = 10;

	private LinkedList<String> uriPathParts = new LinkedList<String>();
	private Map<String, String[]> parameterMap;
	
	public void setResultLimit(long resultLimit) {
		this.resultLimit = resultLimit;
	}
	
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
	
	public long getResultLimit() {
		return resultLimit;
	}

	public long getPage() {
//		if (!isPagingEnabled()) {
//			return 0;
//		}
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
