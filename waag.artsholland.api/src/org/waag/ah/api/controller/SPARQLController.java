package org.waag.ah.api.controller;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.waag.ah.api.service.SPARQLService;

@Controller
public class SPARQLController {

	//private Logger logger = Logger.getLogger(SPARQLController.class);

	@Resource(name = "sparqlService")
	private SPARQLService sparqlService;
	
	@RequestMapping(value = "/sparql", method = RequestMethod.POST, produces = "application/sparql-results+json")	
	public void bert(final HttpServletRequest request,
			final HttpServletResponse response,
			@RequestParam("query") String query) {      	
		try {
			sparqlService.tupleQuery(request, response, query);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

   	@RequestMapping(value = "/sparql", method = RequestMethod.GET)
	public void getQueryForm(final HttpServletRequest request,
			final HttpServletResponse response) {		
   		try {
   			/*
   			 * TODO: get url from properties? put in web.xml?
   			 */
			response.sendRedirect("/snorql");
		} catch (IOException e) {			
			e.printStackTrace();
		}   		
	}

	@RequestMapping(value = "/sparql-proxy", method = RequestMethod.POST, produces = "application/json;application/xml")
	public void postQuery(final HttpServletRequest request,
			final HttpServletResponse response,
			@RequestParam("query") String query) {
		sparqlService.proxyQuery(request, response, query);
	}
}