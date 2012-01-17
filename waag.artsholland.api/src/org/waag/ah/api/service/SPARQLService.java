package org.waag.ah.api.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Service("sparqlService")
public class SPARQLService {
	private Logger logger = Logger.getLogger(SPARQLService.class);
	private UriComponents SPARQL_ENDPOINT;
	
	public SPARQLService() {
		SPARQL_ENDPOINT = UriComponentsBuilder
				.fromUriString("http://127.0.0.1:8080/bigdata/sparql?query={query}")
				.build();
	}
	
//	private RepositoryConnection connection;
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

	public void proxyQuery(HttpServletRequest request,
			HttpServletResponse response, String query) {
		try {
			URL url = new URL(SPARQL_ENDPOINT.expand(query).encode().toUriString());
			logger.info(url.toExternalForm());
			URLConnection conn = url.openConnection();
//			conn.setRequestProperty("Accept", request.getContentType());
//			conn.setRequestProperty("Accept", "application/x-turtle");
			IOUtils.copy(conn.getInputStream(), response.getOutputStream());
		} catch (MalformedURLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} catch (IOException e) {
			response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
		}
	}
}