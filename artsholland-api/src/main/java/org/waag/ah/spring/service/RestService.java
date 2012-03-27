package org.waag.ah.spring.service;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.concurrent.FutureTask;

import javax.ejb.EJB;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.waag.ah.PlatformConfig;
import org.waag.ah.RepositoryConnectionFactory;
import org.waag.ah.bigdata.BigdataQueryService;
import org.waag.ah.bigdata.BigdataQueryService.QueryTask;
import org.waag.ah.rest.RDFJSONFormat;
import org.waag.ah.rest.RESTParameters;

@Service("restService")
public class RestService implements InitializingBean, DisposableBean {
	private static final Logger logger = LoggerFactory.getLogger(RestService.class);
	
	@EJB(mappedName="java:app/datastore/BigdataQueryService")
	private BigdataQueryService context;

	@EJB(mappedName="java:app/datastore/BigdataConnectionService")
	private RepositoryConnectionFactory connFactory;
	
//	@EJB(mappedName = "java:app/datastore/ObjectConnectionService")
//	private ObjectConnectionFactory connFactory;

	private PropertiesConfiguration config;
	private RepositoryConnection conn;
	private ValueFactory vf;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		config = PlatformConfig.getConfig(); 
		conn = connFactory.getConnection();
		vf = conn.getValueFactory();
	}

	@Override
	public void destroy() throws Exception {
		conn.close();
	}	

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
		"PREFIX ah: <http://purl.org/artsholland/1.0/>\n";
	
	//private static final String NAMESPACE = "http://purl.org/artsholland/1.0/";
	private static final String NAMESPACE = "http://data.artsholland.com/";

	private static final String QUERY_OBJECT = 
			"CONSTRUCT { ?object ?property ?hasValue. }"
		+ "WHERE {"
		+ "  { ?object ?property ?hasValue."
		+ "    ?object a ?class. }"
		+ "} ORDER BY ?property";
	
	// FILTER(langMatches(lang(?name), "EN"))
	
	private static final String QUERY_OBJECTS_BY_CLASS =	
			"CONSTRUCT { ?url ?relationTwo ?second . } "
		+ "WHERE { "
		+ "   OPTIONAL { ?url ?relationTwo ?second . } "
		+ "   { SELECT ?url WHERE { ?url a ?class . } ORDER BY ?url [[paging]] } "
		+ "} ORDER BY ?url ?relationTwo";
	
	
	
	private static final String QUERY_COUNT_OBJECTS_BY_CLASS =
			  "SELECT (COUNT(DISTINCT ?s) AS ?count) "
			+ "WHERE { "
			+ "?s a ?class .}";
	

	/*
	 * TODO: seperate queries per class (predicate checking might me necessary.
	 * Also speed issues: this queries checks links in both ways, but it is easy to check
	 * beforehand in which direction the query need be evaluated.
	 */
	private static final String QUERY_LINKED_OBJECTS_BY_CLASS = 
			"CONSTRUCT { ?url ?relationTwo ?second . }"
		+ "WHERE" 
		+ "{"
		+ "  OPTIONAL { ?url ?relationTwo ?second . }"
		+ "  {"
		+ "    SELECT DISTINCT ?url WHERE"
		+ "    {"
		+ "      { ?object ?p ?url. } UNION"		
		+ "      { ?url ?p ?object. } UNION"
		+ "      { ?i ?p1 ?object. ?i ?p2 ?url. }"
		+ "	     ?object a ?class."
		+ "	     ?url a ?linkedClass."
		+ "    } ORDER BY ?url [[paging]]"
		+ "  }"
		+ "} ORDER BY ?url ?relationTwo";

	
	
	
	
	

	private static final String QUERY_COUNT_LINKED_OBJECTS_BY_CLASS = 			
			"SELECT (COUNT(DISTINCT ?s) AS ?count) "
			+ "WHERE { { ?object ?p ?s. } UNION { ?s ?p ?object. } ?s a ?class. }";
	
	/*
	 * Check with http://localhost:8080/rest/venues/bf23f0c6-5c54-4d18-a0e5-35d1dc140508
	 * (Paradiso Amsterdam)
	 */
	
	
	/*
	 * TODO: use RDF jargon?
	 * 
	 * object instead of instance
	 * ?s ?p ?o
	 */
