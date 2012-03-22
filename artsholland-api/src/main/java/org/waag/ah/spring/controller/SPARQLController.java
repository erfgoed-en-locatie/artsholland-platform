package org.waag.ah.spring.controller;

import java.io.IOException;
import java.util.Enumeration;
import java.util.concurrent.ExecutionException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.waag.ah.spring.service.SPARQLService;

@Secured("ROLE_API_USER")
@Controller
public class SPARQLController {
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(SPARQLController.class);
	private static final String MAPPING = "/sparql";
    
	@Resource(name="sparqlService")
	private SPARQLService sparqlService;
	
	@RequestMapping(value = "/sparql", method = RequestMethod.GET, headers = "Accept=text/html")
	public ModelAndView getSnorql(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException { 
		return new ModelAndView("snorql");
	}
	
	@RequestMapping(value = MAPPING, method = RequestMethod.POST)	
	public void postQuery(final HttpServletRequest request,
			final HttpServletResponse response)
			throws InterruptedException, ExecutionException, IOException {
		sparqlService.query(request, response);	
	}
	
	@RequestMapping(value = MAPPING, method = RequestMethod.GET) //, headers = "Accept=application/*"
	public void getQuery(final HttpServletRequest request,
			final HttpServletResponse response)
			throws InterruptedException, ExecutionException, IOException  {      	
		sparqlService.query(request, response);	
	}
}
