package org.waag.ah.api.service;

import java.io.ByteArrayOutputStream;

import javax.ejb.EJB;

import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.resultio.TupleQueryResultWriter;
import org.openrdf.query.resultio.sparqljson.SPARQLResultsJSONWriter;
import org.openrdf.repository.RepositoryConnection;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.waag.ah.RepositoryConnectionFactory;

@Service("apiService")
public class ApiService implements InitializingBean, DisposableBean  {

	private RepositoryConnection connection;

	@EJB(mappedName="java:app/datastore/SAILConnectionFactory")
	private RepositoryConnectionFactory connFactory;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		connection = connFactory.getReadOnlyConnection();
	}
	
	@Override
	public void destroy() throws Exception {
		connection.close();
	}
	
	private static final String prefix = 
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
		"PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
		"PREFIX dc: <http://purl.org/dc/elements/1.1/>\n" +		
		"PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
		"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n";

	private static final String eventQuery =
			"SELECT DISTINCT ?property ?hasValue ?isValueOf\n" +
			"WHERE {\n" +
			"  { <http://purl.org/artsholland/1.0/events/[[id]]> ?property ?hasValue }\n" +
			"  UNION\n" +
			"  { ?isValueOf ?property <http://purl.org/artsholland/1.0/events/[[id]]> }\n" +
			"}\n";			
	
	
	public String getEvent(String id) {
		// Class.getResource(String).
		
		String query = prefix + eventQuery.replace("[[id]]", id);
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		
		try {
			TupleQuery tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, query);
			TupleQueryResult tupleQueryResult = tupleQuery.evaluate();		
			TupleQueryResultWriter resultsWriter = new SPARQLResultsJSONWriter(outputStream);
					
			if (resultsWriter != null) {			
				resultsWriter.startQueryResult(tupleQueryResult.getBindingNames());
				try {
					while(tupleQueryResult.hasNext()) {
						BindingSet bindingSet = tupleQueryResult.next();
						resultsWriter.handleSolution(bindingSet);
					}
				} finally {
					tupleQueryResult.close();
				}
				resultsWriter.endQueryResult();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return outputStream.toString();
	}
	
/*
 * 		
		SELECT DISTINCT ?property ?hasValue ?isValueOf
				WHERE {
				  { <http://purl.org/artsholland/1.0/events/1f904b47-1b1a-4abc-9a8f-6746ce74904a> ?property ?hasValue }
				  UNION
				  { ?isValueOf ?property <http://purl.org/artsholland/1.0/events/1f904b47-1b1a-4abc-9a8f-6746ce74904a> }
				}
				ORDER BY (!BOUND(?hasValue)) ?property ?hasValue ?isValueOf
 */
	

}
