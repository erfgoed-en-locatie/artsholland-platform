package org.waag.ah.rest.model;

public class SPARQLFilter {
	
	private String parameter;
	private String statement;
	private String filter;
	
	public SPARQLFilter(String parameter, String statement, String filter) {
		this.parameter = parameter;
		this.statement = statement;
		this.filter = filter;
	}
	
	public String getStatement() {
		return statement;
	}
	public void setStatement(String statement) {
		this.statement = statement;
	}
	
	public String getFilter() {
		return filter;
	}
	
	public String getFilter(String parameter) {
		return filter.replace("?parameter", parameter);
	}
	
	public void setFilter(String filter) {
		this.filter = filter;
	}
	
	public String getParameter() {
		return parameter;
	}
	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	/*
	 * 
	 * select.add("?object time:hasBeginning ?time.");
	 * select.add("?object vcard:locality ?locality.");
	 * 
	 * 
	 * ?locality=groningen
	 * ?object vcard:locality ?locality.
	 * 
	 * ?locality = "groningen"	
	 */
	
}
