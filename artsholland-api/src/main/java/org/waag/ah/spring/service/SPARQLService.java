package org.waag.ah.spring.service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;

import javax.ejb.EJB;
import javax.naming.OperationNotSupportedException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openrdf.query.MalformedQueryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.waag.ah.bigdata.BigdataQueryService;
import org.waag.ah.bigdata.BigdataQueryService.QueryTask;
import org.waag.ah.rest.util.MIMEParse;

import com.bigdata.journal.TimestampUtility;

@Service(value="sparqlService")
public class SPARQLService {
	private static final Logger logger = LoggerFactory.getLogger(SPARQLService.class);
	
	@EJB(mappedName="java:app/datastore/BigdataQueryService")
	private BigdataQueryService context;

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
    		final OutputStream out = response.getOutputStream();
    		final String query = request.getParameter("query");
            final String baseURI = request.getRequestURL().toString();
//          final boolean explain = request.getParameter(BigdataQueryService.EXPLAIN) != null;
//          final String timestamp = request.getParameter("timestamp");

            final QueryTask queryTask;
            try {
                queryTask = context.getQueryTask(query, baseURI, mimeType, out);
            } catch (MalformedQueryException ex) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        ex.getLocalizedMessage());
                return;
            }
            
            final FutureTask<Void> ft = new FutureTask<Void>(queryTask);

            if (logger.isTraceEnabled()) {
                logger.trace("Will run query: " + query);
            }
            
            response.setStatus(HttpServletResponse.SC_OK);

            if (queryTask.getExplain()) {
//                response.setContentType(BigdataServlet.MIME_TEXT_HTML);
//                final Writer w = new OutputStreamWriter(os, queryTask.getCharset());
//                try {
//                    // Begin executing the query (asynchronous)
//                    getBigdataRDFContextWrapper().queryService.execute(ft);
//                    // Send an explanation instead of the query results.
//                    explainQuery(query, queryTask, ft, w);
//                } finally {
//                    w.flush();
//                    w.close();
//                    os.flush();
//                    os.close();
//                }
            	throw new OperationNotSupportedException("Explain queries are not yet supported.");
            } else {
                response.setContentType(queryTask.getMimeType());
                if (queryTask.getCharset() != null) {
                    response.setCharacterEncoding(queryTask.getCharset().name());
                }
                if (isAttachment(queryTask.getMimeType())) {
                    response.setHeader("Content-disposition",
                            "attachment; filename=query" + queryTask.getQueryId()
                                    + "." + queryTask.getFileExt());
                }
                if (TimestampUtility.isCommitTime(queryTask.getTimestamp())) {
                    response.addHeader("Cache-Control", "public");
                    // response.addHeader("Cache-Control", "no-cache");
                }
                context.executeQueryTask(ft);
                ft.get();
            }
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
					e.getLocalizedMessage());
		}		
	}
	
    private boolean isAttachment(final String mimeType) {
        if(mimeType.equals(MIME_TEXT_PLAIN)) {
            return false;
        } else if(mimeType.equals(MIME_SPARQL_RESULTS_XML)) {
            return false;
        } else if(mimeType.equals(MIME_SPARQL_RESULTS_JSON)) {
            return false;
        } else if(mimeType.equals(MIME_APPLICATION_XML)) {
            return false;
        }
        return true;
    }
    
//	public void proxyQuery(HttpServletRequest request,
//			HttpServletResponse response, String query) {
//		try {
//			URL url = new URL(SPARQL_ENDPOINT.expand(query).encode().toUriString());
//			response.setContentType("application/xml");
//			URLConnection conn = url.openConnection();
//			IOUtils.copy(conn.getInputStream(), response.getOutputStream());
//		} catch (MalformedURLException e) {
//			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//		} catch (IOException e) {
//			response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
//		}
//	}
}
