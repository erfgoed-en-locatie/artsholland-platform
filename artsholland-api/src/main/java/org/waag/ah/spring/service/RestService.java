package org.waag.ah.spring.service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.ejb.EJB;
import javax.xml.datatype.XMLGregorianCalendar;

import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectQuery;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.waag.ah.ObjectConnectionFactory;
import org.waag.ah.jackson.JSONPagedResultSet;
import org.waag.ah.model.rdf.AHRDFObject;

@Service("restService")
public class RestService implements InitializingBean, DisposableBean {

	@EJB(mappedName = "java:app/datastore/ObjectConnectionService")
	private ObjectConnectionFactory connFactory;
	private ObjectConnection conn;

	@Override
	public void afterPropertiesSet() throws Exception {
		conn = connFactory.getObjectConnection();
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
		"PREFIX ah: <http://data.artsholland.com/>\n";
	
	private static final String NAMESPACE = "http://data.artsholland.com/";

	
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
		return conn.getValueFactory().createURI(NAMESPACE + uriStringWithoutNamespace);
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

	public String testObject() throws MalformedQueryException, RepositoryException, QueryEvaluationException {

		ArrayList<String> strings = new ArrayList<String>();

		ObjectQuery query = conn.prepareObjectQuery(QueryLanguage.SPARQL,
				QUERY_PREFIX + addPaging(QUERY_OBJECTS_BY_CLASS, 100, 0));
		
		query.setBinding("class", createURI("Event"));			
		
		@SuppressWarnings("unchecked")
		Set<AHRDFObject> results = (Set<AHRDFObject>) query.evaluate().asSet();
		for (AHRDFObject result: results) {
			strings.add(result.getURI());
		}
		

		
		return strings.toString();
	}
	
	public Object testTuple() throws MalformedQueryException, RepositoryException, QueryEvaluationException {
		
		String vis = "SELECT DISTINCT ?s ?p ?o WHERE { ?s ?p ?o. ?s a ah:Event.} LIMIT 1000";
		Set<String> strings = new LinkedHashSet<String>();
		
		TupleQuery query = conn.prepareTupleQuery(QueryLanguage.SPARQL, QUERY_PREFIX + vis);
		
		TupleQueryResult result = query.evaluate();
		
		while (result.hasNext()) {
			BindingSet paard = result.next();
			
			strings.add(paard.getBinding("s") + " met de " + paard.getBinding("p") + " is een " + paard.getBinding("o"));
		}
		
		return strings;
		
	}

}