//	public JSONPagedResultSet getObject(URI uri, String lang) {		
//		AHRDFObject result = null;
//		try {
//			conn.setLanguage(lang);
//			
//			Object object = conn.getObject(uri);
//			if (object instanceof AHRDFObject) {
//				result = (AHRDFObject) object;
//			}
//		} catch (RepositoryException e) {
//			e.printStackTrace();
//		}
//		return new JSONPagedResultSet(result);
//		
//	}
	
	public URI createURI(String uriStringWithoutNamespace) {
		return vf.createURI(NAMESPACE + uriStringWithoutNamespace);
	}	

	private long getCount(TupleQuery query) {
		try {
			TupleQueryResult result = query.evaluate();
			if (result.hasNext()) {
				BindingSet next = result.next();
				if (next.hasBinding("count")) {
					Value value = next.getValue("count");
					if (value instanceof Literal) {
						return ((Literal) value).longValue();
					}
				}
			}
		} catch (Exception e)  {
			/*
			 * TODO	
			 */
		}
		 
		return 0;
	}
	
//	public long getObjectCount(URI classURI) {
//		
//			try {
//				TupleQuery query = conn.prepareTupleQuery(QueryLanguage.SPARQL,
//						QUERY_PREFIX + QUERY_COUNT_OBJECTS_BY_CLASS);
//				
//				query.setBinding("class", classURI);	
//				
//				return getCount(query);
//				
//			} catch (Exception e) {				
//				e.printStackTrace();
//			}
//
//			return 0;
//		
//	}
	
//	public long getLinkedObjectCount(URI objectURI, URI classURI) {
//		
//		try {
//			TupleQuery query = conn.prepareTupleQuery(QueryLanguage.SPARQL,
//					QUERY_PREFIX + QUERY_COUNT_LINKED_OBJECTS_BY_CLASS);
//			
//			query.setBinding("object", objectURI);
//			query.setBinding("class", classURI);
//			
//			return getCount(query);
//			
//		} catch (Exception e) {				
//			e.printStackTrace();
//		}
//
//		return 0;
//	
//}

//	public JSONPagedResultSet getObjects(URI classURI, long count, long page, String lang) {
//		conn.setLanguage(lang);
//		long total = getObjectCount(classURI);
//		
//		try {			
//			ObjectQuery query = conn.prepareObjectQuery(QueryLanguage.SPARQL,
//					QUERY_PREFIX + addPaging(QUERY_OBJECTS_BY_CLASS, count, page));
//			
//			query.setBinding("class", classURI);			
//			
//			@SuppressWarnings("unchecked")
//			Set<AHRDFObject> results = (Set<AHRDFObject>) query.evaluate().asSet();
//			return new JSONPagedResultSet(results, page * count, total);
//			
//		} catch (Exception e) {			
//			e.printStackTrace();
//		}
//		
//		return null;
//	}
	
	private void executeParameterizedQuery(HttpServletResponse response,
			RESTParameters params, String query) throws IOException {
		try {
			
			/*
			PrintWriter vis = new PrintWriter(new OutputStreamWriter(response.getOutputStream(), "UTF8"), true);
			vis.write("DE VIS  DE VIS DE VIS!!!!");
			vis.flush();
			*/
			
			
			final String pagedQuery = QUERY_PREFIX + addPaging(query, params.getResultLimit(), params.getPage());
			final OutputStream out = response.getOutputStream();
			final QueryTask queryTask = context.getQueryTask(pagedQuery,
					config.getString("platform.baseUri"), RDFJSONFormat.MIMETYPE, out);
			final FutureTask<Void> ft = new FutureTask<Void>(queryTask);
		
			// Set binding:
			
			if (params.getObjectURI() != null && !params.getObjectURI().isEmpty()) {
				queryTask.setBinding("object", createURI(params.getObjectURI()));
			}
			
			if (params.getObjectClass() != null && !params.getObjectClass().isEmpty()) {
				queryTask.setBinding("class", createURI(params.getObjectClass()));	
			}
			
			if (params.getObjectClass() != null && !params.getObjectClass().isEmpty()) {
				queryTask.setBinding("linkedClass", createURI(params.getLinkedClass()));	
			}
			
			response.setStatus(HttpServletResponse.SC_OK);
			context.executeQueryTask(ft);
			ft.get();
		
		} catch (MalformedQueryException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,
				e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
				e.getMessage());
		}
	}
			
	public void getObjects(HttpServletRequest request,
			HttpServletResponse response, RESTParameters params)
			throws IOException {

		if (params.getObjectClass().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Need an object class");
		}		
		executeParameterizedQuery(response, params, QUERY_OBJECTS_BY_CLASS);
	}
	
	public void getObject(HttpServletRequest request,
			HttpServletResponse response, RESTParameters params) throws IOException {
		
		if (params.getObjectURI().isEmpty() || params.getObjectClass().isEmpty()) {
		    response.sendError(HttpServletResponse.SC_BAD_REQUEST,
		    		"Need an object class and CIDN");
		}		
		executeParameterizedQuery(response, params, QUERY_OBJECT);
	}	
	
	public void getLinkedObjects(HttpServletRequest request,
			HttpServletResponse response, RESTParameters params) throws IOException {
		

		if (params.getObjectClass().isEmpty() || params.getLinkedClass().isEmpty() || params.getObjectURI().isEmpty()) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST,
              "Need an object class, CIDN and object subclass.");
		}
		executeParameterizedQuery(response, params, QUERY_LINKED_OBJECTS_BY_CLASS);
	}	

	private String addPaging(String query, long limit, long page) {
		// TODO: check if count & page are valid
		return query.replace("[[paging]]", "LIMIT "+ limit + " OFFSET " + limit * page);
	}


