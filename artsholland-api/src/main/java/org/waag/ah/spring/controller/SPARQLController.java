package org.waag.ah.spring.controller;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.waag.ah.rest.SPARQLParameters;
import org.waag.ah.spring.annotation.SPARQLRequestParameters;
import org.waag.ah.spring.service.SPARQLService;
import org.waag.ah.spring.view.QueryTaskView;

@Secured("ROLE_API_USER")
@Controller
public class SPARQLController {
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(SPARQLController.class);
	private static final String MAPPING = "/sparql";
    
	@Resource(name="sparqlService")
	private SPARQLService sparqlService;
	
	@Autowired
	private QueryTaskView view;
	
	@RequestMapping(value = MAPPING, method = RequestMethod.GET, headers = "Accept=text/html")
	public ModelAndView getSnorql(
			final HttpServletRequest request,
			final HttpServletResponse response,
			@SPARQLRequestParameters() SPARQLParameters params
			)	throws IOException { 
		String extension = QueryTaskView.getExtension(request);
		if (extension == null) {
			return new ModelAndView("snorql");
		} else {
			return doQuery(request, response, params);
		}
	}
	
	@RequestMapping(value = MAPPING)	
	public ModelAndView doQuery(
			final HttpServletRequest request,
			final HttpServletResponse response,
			@SPARQLRequestParameters() SPARQLParameters params
			) throws IOException {		
		return new ModelAndView(view, QueryTaskView.MODEL_QUERY, sparqlService.getQuery(params));		
	}
	
	
	/*@RequestMapping(value = MAPPING, method = RequestMethod.POST)	
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
	}*/
}
