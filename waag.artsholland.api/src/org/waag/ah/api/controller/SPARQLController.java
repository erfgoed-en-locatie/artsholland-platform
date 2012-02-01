package org.waag.ah.api.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.logging.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.waag.ah.api.service.SPARQLService;


@Controller
public class SPARQLController {
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(SPARQLController.class);
	  
	@Resource(name="sparqlService")
	private SPARQLService sparqlService;
	
	@RequestMapping(value="/sparql", method=RequestMethod.GET) 
	public String getQueryForm() {
      return "sparql";
	}
	
	@RequestMapping(value="/sparql", method=RequestMethod.POST,
			produces="application/json;application/xml") 
	public void postQuery(final HttpServletRequest request,
			final HttpServletResponse response, 
			@RequestParam("query") String query) {
		sparqlService.proxyQuery(request, response, query);
	}
}