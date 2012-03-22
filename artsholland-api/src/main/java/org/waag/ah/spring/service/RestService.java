package org.waag.ah.spring.service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.concurrent.FutureTask;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.waag.ah.ObjectConnectionFactory;
import org.waag.ah.PlatformConfig;
import org.waag.ah.bigdata.BigdataQueryService;
import org.waag.ah.bigdata.BigdataQueryService.QueryTask;
import org.waag.ah.jackson.JSONPagedResultSet;
import org.waag.ah.model.rdf.AHRDFObject;
import org.waag.ah.rest.RDFJSONFormat;
import org.waag.ah.rest.RESTParameters;

@Service("restService")
public class RestService implements InitializingBean, DisposableBean {
	private static final Logger logger = LoggerFactory.getLogger(RestService.class);
	
	@EJB(mappedName="java:app/datastore/BigdataQueryService")
	private BigdataQueryService context;
	
	@EJB(mappedName = "java:app/datastore/ObjectConnectionService")
	private ObjectConnectionFactory connFactory;

	private PropertiesConfiguration config;
	private ObjectConnection conn;
	private ValueFactory vf;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		config = PlatformConfig.getConfig(); 
		conn = connFactory.getObjectConnection();
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
	
	private static final String NAMESPACE = "http://purl.org/artsholland/1.0/";

	
	private static final String QUERY_OBJECTS_BY_CLASS = 
			  "SELECT DISTINCT ?s "
			+ "WHERE { ?s a ?class . } ORDER BY ?s";
	
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
			"SELECT DISTINCT ?s WHERE " +
			"{ { ?object ?p ?s. } UNION " +
			"{ ?s ?p ?object. } UNION " +
			"{ ?i ?p1 ?object. ?i ?p2 ?s. } " +   
			"?s a ?class. } ORDER BY ?s";

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
	public JSONPagedResultSet getObject(URI uri, String lang) {		
		AHRDFObject result = null;
		try {
			conn.setLanguage(lang);
			
			Object object = conn.getObject(uri);
			if (object instanceof AHRDFObject) {
				result = (AHRDFObject) object;
			}
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		return new JSONPagedResultSet(result);
		
	}
	
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
	
	public long getObjectCount(URI classURI) {
		
			try {
				TupleQuery query = conn.prepareTupleQuery(QueryLanguage.SPARQL,
						QUERY_PREFIX + QUERY_COUNT_OBJECTS_BY_CLASS);
				
				query.setBinding("class", classURI);	
				
				return getCount(query);
				
			} catch (Exception e) {				
				e.printStackTrace();
			}

			return 0;
		
	}
	
	public long getLinkedObjectCount(URI objectURI, URI classURI) {
		
		try {
			TupleQuery query = conn.prepareTupleQuery(QueryLanguage.SPARQL,
					QUERY_PREFIX + QUERY_COUNT_LINKED_OBJECTS_BY_CLASS);
			
			query.setBinding("object", objectURI);
			query.setBinding("class", classURI);
			
			return getCount(query);
			
		} catch (Exception e) {				
			e.printStackTrace();
		}

		return 0;
	
}

	public JSONPagedResultSet getObjects(URI classURI, long count, long page, String lang) {
		conn.setLanguage(lang);
		long total = getObjectCount(classURI);
		
		try {			
			ObjectQuery query = conn.prepareObjectQuery(QueryLanguage.SPARQL,
					QUERY_PREFIX + addPaging(QUERY_OBJECTS_BY_CLASS, count, page));
			
			query.setBinding("class", classURI);			
			
			@SuppressWarnings("unchecked")
			Set<AHRDFObject> results = (Set<AHRDFObject>) query.evaluate().asSet();
			return new JSONPagedResultSet(results, page * count, total);
			
		} catch (Exception e) {			
			e.printStackTrace();
		}
		
		return null;
	}
		
	public void getObjects(HttpServletRequest request,
			HttpServletResponse response, RESTParameters params)
			throws IOException {

		if (params.getObjectURI().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Need an object class");
		}
		
		String query = QUERY_PREFIX + 
				"CONSTRUCT { ?url ?relationTwo ?second . } "
				+ "WHERE { "
				+ "   OPTIONAL { ?url ?relationTwo ?second . } "
				+ "   { SELECT ?url WHERE { ?url a ?class . } LIMIT 10 } "
				+ "}"; //, params.getPage()

		logger.info(query);
		
		try {
			final OutputStream out = response.getOutputStream();
			final QueryTask queryTask = context.getQueryTask(query,
					config.getString("platform.baseUri"), RDFJSONFormat.MIMETYPE, out);
			final FutureTask<Void> ft = new FutureTask<Void>(queryTask);

			queryTask.setBinding("class", createURI(params.getObjectURI()));
			
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

	private String addPaging(String query, long count, long page) {
		// TODO: check if count & page are valid
		return query + " LIMIT "+ count + " OFFSET " + count * page;
	}

	public Set<?> getEvents(XMLGregorianCalendar dateTimeFrom,
			XMLGregorianCalendar dateTimeTo) throws MalformedQueryException, RepositoryException, QueryEvaluationException {
		
		ObjectQuery query = conn.prepareObjectQuery(QueryLanguage.SPARQL, 
				"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" + 
				"PREFIX time: <http://www.w3.org/2006/time#>\n" + 
				"SELECT DISTINCT ?instance WHERE { " +
				"	?instance time:hasBeginning ?datePub" +
				"	FILTER(?datePub >= ?dtFrom && ?datePub < ?dtTo)." +
				"} ORDER BY DESC(?datePub) LIMIT 10"
			);
		
//			query.setBinding("dtFrom", conn.getValueFactory().createLiteral(
//					XMLDatatypeUtil.parseCalendar("2009-01-01T17:00:00Z")));
//			query.setBinding("dtTo", conn.getValueFactory().createLiteral(
//					XMLDatatypeUtil.parseCalendar("2014-02-01T17:00:00Z")));
		
		query.setBinding("dtFrom", conn.getValueFactory().createLiteral(dateTimeFrom));
		query.setBinding("dtTo", conn.getValueFactory().createLiteral(dateTimeTo));
		
		return query.evaluate().asSet();		

	}

	public JSONPagedResultSet getLinkedObjects(URI objectURI, URI classURI, long count, long page, String lang) {
		
		conn.setLanguage(lang);
		long total = getLinkedObjectCount(objectURI, classURI);
		
		try {			
			ObjectQuery query = conn.prepareObjectQuery(QueryLanguage.SPARQL,
					QUERY_PREFIX + addPaging(QUERY_LINKED_OBJECTS_BY_CLASS, count, page));
			
			query.setBinding("object", objectURI);
			query.setBinding("class", classURI);
			
			@SuppressWarnings("unchecked")
			Set<AHRDFObject> results = (Set<AHRDFObject>) query.evaluate().asSet();
			return new JSONPagedResultSet(results, page * count, total);
			
		} catch (Exception e) {			
			e.printStackTrace();
		}
		
		return null;
			
	}

}
