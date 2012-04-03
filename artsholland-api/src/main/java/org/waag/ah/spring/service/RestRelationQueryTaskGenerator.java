package org.waag.ah.spring.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import org.openrdf.model.Literal;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.waag.ah.bigdata.BigdataQueryService;
import org.waag.ah.bigdata.BigdataQueryService.QueryTask;
import org.waag.ah.rdf.RDFWriterConfig;
import org.waag.ah.rest.RDFJSONFormat;
import org.waag.ah.rest.RESTParameters;
import org.waag.ah.rest.model.RestRelation;
import org.waag.ah.rest.model.RestRelation.RelationQuantity;
import org.waag.ah.rest.model.RestRelation.RelationType;

public class RestRelationQueryTaskGenerator {
	
	// "!isLiteral(?hasValue) || datatype(?hasValue) != \"xsd:string\" || langMatches(lang(?hasValue), ?lang	) || langMatches(lang(?hasValue), \"\")"
	// query = query.replace("?lang", "\"" + params.getLanguage() + "\"");
	
	/*
	 * Check with http://localhost:8080/rest/venues/bf23f0c6-5c54-4d18-a0e5-35d1dc140508
	 * (Paradiso Amsterdam)
	 */
	
	private BigdataQueryService context;
	private RepositoryConnection conn; 
	private String baseUri;
	private RestRelation rootRelation;
	
	private static final String PAGING_PLACEMARK = "[[paging]]";
	private static final String FILTER_PLACEMARK = "[[filter]]";
	
	private static final String QUERY_PREFIX = 
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" + 
			"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" + 
			"PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" + 
			"PREFIX dc: <http://purl.org/dc/elements/1.1/>\n" + 
			"PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" + 
			"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" + 
			"PREFIX time: <http://www.w3.org/2006/time#>\n" + 
			"PREFIX gr: <http://purl.org/goodrelations/v1#>\n" + 
			"PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>\n" + 
			"PREFIX vcard: <http://www.w3.org/2006/vcard/ns#>\n" + 
			"PREFIX nub: <http://resources.uitburo.nl/>\n" + 
			"PREFIX ah: <http://data.artsholland.com/>\n";
	
	private static final String QUERY_SINGLE_SELF = 
			"CONSTRUCT { ?object ?p ?o. }"
		+ "WHERE {"
		+ "  { ?object ?p ?o."
		+ "    ?object a ?class. " + FILTER_PLACEMARK + " }"
		+ "} ORDER BY ?p";

	private static final String QUERY_MULTIPLE_SELF =	
			"CONSTRUCT { ?object ?p ?o. } "
		+ "WHERE { "
		+ "   OPTIONAL { ?object ?p ?o . } "
		+ "   { SELECT ?object WHERE { ?object a ?class. } ORDER BY ?object " + PAGING_PLACEMARK + " } " + FILTER_PLACEMARK
		+ "} ORDER BY ?object ?p";
	
	private static final String COUNT_SELF =
		  "SELECT (COUNT(DISTINCT ?s) AS ?count) "
		+ "WHERE { "
		+ "?s a ?class .}";
	
	private static final String QUERY_SINGLE_FORWARD = 
			"CONSTRUCT { ?s ?p2 ?o . }"
		+ "WHERE {" 
		+ "  OPTIONAL { ?s ?p2 ?o . }"
		+ "  {"
		+ "    SELECT DISTINCT ?s WHERE"
		+ "    {"
		+ "      ?object ?p ?s. "
		+ "	     ?object a ?class."
		+ "	     ?s a ?linkedClass."
		+ "    } ORDER BY ?s LIMIT 1"
		+ "  } " + FILTER_PLACEMARK
		+ "} ORDER BY ?s ?p2";	
	
	private static final String QUERY_MULTIPLE_FORWARD = 
			"CONSTRUCT { ?s ?p2 ?o . }"
		+ "WHERE {" 
		+ "  OPTIONAL { ?s ?p2 ?o . }"
		+ "  {"
		+ "    SELECT DISTINCT ?s WHERE"
		+ "    {"
		+ "      ?object ?p ?s. "
		+ "	     ?object a ?class."
		+ "	     ?s a ?linkedClass."
		+ "    } ORDER BY ?s " + PAGING_PLACEMARK
		+ "  }" + FILTER_PLACEMARK
		+ "} ORDER BY ?s ?p2";
	
	private static final String COUNT_FORWARD = 
			"SELECT (COUNT(DISTINCT ?s) AS ?count)"
		+ "WHERE {" 
		+ "  OPTIONAL { ?s ?p2 ?o . }"
		+ "  {"
		+ "    SELECT DISTINCT ?s WHERE"
		+ "    {"
		+ "      ?object ?p ?s. "
		+ "	     ?object a ?class."
		+ "	     ?s a ?linkedClass."
		+ "    } ORDER BY ?s "
		+ "  }"
		+ "} ORDER BY ?s ?p2";	
	
