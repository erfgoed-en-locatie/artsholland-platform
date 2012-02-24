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
import org.springframework.web.servlet.ModelAndView;
import org.waag.ah.api.service.SPARQLService;

@Controller
public class SPARQLController {
	private Logger logger = Logger.getLogger(SPARQLController.class);

	private static final String MAPPING = "/sparql";
	
	@Resource(name="sparqlService")
	private SPARQLService sparqlService;
	
	@RequestMapping(value = MAPPING, method = RequestMethod.POST /*, params = "query"*/)	
	public void postQuery(final HttpServletRequest request,
			final HttpServletResponse response,
			@RequestParam("query") String query, @RequestHeader("Accept") String accept) throws InterruptedException, ExecutionException, IOException  {      	
		sparqlService.tupleQuery(request, response, query, accept);	
	}
	
	@RequestMapping(value = MAPPING, method = RequestMethod.GET /*, params = "query"*/, headers = "Accept=application/*")
	public void getQuery(final HttpServletRequest request,
			final HttpServletResponse response,
			@RequestParam("query") String query, @RequestHeader("Accept") String accept) throws InterruptedException, ExecutionException, IOException  {      	
		sparqlService.tupleQuery(request, response, query, accept);	
	}
	
	@RequestMapping(value = MAPPING, method = RequestMethod.GET, params = "output")
	public void getQueryOutput(final HttpServletRequest request,
			final HttpServletResponse response,
			@RequestParam("query") String query, @RequestParam("output") String output) throws InterruptedException, ExecutionException, IOException  {
		// TODO: output=json, also support output=xml
		sparqlService.tupleQuery(request, response, query, SPARQLService.MIME_SPARQL_RESULTS_JSON);	
	}
	
	
	@RequestMapping(value = "/sparql", method = RequestMethod.GET, headers = "Accept=text/html")
	public ModelAndView getSnorql(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException { 
		return new ModelAndView("snorql");
	}	

}