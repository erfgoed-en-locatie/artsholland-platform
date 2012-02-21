package org.waag.ah.api.controller;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutionException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.jboss.logging.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.waag.ah.api.service.SPARQLService;

@Controller
public class SPARQLController {
	private Logger logger = Logger.getLogger(SPARQLController.class);

	@Resource(name="sparqlService")
	private SPARQLService sparqlService;
	
	@RequestMapping(value = "/sparql", method = RequestMethod.POST)	
	public void bert(final HttpServletRequest request,
			final HttpServletResponse response,
			@RequestParam("query") String query, @RequestHeader("Accept") String accept) throws InterruptedException, ExecutionException, IOException  {      	
		sparqlService.tupleQuery(request, response, query, accept);	
	}

	@RequestMapping(value = "/sparql", method = RequestMethod.GET, headers = "Accept=text/html")
	public void getQueryForm(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {   		
   		/*
   		 * TODO: get url from properties? put in web.xml?
   		 */
		//response.sendRedirect("/snorql");
		URL url = new URL("http://127.0.0.1:8080/snorql");		
		response.setContentType("text/html");
		URLConnection conn = url.openConnection();
		IOUtils.copy(conn.getInputStream(), response.getOutputStream()); 		
	}

//	@RequestMapping(value = "/sparql-proxy", method = RequestMethod.POST, produces = "application/json;application/xml")
//	public void postQuery(final HttpServletRequest request,
//			final HttpServletResponse response,
//			@RequestParam("query") String query) {
//		sparqlService.proxyQuery(request, response, query);
//	}
}