	private static final String QUERY_MULTIPLE_BACKWARD = 
			"CONSTRUCT { ?s ?p2 ?o . }"
		+ "WHERE" 
		+ "{"
		+ "  OPTIONAL { ?s ?p2 ?o . }"
		+ "  {"
		+ "    SELECT DISTINCT ?s WHERE"
		+ "    {"
		+ "      ?s ?p ?object."
		+ "	     ?object a ?class."
		+ "	     ?s a ?linkedClass."
		+ "    } ORDER BY ?s " + PAGING_PLACEMARK
		+ "  }" + FILTER_PLACEMARK
		+ "} ORDER BY ?s ?p2";	
	
	private static final String COUNT_BACKWARD = 
			"SELECT (COUNT(DISTINCT ?s) AS ?count)"
		+ "WHERE" 
		+ "{"
		+ "  OPTIONAL { ?s ?p2 ?o . }"
		+ "  {"
		+ "    SELECT DISTINCT ?s WHERE"
		+ "    {"
		+ "      ?s ?p ?object."
		+ "	     ?object a ?class."
		+ "	     ?s a ?linkedClass."
		+ "    } ORDER BY ?s "
		+ "  }"
		+ "} ORDER BY ?s ?p2";	
	
	private static final String QUERY_MULTIPLE_BACKWARDFORWARD = 
			"CONSTRUCT { ?s ?p3 ?o . }"
		+ "WHERE" 
		+ "{"
		+ "  OPTIONAL { ?s ?p3 ?o . }"
		+ "  {"
		+ "    SELECT DISTINCT ?s WHERE"
		+ "    {"
		+ "      ?i ?p1 ?object. ?i ?p2 ?s."
		+ "	     ?object a ?class."
		+ "	     ?s a ?linkedClass."
		+ "    } ORDER BY ?s " + PAGING_PLACEMARK
		+ "  }" + FILTER_PLACEMARK
		+ "} ORDER BY ?url ?p3";	
	
	private static final String COUNT_BACKWARDFORWARD = 
			"SELECT (COUNT(DISTINCT ?s) AS ?count)"
		+ "WHERE" 
		+ "{"
		+ "  OPTIONAL { ?s ?p3 ?o . }"
		+ "  {"
		+ "    SELECT DISTINCT ?s WHERE"
		+ "    {"
		+ "      ?i ?p1 ?object. ?i ?p2 ?s."
		+ "	     ?object a ?class."
		+ "	     ?s a ?linkedClass."
		+ "    } ORDER BY ?s "
		+ "  }"
		+ "} ORDER BY ?url ?p3";	
	
	
	/*
	private static final String QUERY_COUNT_OBJECTS_BY_CLASS =
		  "SELECT (COUNT(DISTINCT ?s) AS ?count) "
		+ "WHERE { "
		+ "?s a ?class .}";
		
		
			private static final String QUERY_COUNT_LINKED_OBJECTS_BY_CLASS = 			
			"SELECT (COUNT(DISTINCT ?s) AS ?count) "
			+ "WHERE { { ?object ?p ?s. } UNION { ?s ?p ?object. } UNION { ?i ?p1 ?object. ?i ?p2 ?s. } ?s a ?linkedClass. }";
	

	 */
	
	public RestRelationQueryTaskGenerator(BigdataQueryService context,
			RepositoryConnection conn, String baseUri, RestRelation rootRelation) {
		this.context = context;
		this.conn = conn;
		this.baseUri = baseUri;
		this.rootRelation = rootRelation;
	}
	
	public long getCount(String query, String objectURI, String classURI, String linkedClassURI) {
		
			try {
				
				query = query.replace("?object", "<" + objectURI + ">");
				query = query.replace("?class", "<" + classURI + ">");
				query = query.replace("?linkedClass", "<" + linkedClassURI + ">");
				
				TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, QUERY_PREFIX + query);				
		
				TupleQueryResult result = tupleQuery.evaluate();
				if (result.hasNext()) {
					BindingSet next = result.next();
					if (next.hasBinding("count")) {
						Value value = next.getValue("count");
						if (value instanceof Literal) {
							return ((Literal) value).longValue();
						}
					}
				}				
				 
				return 0;							
				
			} catch (Exception e) {				
				e.printStackTrace();
			}

