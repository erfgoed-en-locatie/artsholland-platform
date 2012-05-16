package org.waag.ah.rest.model;

import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.waag.ah.rest.RestParameters;

public class SPARQLQuery {
	
	private static final String PAGING_PLACEMARK = "[[paging]]";
	private static final String STATEMENTS_PLACEMARK = "[[statements]]";
	private static final String FILTER_PLACEMARK = "[[filter]]";
	private static final String LANGUAGE_PLACEMARK = "[[language]]";

	private static final long MAXIMUM_PER_PAGE = 250;
	
	/*
	 * SPARQL headers
	 */
	private static final String SPARQL_HEADER_CONSTRUCT = "CONSTRUCT { ?object ?p ?o . }";
	private static final String SPARQL_HEADER_COUNT = "SELECT (COUNT(DISTINCT ?object) AS ?count)";
	
	/*
	 * SPARQL footers
	 */
	private static final String SPARQL_FOOTER_CONSTRUCT = "CONSTRUCT { ?object ?p ?o . }";
	private static final String SPARQL_FOOTER_COUNT = "SELECT (COUNT(DISTINCT ?object) AS ?count)";
		
	/*
	 * SPARQL body 
	 */
	private static final String SPARQL_BODY =	
		  "WHERE {" 
		+ "  OPTIONAL { ?object ?p ?o . }"
		+ "  {"
		+ "    SELECT DISTINCT ?object WHERE"
		+ "    {"
		+ "      [[statements]]"
		+ "      [[filter]]"
		+ "    } ORDER BY ?object [[paging]]"
		+ "  } [[language]]"
		+ "} ORDER BY ?object ?p";
	
	/*
	 * Make other class???
	 */
	private static final String SPARQL_BODY_SINGLE_SELF = 
  		"WHERE {"
		+ "  { [[statements]] [[language]] [[filter]] }"
		+ "} ORDER BY ?p";
	 
	
	/*
	 * private fields
	 */
	private String[] statements;
	private String body;
	
	public SPARQLQuery(String... statements) {
		this.statements = statements;	
		this.body = SPARQL_BODY;
	}
	
	public SPARQLQuery(boolean singleSelf, String... statements) {
		this.statements = statements;
		if (singleSelf) {		
			this.body = SPARQL_BODY_SINGLE_SELF;
		} else {			
			this.body = SPARQL_BODY;
		}
	}
	

			
	public String generateContruct(RestRelation relation, RestParameters params, Map<String, String> bindings, boolean includePrefix) {
		String query = SPARQL_HEADER_CONSTRUCT + body;		

		query = addPaging(query, params.getPerPage(), params.getPage());
		query = addLanguageFilter(query, params);
		query = addFilters(query, generateFilters(relation, params));		
		query = addStatements(query, (String[]) ArrayUtils.addAll(statements, relation.getStatements(params).toArray()));
		
		query = addBindings(bindings, query);		
		
		return includePrefix ? AHRDFNamespaces.getSPARQLPrefix() + query : query;
	}
	
	public String generateCount(RestRelation relation, RestParameters params, Map<String, String> bindings, boolean includePrefix) {
		String query = SPARQL_HEADER_COUNT + body;		
		
		query = query.replace(PAGING_PLACEMARK, "");
		query = query.replace(LANGUAGE_PLACEMARK, "");
		query = addFilters(query, generateFilters(relation, params));		
		query = addStatements(query, (String[]) ArrayUtils.addAll(statements, relation.getStatements(params).toArray()));
		
		query = addBindings(bindings, query);		
		
		return includePrefix ? AHRDFNamespaces.getSPARQLPrefix() + query : query;
	}
	
	/*
	private String generateSPARQLBody(RestRelation relation,
			RestParameters params, Map<String, String> bindings, String query) {
		
		query = addPaging(query, params.getResultLimit(), params.getPage());
		query = addLanguageFilter(query, params);
		query = addFilters(query, generateFilters(relation, params));		
		query = addStatements(query, (String[]) ArrayUtils.addAll(statements, relation.getStatements(params).toArray()));
		
		query = addBindings(bindings, query);		
		
		return query;
	}
	*/
	
	private String addBindings(Map<String, String> bindings, String query) {
		for (Map.Entry<String, String> entry : bindings.entrySet()) {
			query = query.replace("?" + entry.getKey(), "<" + entry.getValue() + ">");
		}
		return query;
	}
	
	public String[] getStatements() {
		return statements;
	}

	public void setStatements(String[] statements) {
		this.statements = statements;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
	
	
	
	
	
	private ArrayList<String> generateFilters(RestRelation relation,
			RestParameters params) { 
		return relation.getFilters(params);		
	}		
	
	private String addStatements(String query,  String... statements) {
		StringBuilder statementsString = new StringBuilder();
				
		if (statements != null && statements.length > 0) {			
			statementsString.append(statements[0]);			 
			for (int i = 1; i < statements.length; i++) {
				statementsString.append(" ");
				statementsString.append(statements[i]);
			}			
		}		
		return query.replace(STATEMENTS_PLACEMARK, statementsString);
	}

	private String addLanguageFilter(String query, RestParameters params) {
		ArrayList<String> filters = new ArrayList<String>();
		
		String languageFilter = "!isLiteral(?o) || datatype(?o) != \"xsd:string\" || langMatches(lang(?o), ?lang	) || langMatches(lang(?o), \"\")";
		languageFilter = languageFilter.replace("?lang", "\"" + params.getLanguageTag() + "\"");
		filters.add(languageFilter);		
		
		return addFilters(query, LANGUAGE_PLACEMARK, filters);
	}
	
	
	private String addPaging(String query, long perPage, long page) {
		// TODO: check if count & page are valid
		long oldPerPage = perPage;
		
		if (perPage > MAXIMUM_PER_PAGE) {
			perPage = MAXIMUM_PER_PAGE;
		}
		
		if (page < 1) {
			page = 1;
		}
		
		return query.replace(PAGING_PLACEMARK, "LIMIT " + perPage + " OFFSET " + oldPerPage * (page - 1));
	}
	
	private String addFilters(String query, ArrayList<String> filters) {
		return addFilters(query, FILTER_PLACEMARK, filters);
	}
	
	private String addFilters(String query, String placemark, ArrayList<String> filters) {		
		StringBuilder filter = new StringBuilder();
		if (filters != null && filters.size() > 0) {			
			filter.append("FILTER(");
			
			filter.append("(" + filters.get(0) + ")");			 
			 
			for (int i = 1; i < filters.size(); i++) {
				filter.append(" && ");
				filter.append("(" + filters.get(i) + ")");
			 }
			 filter.append(")");
		}
		
		return query.replace(placemark, filter);
	}

}
