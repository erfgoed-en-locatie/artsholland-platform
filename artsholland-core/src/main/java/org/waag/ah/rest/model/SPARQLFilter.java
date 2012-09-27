package org.waag.ah.rest.model;

import java.util.ArrayList;
import java.util.Map;

public class SPARQLFilter {
	
	private String parameter;
	private String statement;
	private String filter;
	private ArrayList<String> extraParameters = new ArrayList<String>();
	
	public SPARQLFilter(String parameter, String statement) {
		this.parameter = parameter;
		this.statement = statement;
		this.filter = "";
	}
	
	public SPARQLFilter(String parameter, String statement, String filter) {
		this.parameter = parameter;
		this.statement = statement;
		this.filter = filter;
	}	

	public void setStatement(String statement) {
		this.statement = statement;
	}
	
	public String getStatement() {
		return statement;
	}
	
	public String getStatement(String parameter) {
		return getSPARQL(statement, parameter);		
	}
	
	public String getStatement(String parameter, Map<String, String[]> parameterMap) {
		return getSPARQL(statement, parameter, parameterMap);
	}
	
	public String getFilter() {
		return filter;
	}
	
	public String getFilter(String parameter) {
		return getSPARQL(filter, parameter);		
	}
	
	public String getFilter(String parameter, Map<String, String[]> parameterMap) {
		return getSPARQL(filter, parameter, parameterMap);
	}	
	
	public String getSPARQL(String sparql, String parameter) {
		if (sparql != null) {
			sparql = sparql.replace("?parameter", parameter);
		}
		return sparql;
	}
	
	public String getSPARQL(String sparql, String parameter, Map<String, String[]> parameterMap) {
		if (sparql != null) {
			for (String extraParameter : extraParameters) {
				if (parameterMap.containsKey(extraParameter)) {
					String[] values = parameterMap.get(extraParameter);
					if (values.length > 0) {
						// TODO: now only works for integer/double values
						// Add check to test whether values[0] is a string
						sparql = sparql.replace("?" + extraParameter, values[0]);	
					}				
				}
			}
			
			sparql = sparql.replace("?parameter", parameter);
		}
		return sparql;
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

	public ArrayList<String> getExtraParameters() {
		return extraParameters;
	}
	
	public void addExtraParameter(String extraParameter) {
		extraParameters.add(extraParameter);
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