			return 0;
	}
		
	private String addPaging(String query, long limit, long page) {
		// TODO: check if count & page are valid
		return query.replace(PAGING_PLACEMARK, "LIMIT "+ limit + " OFFSET " + limit * (page - 1));
	}
	
	private String addFilters(String query, ArrayList<String> filters) {		
		StringBuilder filter = new StringBuilder();
		if (filters.size() > 0) {			
			filter.append("FILTER(");
			
			filter.append("(" + filters.get(0) + ")");			 
			 
			for (int i = 1; i < filters.size(); i++) {
				filter.append(" && ");
				filter.append("(" + filters.get(i) + ")");
			 }
			 filter.append(")");
		}
		
		return query.replace(FILTER_PLACEMARK, filter);
	}
	
	
	public QueryTask generate(ServletOutputStream out, RESTParameters params) throws MalformedQueryException {
		
		LinkedList<String> uriPathParts = params.getURIPathParts();
		RestRelation relation = rootRelation.findRelation(uriPathParts);
		
		if (relation == null) {
			return null;
		}
			
		long count = 0;	
		long page = params.getPage();
		boolean calculateCount = (page == 1);
		
		QueryTask queryTask = null;
		String query = null;
		Map<String, String> bindings = new HashMap<String, String>();
		
		RelationType type = relation.getType();
		RelationQuantity quantity = relation.getQuantity();
		
		if (type == RelationType.SELF) {
			if (quantity == RelationQuantity.SINGLE) {
				
				query = QUERY_SINGLE_SELF;
				bindings.put("object", relation.getObjectURI(uriPathParts, uriPathParts.size() - 1));
				bindings.put("class", relation.getClassURI());
				
			} else if (relation.getQuantity() == RelationQuantity.MULTIPLE) {
				
				query = QUERY_MULTIPLE_SELF;
				bindings.put("class", relation.getClassURI());
				
				if (calculateCount) {
					count = getCount(COUNT_SELF, null, relation.getClassURI(), null);
				}
				
			}
		} else { 
			
			if (relation.getParent() != null) {
			
				String objectURI = relation.getParent().getObjectURI(uriPathParts, uriPathParts.size() - 2);
				String classURI = relation.getParent().getClassURI();
				String linkedClassURI = relation.getClassURI();
				
				if (type == RelationType.FORWARD) {
				
					if (quantity == RelationQuantity.SINGLE) {
						query = QUERY_SINGLE_FORWARD;		
					} else if (quantity == RelationQuantity.MULTIPLE) {
						query = QUERY_MULTIPLE_FORWARD;
						if (calculateCount) {
							count = getCount(COUNT_FORWARD, objectURI, classURI, linkedClassURI);
						}
					}
		
				} else if (type == RelationType.BACKWARD) {
					
					query = QUERY_MULTIPLE_BACKWARD;
					if (calculateCount) {
						count = getCount(COUNT_BACKWARD, objectURI, classURI, linkedClassURI);
					}
					
				} else if (type == RelationType.BACKWARDFORWARD) {
					
					query = QUERY_MULTIPLE_BACKWARDFORWARD;	
					if (calculateCount) {
						count = getCount(COUNT_BACKWARDFORWARD, objectURI, classURI, linkedClassURI);
					}
	
				}
				
				bindings.put("object", objectURI);
				bindings.put("class", classURI);
				bindings.put("linkedClass", linkedClassURI);
			}
			
		}
		
		if (query != null) {
			// TODO: why does setBinding not always work?
			for (Map.Entry<String, String> entry : bindings.entrySet()) {
				query = query.replace("?" + entry.getKey(), "<" + entry.getValue() + ">");
			}
			
			RDFWriterConfig config = new RDFWriterConfig();
			config.setPrettyPrint(true);
			Map<String, Number> metaData = new HashMap<String, Number>();
			if (count > 0) {
				metaData.put("count", count);
			}
			metaData.put("page", page);
			metaData.put("limit", params.getResultLimit());
			config.setMetaData(metaData);
			
			query = addPaging(query, params.getResultLimit(), params.getPage());
			query = addFilters(query, generateFilters(relation, params));
			queryTask = context.getQueryTask(QUERY_PREFIX + query, baseUri, RDFJSONFormat.MIMETYPE, out, config);
			
	//		ValueFactory vf = conn.getValueFactory();
	//		for (Map.Entry<String, URI> entry : bindings.entrySet()) {
	//			queryTask.setBinding(entry.getKey(), vf.createURI(entry.getValue()));
	//		}
		}
		
		return queryTask;
	}

	private ArrayList<String> generateFilters(RestRelation relation,
			RESTParameters params) { 
		
		ArrayList<String> filters = new ArrayList<String>();
				
		String languageFilter = "!isLiteral(?o) || datatype(?o) != \"xsd:string\" || langMatches(lang(?o), ?lang	) || langMatches(lang(?o), \"\")";
		languageFilter = languageFilter.replace("?lang", "\"" + params.getLanguageTag() + "\"");
		
		filters.add(languageFilter);
		
		return filters;
	}
	
}
