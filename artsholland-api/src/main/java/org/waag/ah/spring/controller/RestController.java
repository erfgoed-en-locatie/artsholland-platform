package org.waag.ah.spring.controller;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.waag.ah.rest.RDFJSONFormat;
import org.waag.ah.rest.RESTParametersImpl;
import org.waag.ah.spring.annotation.RestRequestParameters;
import org.waag.ah.spring.service.RestService;

@Controller
public class RestController {
	final static Logger logger = LoggerFactory.getLogger(RestController.class);

	@Resource(name = "restService")
	private RestService restService;

	@Autowired
	PropertiesConfiguration config;

	public RestController() {
		RDFFormat.register(RDFJSONFormat.RESTAPIJSON);
	}

	@RequestMapping(value="/data/*/*", method=RequestMethod.GET)
	public void getObjectByUri(
			final HttpServletRequest request,
			final HttpServletResponse response,
			@RestRequestParameters(prefixLength=1) RESTParametersImpl params)
			throws IOException {
		restService.restRequest(params, response);
	}

	@RequestMapping(value="/rest/**", method=RequestMethod.GET)
	public void restRequest(
			final HttpServletRequest request,
			final HttpServletResponse response,
			@RestRequestParameters(prefixLength=1, paging=true) RESTParametersImpl params)
			throws IOException {
		restService.restRequest(params, response);
	}
}
