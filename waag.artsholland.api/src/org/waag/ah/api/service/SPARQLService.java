package org.waag.ah.api.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.jboss.logging.Logger;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.query.resultio.sparqljson.SPARQLResultsJSONWriter;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.bigdata.rdf.sail.webapp.QueryServlet;

@Service("sparqlService")
public class SPARQLService extends QueryServlet {
	private static final long serialVersionUID = 5520647237936009532L;
	private Logger logger = Logger.getLogger(SPARQLService.class);
	private UriComponents SPARQL_ENDPOINT;
	
	@Autowired
	private RepositoryConnection connection;
	
	public SPARQLService() {
		SPARQL_ENDPOINT = UriComponentsBuilder
				.fromUriString("http://127.0.0.1:8080/sparql?query={query}")
				.build();
	}
	
//		
//
//	public SPARQLService() throws NamingException, RepositoryException, IOException {
//		InitialContext context = new InitialContext();
//		RepositoryConnectionFactory connectionFactory = (RepositoryConnectionFactory) 
//				context.lookup("java:global/SPARQLConnectionFactory");
//		connection = connectionFactory.getConnection();
//	}
//
//	public void query(String query) throws RepositoryException, 
//			MalformedQueryException, QueryEvaluationException, RDFHandlerException {
//		GraphQuery graphQuery = connection.prepareGraphQuery(
//				QueryLanguage.SPARQL, query);
//		RDFHTMLHandler resultHandler = new RDFHTMLHandler();
//		graphQuery.evaluate(resultHandler);
//	}
//	
//	private static class RDFHTMLHandler extends RDFHandlerBase {
//		
//	}
	
	public void tupleQuery(HttpServletRequest request,
		HttpServletResponse response, String query) throws RepositoryException, 
		MalformedQueryException, QueryEvaluationException, IOException, TupleQueryResultHandlerException {
	
		TupleQuery tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, query);			
		TupleQueryResult tupleQueryResult = tupleQuery.evaluate();
				
		SPARQLResultsJSONWriter resultsWriter = new SPARQLResultsJSONWriter(response.getOutputStream());
							
		/*
		 * TODO: grijp uit properties
		 */
		Integer limit = 50; 
		resultsWriter.startQueryResult(tupleQueryResult.getBindingNames());
        try {
            for (int i=0; tupleQueryResult.hasNext() && (limit == null || i < limit.intValue()); i++) {
                BindingSet bindingSet = tupleQueryResult.next();
                resultsWriter.handleSolution(bindingSet);
            }
        }
        finally {
            tupleQueryResult.close();
        }
        resultsWriter.endQueryResult();
	}
	

	public void proxyQuery(HttpServletRequest request,
			HttpServletResponse response, String query) {
		try {
			URL url = new URL(SPARQL_ENDPOINT.expand(query).encode().toUriString());
			logger.info(url.toExternalForm());
			response.setContentType("application/xml");
			URLConnection conn = url.openConnection();
			IOUtils.copy(conn.getInputStream(), response.getOutputStream());
		} catch (MalformedURLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} catch (IOException e) {
			response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
		}
	}
}