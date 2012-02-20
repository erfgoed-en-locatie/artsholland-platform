package org.waag.ah.api.service;

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;

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
import org.openrdf.query.resultio.TupleQueryResultWriter;
import org.openrdf.query.resultio.sparqljson.SPARQLResultsJSONWriter;
import org.openrdf.query.resultio.sparqlxml.SPARQLResultsXMLWriter;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;

@Service("sparqlService")
public class SPARQLService {
	private Logger logger = Logger.getLogger(SPARQLService.class);
	private UriComponents SPARQL_ENDPOINT;
	
	/**
	 * Common MIME types for dynamic content.
	 */
	public static final transient String
		MIME_APPLICATION_RDF_XML = "application/rdf+xml",
		MIME_APPLICATION_RDF_JSON = "application/rdf+json",
		MIME_SPARQL_RESULTS_XML = "application/sparql-results+xml",
  	MIME_SPARQL_RESULTS_JSON = "application/sparql-results+json";

	@Autowired
	private RepositoryConnection connection;

	private ExecutorService executor;
	
	public SPARQLService() {
		executor = new ScheduledThreadPoolExecutor(5);
		//SPARQL_ENDPOINT = UriComponentsBuilder.fromUriString(
		//		"http://127.0.0.1:8080/sparql?query={query}").build();

	}
	
	
	//
	//
	// public SPARQLService() throws NamingException, RepositoryException,
	// IOException {
	// InitialContext context = new InitialContext();
	// RepositoryConnectionFactory connectionFactory =
	// (RepositoryConnectionFactory)
	// context.lookup("java:global/SPARQLConnectionFactory");
	// connection = connectionFactory.getConnection();
	// }
	//
	// public void query(String query) throws RepositoryException,
	// MalformedQueryException, QueryEvaluationException, RDFHandlerException {
	// GraphQuery graphQuery = connection.prepareGraphQuery(
	// QueryLanguage.SPARQL, query);
	// RDFHTMLHandler resultHandler = new RDFHTMLHandler();
	// graphQuery.evaluate(resultHandler);
	// }
	//
	// private static class RDFHTMLHandler extends RDFHandlerBase {
	//
	// }	
	
	
	private class QueryTask implements Callable<Integer> {

		private String query;
		private String accept;
		private OutputStream outputStream;
		
		QueryTask(HttpServletRequest request,	HttpServletResponse response, String query, String accept) throws IOException{
		  this.query = query;
		  this.accept = accept;
		  this.outputStream = response.getOutputStream();
		}

		public Integer call() {
		  
			TupleQuery tupleQuery;			
			try {
				
				//Query preparedQuery = connection.prepareQuery(QueryLanguage.SPARQL, query);				
				//queryResult = preparedQuery.
				
				/*BooleanQuery vis = connection.prepareBooleanQuery(QueryLanguage.SPARQL, query);
				GraphQuery koek = connection.prepareGraphQuery(QueryLanguage.SPARQL, query);
				GraphQueryResult chips = koek.evaluate();
				boolean hond = vis.evaluate();*/
				
				tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, query);
				TupleQueryResult tupleQueryResult = tupleQuery.evaluate();
				
				TupleQueryResultWriter resultsWriter = null;
				if(accept.contains(MIME_SPARQL_RESULTS_XML)) {
					resultsWriter = new SPARQLResultsXMLWriter(outputStream);
				} else if(accept.contains(MIME_SPARQL_RESULTS_JSON)) {
					resultsWriter = new SPARQLResultsJSONWriter(outputStream);
				}
				
				if (resultsWriter != null) {
					// TODO: grijp uit properties
					Integer limit = 5000;
					resultsWriter.startQueryResult(tupleQueryResult.getBindingNames());
					try {
						for (int i = 0; tupleQueryResult.hasNext()
								&& (limit == null || i < limit.intValue()); i++) {
							BindingSet bindingSet = tupleQueryResult.next();
							resultsWriter.handleSolution(bindingSet);
						}
					} finally {
						tupleQueryResult.close();
					}
					resultsWriter.endQueryResult();
				}
				
			} catch (RepositoryException e) {
				e.printStackTrace();
			} catch (MalformedQueryException e) {
				e.printStackTrace();
			} catch (QueryEvaluationException e) {				
				e.printStackTrace();
			} catch (TupleQueryResultHandlerException e) {
				e.printStackTrace();
			}	
			
			return 0;
			
		}
		
	}  


	public void tupleQuery(HttpServletRequest request,
			HttpServletResponse response, String query, String accept) throws InterruptedException, ExecutionException, IOException {

		Callable<Integer> callable = new QueryTask(request, response, query, accept);	  
	  Future<Integer> future = executor.submit(callable);
	  future.get();	  
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