	package org.waag.ah.spring.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openrdf.rio.RDFFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.waag.ah.rest.RDFJSONFormat;
import org.waag.ah.rest.RESTParametersImpl;
import org.waag.ah.spring.service.RestService;

@Controller
public class RestController {

	private static final String MAPPING = "/rest/";
		
	@Resource(name = "restService")
	private RestService restService;
	
	public RestController() {
    	RDFFormat.register(RDFJSONFormat.RESTAPIJSON);    	
	}	
	
//	@RequestMapping(value = MAPPING + "datumtest", method = RequestMethod.GET)	
//	public ModelAndView testDate(
//			final HttpServletRequest request,	final HttpServletResponse response,			
//			@RequestParam(value="from", defaultValue="1970-01-01T00:00:00Z", required=false) String dtFrom,
//			@RequestParam(value="to", defaultValue="2050-01-01T17:00:00Z", required=false) String dtTo) throws MalformedQueryException, RepositoryException, QueryEvaluationException {	
//		
//		Set<?> result = restService.getEvents(XMLDatatypeUtil.parseCalendar(dtFrom), XMLDatatypeUtil.parseCalendar(dtTo));
//		return modelAndView(result);
//		
//	}	
	
	@RequestMapping(value = MAPPING + "**", method = RequestMethod.GET)
	public void restRequest(
		final HttpServletRequest request,
		final HttpServletResponse response,
		@RequestParam(value = "limit", defaultValue = "10", required = false) long limit,
		@RequestParam(value = "page", defaultValue = "1", required = false) long page,
		@RequestParam(value = "lang", defaultValue = "nl", required = false) String lang) throws IOException {

		RESTParametersImpl params = new RESTParametersImpl();
		
		params.setResultLimit(limit);
		params.setPage(page);
		params.setLanguage(lang);
		params.setSplitPath(new LinkedList<String>(Arrays.asList(request.getRequestURI().substring(6).split("/"))));
		
		restService.restRequest(request, response, params);
	}	

//	@RequestMapping(value = MAPPING + "geo", method = RequestMethod.GET)	
//	public @ResponseBody String getGeo(final HttpServletRequest request,
//			final HttpServletResponse response) throws IOException  {		
//		return "Not yet implemented";
//	}

}