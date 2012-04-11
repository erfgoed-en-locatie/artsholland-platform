package org.waag.ah.spring.controller;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openrdf.query.MalformedQueryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.waag.ah.rest.RestParameters;
import org.waag.ah.spring.annotation.RestRequestParameters;
import org.waag.ah.spring.service.RestService;
import org.waag.ah.spring.view.QueryTaskView;

@Controller
public class RestController { // implements InitializingBean
	final static Logger logger = LoggerFactory.getLogger(RestController.class);

	@Resource(name = "restService")
	private RestService restService;

	@Autowired
	private QueryTaskView view;
	
	@RequestMapping(value="/data/*/*", method=RequestMethod.GET)
	public ModelAndView getObjectByUri(
			final HttpServletRequest request,
			final HttpServletResponse response,
			@RestRequestParameters(prefixLength=1) RestParameters params)
			throws IOException {
		try {
			return new ModelAndView(view, QueryTaskView.MODEL_QUERY,
					restService.getObjectQuery(params));
		} catch (MalformedQueryException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		}
		return null;
	}

	@RequestMapping(value="/rest/**", method=RequestMethod.GET)
	public ModelAndView restRequest(
			final HttpServletRequest request,
			final HttpServletResponse response,
			@RestRequestParameters(prefixLength=1, paging=true) RestParameters params)
			throws IOException {
		try {
			return new ModelAndView(view, QueryTaskView.MODEL_QUERY,
					restService.getPagedQuery(params));
		} catch (MalformedQueryException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		}
		return null;
	}
}
