package org.waag.ah.api.service;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.query.resultio.TupleQueryResultWriter;
import org.openrdf.query.resultio.sparqljson.SPARQLResultsJSONWriter;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectQuery;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.waag.ah.ObjectConnectionFactory;

@Service("restService")
public class RestService implements InitializingBean, DisposableBean {

	@EJB(mappedName = "java:app/datastore/ObjectConnectionFactoryImpl")
	private ObjectConnectionFactory connFactory;
	private ObjectConnection conn;

	@Override
	public void afterPropertiesSet() throws Exception {
		conn = connFactory.getObjectConnection();
		conn.setIncludeInferred(true);
		
		/*
		ObjectFactory of = conn.getObjectFactory();
		conn.setIncludeInferred(includeInferred)
		conn.isIncludeInferred()
		Room entity = conn.addDesignation(of.createObject(), Room.class);*/
	
		
	}

	@Override
	public void destroy() throws Exception {
		conn.close();
	}
	
	/*
	 * TODO: move to controller
	 */
	private static final Map<String, String> CLASS_MAP = createMap();
	private static Map<String, String> createMap() {
      Map<String, String> result = new HashMap<String, String>();
      
      result.put("events", "Event");
      result.put("venues", "Venue");
      result.put("rooms", "Room");
      result.put("productions", "Production");      
      
      /*
      Attachment 
      Event 
      EventStatus 
      EventType 
      Genre 
      Production 
      ProductionType 
      Room 
      Venue 
      VenueType 
      Offering 
      UnitPriceSpecification 
      */      
      
      return Collections.unmodifiableMap(result);
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
	
	private static final String uriPrefix = "http://data.artsholland.com/";

	private static final String classQueryString = "SELECT DISTINCT ?property ?hasValue ?isValueOf\n"
			+ "WHERE {\n"
			+ "  { ?c ?property ?hasValue }\n"
			+ "  UNION\n"
			+ "  { ?isValueOf ?property ?c }\n" + "}\n";
	
	private static final String QUERY_GET_CLASS = "SELECT DISTINCT ?instance \n"
			+ "WHERE { ?instance a ?class . } ORDER BY ?instance";



	public String alleStatements(int page, int count) throws RepositoryException {
		URI uri = new URIImpl("http://data.artsholland.com/Production");
		RepositoryResult<Statement> statements = conn.getStatements(null, null,
				uri, false);

		Statement statement = null;
		ArrayList<String> lines = new ArrayList<String>();
		int i = 0;
		while (statements.hasNext() && i < (page + 1) * count) {
			statement = statements.next();
			if (i >= page * count) {

				Resource resource = statement.getSubject();
				lines.add(resource.toString() + "\n");
			}
			i += 1;
		}
		return lines.toString();
	}
	

	public String query() throws TupleQueryResultHandlerException, QueryEvaluationException, MalformedQueryException, RepositoryException {
		
		TupleQuery query = conn.prepareTupleQuery(QueryLanguage.SPARQL,
				QUERY_PREFIX + classQueryString);

		// <http://data.artsholland.com/[[class]]s/[[id]]>
		URI uri = conn
				.getValueFactory()
				.createURI(
						"http://data.artsholland.com/productions/f297a29eb7da60d929a7ab30143083d7");
		// Literal limit = conn.getValueFactory().createLiteral(count);
		// Literal offset = conn.getValueFactory().createLiteral(page * count);

		query.setBinding("c", uri);
		// query.setBinding("l", limit);
		// query.setBinding("o", offset);		
		

		TupleQueryResult queryResult = query.evaluate();
		OutputStream outputStream = null;
		TupleQueryResultWriter resultsWriter = new SPARQLResultsJSONWriter(
				outputStream);

		if (resultsWriter != null) {
			resultsWriter.startQueryResult(queryResult.getBindingNames());
			try {
				while (queryResult.hasNext()) {
					BindingSet bindingSet = queryResult.next();
					resultsWriter.handleSolution(bindingSet);
				}
			} finally {
				queryResult.close();
			}
			resultsWriter.endQueryResult();
		}
		
		return null;
	}

	

	public Set<?> getList(String classname, int count, int page) {
		
		classname = CLASS_MAP.get(classname);
		String queryString = addPaging(QUERY_GET_CLASS, count, page).replace("?class", "<http://data.artsholland.com/" + classname + ">");		
		
		try {
			
			ObjectQuery query = conn.prepareObjectQuery(QueryLanguage.SPARQL,
					QUERY_PREFIX + queryString);
			
			return query.evaluate().asSet();	
		} catch (MalformedQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new HashSet<Object>();
		
	}

	private String addPaging(String query, int count, int page) {
		// TODO: check if count & page are valid
		return query + " LIMIT "+ count + " OFFSET " + count * page;
	}

}
