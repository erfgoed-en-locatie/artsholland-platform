package org.waag.ah.api.controller;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.waag.ah.api.service.UitbaseService;
import org.jboss.logging.Logger;

@Controller
public class UitbaseController {
	private Logger logger = Logger.getLogger(UitbaseController.class);

	@Resource(name = "uitbaseService")
	private UitbaseService uitbaseService;
	
	@RequestMapping(value = "/uitbase", method = RequestMethod.GET)	
	public void proxy(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException  {		
		uitbaseService.proxyQuery(request, response);
	}

	/*
	@RequestMapping(value = "/sparql-proxy", method = RequestMethod.POST, produces = "application/json;application/xml")
	public void mongo(final HttpServletRequest request,
			final HttpServletResponse response,
			@RequestParam("query") String query) {
		sparqlService.proxyQuery(request, response, query);
	}*/
}