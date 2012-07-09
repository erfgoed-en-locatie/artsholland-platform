package org.waag.ah.spring.service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.parser.ParsedBooleanQuery;
import org.openrdf.query.parser.ParsedGraphQuery;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.ParsedTupleQuery;
import org.openrdf.query.parser.QueryParser;
import org.openrdf.query.parser.sparql.SPARQLParserFactory;
import org.openrdf.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.waag.ah.QueryDefinition;
import org.waag.ah.QueryTask;
import org.waag.ah.RepositoryConnectionFactory;
import org.waag.ah.WriterConfig;
import org.waag.ah.rdf.AskQueryTask;
import org.waag.ah.rdf.GraphQueryTask;
import org.waag.ah.rdf.RDFWriterConfig;
import org.waag.ah.rdf.RdfQueryDefinition;
import org.waag.ah.rdf.TupleQueryTask;
import org.waag.ah.rest.util.MIMEParse;

@Service(value="sparqlService")
public class SPARQLService {
	private static final Logger logger = LoggerFactory.getLogger(SPARQLService.class);
	
	@EJB(mappedName="java:app/datastore/BigdataConnectionService")
	private RepositoryConnectionFactory cf;
	
//	@EJB(mappedName="java:app/datastore/BigdataQueryService")
//	private QueryService context;

	private ExecutorService executor;

	public static final transient String
		MIME_TEXT_PLAIN 			= "text/plain",
		MIME_APPLICATION_XML 		= "application/xml",
		MIME_APPLICATION_RDF_XML 	= "application/rdf+xml",
		MIME_APPLICATION_JSON 		= "application/json",
		MIME_APPLICATION_RDF_JSON 	= "application/rdf+json",
		MIME_SPARQL_RESULTS_XML 	= "application/sparql-results+xml",
		MIME_SPARQL_RESULTS_JSON 	= "application/sparql-results+json";
	
	private static List<String> allowedFormats = Arrays.asList(
			MIME_APPLICATION_XML,
			MIME_APPLICATION_RDF_XML,
			MIME_APPLICATION_JSON,
			MIME_APPLICATION_RDF_JSON,
			MIME_SPARQL_RESULTS_JSON,
			MIME_SPARQL_RESULTS_XML);
	
	private static Map<String, String> mappedFormats = new HashMap<String, String>();
	static {
		mappedFormats.put(MIME_APPLICATION_JSON, MIME_SPARQL_RESULTS_JSON);
		mappedFormats.put(MIME_APPLICATION_RDF_JSON, MIME_SPARQL_RESULTS_JSON);
		mappedFormats.put(MIME_APPLICATION_XML, MIME_SPARQL_RESULTS_XML);
		mappedFormats.put(MIME_APPLICATION_RDF_XML, MIME_SPARQL_RESULTS_XML);
	}
	
	@PostConstruct
	public void init() {
		this.executor = Executors.newCachedThreadPool();
	}
	
	public void query(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String mimeType = MIMEParse.bestMatch(allowedFormats, request.getHeader("Accept"));
        
        if (mappedFormats.containsKey(mimeType)) {
        	mimeType = mappedFormats.get(mimeType);
        }
        
        if (mimeType.isEmpty()) {
        	response.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
        	return;
        }
        
        try {
        	RDFWriterConfig config = new RDFWriterConfig();
        	config.setBaseUri(request.getRequestURL().toString());
        	config.setFormat(mimeType);
        	
			RdfQueryDefinition query = new RdfQueryDefinition(
					QueryLanguage.SPARQL, request.getParameter("query"));
        	
			ServletOutputStream out = response.getOutputStream();
			final QueryTask queryTask = getQueryTask(query, config, out);
            response.setStatus(HttpServletResponse.SC_OK);

            Future<Void> ft = executor.submit(queryTask);
            
            try {
            	ft.get(30, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
            	logger.error("Query execution timeout: "+query.getQuery());
            	ft.cancel(true);
    			response.sendError(HttpServletResponse.SC_REQUEST_TIMEOUT,
    					e.getMessage());	
    		}
        } catch (MalformedQueryException ex) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,
					ex.getLocalizedMessage());
		} catch (Exception e) {
//			e.getCause().printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					e.getCause().getMessage());
		} finally {
			
		}
	}
	
//	@Override
	public QueryTask getQueryTask(QueryDefinition query,
			WriterConfig config, OutputStream out)
			throws MalformedQueryException {
		QueryParser parser = new SPARQLParserFactory().getParser();
		ParsedQuery parsedQuery = parser.parseQuery(query.getQuery(),
				config.getBaseUri());
		try {
			RepositoryConnection conn = cf.getConnection();
			if (parsedQuery instanceof ParsedTupleQuery) {
				return new TupleQueryTask(conn, query, config, out);
			} else if (parsedQuery instanceof ParsedBooleanQuery) {
				return new AskQueryTask(conn, query, config, out);
			} else if (parsedQuery instanceof ParsedGraphQuery) {
				return new GraphQueryTask(conn, query, config, out);
			}			
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		
		throw new MalformedQueryException("Unknown query type: "
				+ ParsedQuery.class.getName());
	}	
}
