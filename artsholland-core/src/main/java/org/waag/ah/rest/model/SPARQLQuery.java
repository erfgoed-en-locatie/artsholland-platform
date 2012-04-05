package org.waag.ah.rest.model;

import java.util.ArrayList;

import org.waag.ah.rest.RestParameters;

public class SPARQLQuery {
	
	private static final String PAGING_PLACEMARK = "[[paging]]";
	private static final String STATEMENTS_PLACEMARK = "[[statements]]";
	private static final String FILTER_PLACEMARK = "[[filter]]";
	private static final String LANGUAGE_PLACEMARK = "[[language]]";

	private String query;
	
	//private String base = "   { SELECT ?object WHERE { ?object a ?class. [[statements]] [[filter]]} ORDER BY ?object [[paging]] } [[language]]"
	

	/*
	private static final String QUERY_SINGLE_SELF = 
			"CONSTRUCT { ?object ?p ?o. }"
		+ "WHERE {"
		+ "  OPTIONAL { ?object ?p ?o . }"
		+ "  { ?object ?p ?o."
		+ "    ?object a ?class. [[language]] }"
		+ "} ORDER BY ?object ?p";
	
	*/
	
	/*
	
	SINGLE SELF
	 "?object ?p ?o."
	 "?object a ?class."
	*/
	
	String[] bodyMULTIPLESELF = 
		{"?object a ?class."};
	
	
String[] BODYMULTIPLEFORWARD = {
	 "?object2 ?p2 ?object.",
	 "?object2 a ?class.",
	 "?object a ?linkedClass."
	};
	

/*
HEADER SINGLE
"CONSTRUCT { ?object ?p ?o. }"
+ "WHERE {"
+ "   ?object ?p ?o."
+ "    ?object a ?class.[[statements]] [[filter]] [[language]] "
+ "} ORDER BY ?p";

	FOOTER SINGLE
	+ "      [[statements]]"
	+ "      [[filter]]"
	+ "    } LIMIT 1"
	+ "  } [[language]]"
	+ "} ORDER BY ?p";	*/

String HEADERCOUNT = "SELECT (COUNT(DISTINCT ?object) AS ?count)";
String HEADERCONSTRUCT = "CONSTRUCT { ?object ?p ?o . }";

String BOdyu_MULTIPLE =	
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
	

	
	
	public String generateQuery(RestRelation relation, RestParameters params) {
		return "";		
	}
	
	public String generateCountQuery(RestRelation relation, RestParameters params) {
		return "";
	}
	
	
	
	
	
	
	
	
	private ArrayList<String> generateFilters(RestRelation relation,
			RestParameters params) { 
		return null;
	}		

	private String addStatements(String query,  ArrayList<String> statements) {
		StringBuilder statementsString = new StringBuilder();
		if (statements != null && statements.size() > 0) {			
			statementsString.append(statements.get(0));			 
			for (int i = 1; i < statements.size(); i++) {
				statementsString.append(" ");				
			}			
		}		
		return query.replace("[[statements]]", statementsString);
	}

	private String addLanguageFilter(String query, RestParameters params) {
		ArrayList<String> filters = new ArrayList<String>();
		
		String languageFilter = "!isLiteral(?o) || datatype(?o) != \"xsd:string\" || langMatches(lang(?o), ?lang	) || langMatches(lang(?o), \"\")";
		languageFilter = languageFilter.replace("?lang", "\"" + params.getLanguageTag() + "\"");
		filters.add(languageFilter);		
		
		return addFilters(query, "[[language]]", filters);
	}
	
	
	
	private String addPaging(String query, long limit, long page) {
		// TODO: check if count & page are valid
		return query.replace("[[paging]]", "LIMIT "+ limit + " OFFSET " + limit * (page - 1));
	}
	
	private String addFilters(String query, ArrayList<String> filters) {
		return addFilters(query, "[[filter]]", filters);
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
