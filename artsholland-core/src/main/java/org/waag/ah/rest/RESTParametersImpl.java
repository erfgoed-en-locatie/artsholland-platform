package org.waag.ah.rest;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.openrdf.model.datatypes.XMLDatatypeUtil;


public class RESTParametersImpl implements RESTParameters {
	private long resultLimit = 10;
	private long page = 1;
	private String languageTag = "nl";	
	private LinkedList<String> uriPathParts = new LinkedList<String>();
	private Map<String, String[]> parameterMap;
	private XMLGregorianCalendar dateFrom = null;
	private XMLGregorianCalendar dateTo = null;
	
	public void setResultLimit(long resultLimit) {
		this.resultLimit = resultLimit;
	}
	
	@Override
	public long getResultLimit() {
		return resultLimit;
	}

	@Override
	public long getPage() {
		return page;
	}

	public void setPage(long page) {
		this.page = page;
	}

	@Override
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
	
	@Override
	public XMLGregorianCalendar getDateFrom() {
		return dateFrom;
	}
	
	public void setDateTo(String dateTo) {
		if (dateTo != null && dateTo.length() > 0) {
			this.dateTo = XMLDatatypeUtil.parseCalendar(dateTo);
		}
	}

	@Override
	public XMLGregorianCalendar getDateTo() {
		return dateTo;
	}
	
	public void setURIPathParts(String... uriParts) {
		String joined = StringUtils.join(uriParts, "/").replaceAll("/+", "/");
		setURIPathParts(new LinkedList<String>(Arrays.asList(joined.split("/"))));
	}
	
	public void setURIPathParts(LinkedList<String> uriPathParts) {
		this.uriPathParts = uriPathParts;
	}

	@Override
	public LinkedList<String> getURIPathParts() {
		return uriPathParts;
	}
	
	public void setURIParameterMap(Map<String, String[]> parameterMap) {
		this.parameterMap = parameterMap;		
	}

	@Override
	public Map<String, String[]> getURIParameterMap() {
		return parameterMap;		
	}
}
