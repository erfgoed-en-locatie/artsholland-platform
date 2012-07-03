package org.waag.ah.rest.model;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.ArrayUtils;
import org.waag.ah.rest.RestParameters;

public class SPARQLQuery {
	
	private static final String PAGING_PLACEMARK = "[[paging]]";
	private static final String STATEMENTS_PLACEMARK = "[[statements]]";
	private static final String FILTER_PLACEMARK = "[[filter]]";
	private static final String LANGUAGE_PLACEMARK = "[[language]]";

	private static final long MAXIMUM_PER_PAGE = 250;
			
	/*
	 * SPARQL
	 */
	
	// TODO: load SPARQL from file?
	private static final String SPARQL_CONSTRUCT =	
			"CONSTRUCT { ?object ?p ?o . }"
		+ "WHERE {" 
		+ "  OPTIONAL { ?object ?p ?o . }"
		+ "  {"
		+ "    SELECT DISTINCT ?object WHERE"
		+ "    {"
		+ "      [[statements]]"
		+ "      [[filter]]"
		+ "    } [[paging]]" //ORDER BY ?object 
		+ "  } [[language]]"
		+ "}"; // ORDER BY ?object ?p
		
		private static final String SPARQL_COUNT =	
				"SELECT (COUNT(?o) AS ?count)"
			+ "WHERE {" 
			+ "  OPTIONAL { ?object a ?o . }"
			+ "  {"
			+ "    SELECT DISTINCT ?object WHERE"
			+ "    {"
			+ "      [[statements]]"
			+ "      [[filter]]"
			+ "    }" // ORDER BY ?object
			+ "  }"
			+ "} GROUP BY ?o";

	private static final String SPARQL_SINGLE_SELF = 
  		"CONSTRUCT { ?object ?p ?o . }"
  	+	"WHERE {"
		+ "  { [[statements]] [[language]] [[filter]] }"
		+ "} ORDER BY ?p";
	 
	
	/*
	 * private fields
	 */
	private String[] statements;
	private boolean singleSelf = false;
	
	public SPARQLQuery(String... statements) {
		this.statements = statements;		
	}
	
	public SPARQLQuery(boolean singleSelf, String... statements) {
		this.statements = statements;
		this.singleSelf = singleSelf;
	}
			
	public String generateContruct(RestRelation relation, RestParameters params, Map<String, String> bindings, boolean includePrefix) {
		String query = singleSelf ? SPARQL_SINGLE_SELF : SPARQL_CONSTRUCT;		

		query = addPaging(query, params.getPerPage(), params.getPage());
		query = addLanguageFilter(query, params);
		query = addFilters(query, generateFilters(relation, params));		
		query = addStatements(query, (String[]) ArrayUtils.addAll(statements, relation.getStatements(params).toArray()));
		
		query = addBindings(bindings, query);		
		
		return includePrefix ? AHRDFNamespaces.getSPARQLPrefix() + query : query;
	}
	
	public String generateCount(RestRelation relation, RestParameters params, Map<String, String> bindings, boolean includePrefix) {
		String query = SPARQL_COUNT;		
		
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
		Map<String, String> namespaces = AHRDFNamespaces.getNamespaces();
		for (Map.Entry<String, String> entry : bindings.entrySet()) {
			String value = entry.getValue();
			boolean addBrackets = true; 
			if (value.contains(":")) {
				for (Entry<String, String> namespace : namespaces.entrySet()) {
					if (value.startsWith(namespace.getKey())) {
						addBrackets = false;						
						break;
					}
				}
			}
			
			if (addBrackets) {
				value = "<" + value + ">";
			}
			
			query = query.replace("?" + entry.getKey(), value);
		}
		return query;
	}
	
	public String[] getStatements() {
		return statements;
	}

	public void setStatements(String[] statements) {
		this.statements = statements;
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