//	public Set<?> getEvents(XMLGregorianCalendar dateTimeFrom,
//			XMLGregorianCalendar dateTimeTo) throws MalformedQueryException, RepositoryException, QueryEvaluationException {
//		
//		ObjectQuery query = conn.prepareObjectQuery(QueryLanguage.SPARQL, 
//				"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" + 
//				"PREFIX time: <http://www.w3.org/2006/time#>\n" + 
//				"SELECT DISTINCT ?instance WHERE { " +
//				"	?instance time:hasBeginning ?datePub" +
//				"	FILTER(?datePub >= ?dtFrom && ?datePub < ?dtTo)." +
//				"} ORDER BY DESC(?datePub) LIMIT 10"
//			);
//		
////			query.setBinding("dtFrom", conn.getValueFactory().createLiteral(
////					XMLDatatypeUtil.parseCalendar("2009-01-01T17:00:00Z")));
////			query.setBinding("dtTo", conn.getValueFactory().createLiteral(
////					XMLDatatypeUtil.parseCalendar("2014-02-01T17:00:00Z")));
//		
//		query.setBinding("dtFrom", conn.getValueFactory().createLiteral(dateTimeFrom));
//		query.setBinding("dtTo", conn.getValueFactory().createLiteral(dateTimeTo));
//		
//		return query.evaluate().asSet();		
//
//	}

//	public JSONPagedResultSet getLinkedObjects(URI objectURI, URI classURI, long count, long page, String lang) {
//		
//		conn.setLanguage(lang);
//		long total = getLinkedObjectCount(objectURI, classURI);
//		
//		try {			
//			ObjectQuery query = conn.prepareObjectQuery(QueryLanguage.SPARQL,
//					QUERY_PREFIX + addPaging(QUERY_LINKED_OBJECTS_BY_CLASS, count, page));
//			
//			query.setBinding("object", objectURI);
//			query.setBinding("class", classURI);
//			
//			@SuppressWarnings("unchecked")
//			Set<AHRDFObject> results = (Set<AHRDFObject>) query.evaluate().asSet();
//			return new JSONPagedResultSet(results, page * count, total);
//			
//		} catch (Exception e) {			
//			e.printStackTrace();
//		}
//		
//		return null;
//			
//	}
//
//	public String testObject() throws MalformedQueryException, RepositoryException, QueryEvaluationException {
//
//		ArrayList<String> strings = new ArrayList<String>();
//
//		ObjectQuery query = conn.prepareObjectQuery(QueryLanguage.SPARQL,
//				QUERY_PREFIX + addPaging(QUERY_OBJECTS_BY_CLASS, 100, 0));
//		
//		query.setBinding("class", createURI("Event"));			
//		
//		@SuppressWarnings("unchecked")
//		Set<AHRDFObject> results = (Set<AHRDFObject>) query.evaluate().asSet();
//		for (AHRDFObject result: results) {
//			strings.add(result.getURI());
//		}
//		
//
//		
//		return strings.toString();
//	}
//	
//	public Object testTuple() throws MalformedQueryException, RepositoryException, QueryEvaluationException {
//		
//		String vis = "SELECT DISTINCT ?s ?p ?o WHERE { ?s ?p ?o. ?s a ah:Event.} LIMIT 1000";
//		Set<String> strings = new LinkedHashSet<String>();
//		
//		TupleQuery query = conn.prepareTupleQuery(QueryLanguage.SPARQL, QUERY_PREFIX + vis);
//		
//		TupleQueryResult result = query.evaluate();
//		
//		while (result.hasNext()) {
//			BindingSet paard = result.next();
//			
//			strings.add(paard.getBinding("s") + " met de " + paard.getBinding("p") + " is een " + paard.getBinding("o"));
//		}
//		
//		return strings;
//		
//	}

}
