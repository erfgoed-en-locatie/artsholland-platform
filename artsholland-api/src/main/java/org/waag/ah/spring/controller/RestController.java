package org.waag.ah.spring.controller;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openrdf.query.MalformedQueryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.waag.ah.rdf.RdfQueryDefinition;
import org.waag.ah.rest.RestParameters;
import org.waag.ah.spring.annotation.RestRequestParameters;
import org.waag.ah.spring.service.RestService;

@Controller
public class RestController {
	final static Logger logger = LoggerFactory.getLogger(RestController.class);

	@Resource(name = "restService")
	private RestService restService;

	@RequestMapping(value="/data/*/*", method=RequestMethod.GET)
	public RdfQueryDefinition getObjectByUri(
			final HttpServletRequest request,
			final HttpServletResponse response,
			@RestRequestParameters(prefixLength=1) RestParameters params)
			throws IOException {
		try {
			return restService.getObjectQuery(params);
		} catch (MalformedQueryException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		}
		return null;
	}

	@RequestMapping(value="/rest/**", method=RequestMethod.GET)
	public RdfQueryDefinition restRequest(
			final HttpServletRequest request,
			final HttpServletResponse response,
			@RestRequestParameters(prefixLength=1, paging=true) RestParameters params)
			throws IOException {
		try {
			return restService.getPagedQuery(params);
		} catch (MalformedQueryException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		}
		return null;
	}
}
