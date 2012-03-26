package org.waag.ah.spring.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openrdf.model.URI;
import org.openrdf.model.datatypes.XMLDatatypeUtil;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.waag.ah.jackson.JSONPagedResultSet;
import org.waag.ah.rest.RDFJSONFormat;
import org.waag.ah.rest.RESTParametersImpl;
import org.waag.ah.spring.service.RestService;

@Controller
public class RestController {

	private static final String MAPPING = "/rest/";
	
	private static final Map<String, String> CLASS_MAP = createMap();
	private static Map<String, String> createMap() {
			Map<String, String> result = new HashMap<String, String>();
	
			result.put("events", "Event");
			result.put("venues", "Venue");
			result.put("rooms", "Room");
			result.put("productions", "Production");
	
			return Collections.unmodifiableMap(result);
	}
	
	@Resource(name = "restService")
	private RestService restService;
	
	public RestController() {
    	RDFFormat.register(RDFJSONFormat.RESTAPIJSON);
	}

	private String getAHRDFClass(String pathVariable) {
		if (CLASS_MAP.containsKey(pathVariable)) {
			return CLASS_MAP.get(pathVariable);
		}
		return null;
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
	
//	@RequestMapping(value = MAPPING + "{class}/{cidn}", method = RequestMethod.GET)
//	public ModelAndView getObject(
//			final HttpServletRequest request,
//			final HttpServletResponse response,
//			@RequestParam(value = "lang", defaultValue = "nl", required = false) String lang,
//			@PathVariable("class") String classname,
//			@PathVariable("cidn") String cidn) throws IOException {
//
//		URI objectURI = restService.createURI(classname + "/" + cidn);
//
//		JSONPagedResultSet result = restService.getObject(objectURI, lang);
//		return modelAndView(result);
//	}
	
//	@RequestMapping(value = MAPPING + "{class}/{cidn}/{linkedClass}", method = RequestMethod.GET)
//	public ModelAndView getLinkedObjects(
//			final HttpServletRequest request,
//			final HttpServletResponse response,
//			@RequestParam(value = "lang", defaultValue = "nl", required = false) String lang,
//			@RequestParam(value = "count", defaultValue = "10", required = false) long count,
//			@RequestParam(value = "page", defaultValue = "0", required = false) long page,
//			@PathVariable("class") String classname,
//			@PathVariable("cidn") String cidn,
//			@PathVariable("linkedClass") String linkedClassname)
//			throws IOException {
//
//		URI objectURI = restService.createURI(classname + "/" + cidn);
//		URI classURI = restService.createURI(getAHRDFClass(linkedClassname));
//
//		JSONPagedResultSet result = restService.getLinkedObjects(objectURI,
//				classURI, count, page, lang);
//		return modelAndView(result);
//	}
	
//	@RequestMapping(value = MAPPING + "{class}", method = RequestMethod.GET)	
//	public ModelAndView getObjects(
//			final HttpServletRequest request,	final HttpServletResponse response, 
//			@PathVariable("class") String classname,
//			@RequestParam(value="lang", defaultValue="nl", required=false) String lang,
//			@RequestParam(value="count", defaultValue="10", required=false) long count, 
//			@RequestParam(value="page", defaultValue="0", required=false) long page) {	
//
//		URI classURI = restService.createURI(getAHRDFClass(classname));
//		
//		JSONPagedResultSet result = restService.getObjects(classURI, count, page, lang);		
//		return modelAndView(result);
//	}
	
	@RequestMapping(value = MAPPING + "{class}", method = RequestMethod.GET)	
	public void getObjects(final HttpServletRequest request, final HttpServletResponse response, 
			@PathVariable("class") String classname,
			@RequestParam(value="lang", defaultValue="nl", required=false) String lang,
			@RequestParam(value="limit", defaultValue="10", required=false) long count, 
			@RequestParam(value="page", defaultValue="0", required=false) long page) throws IOException {	
		RESTParametersImpl params = new RESTParametersImpl();
		
		params.setObjectURI(getAHRDFClass(classname));
		params.setResultLimit(count);
		params.setPage(page);
		
		restService.getObjects(request, response, params);
	}
	
	private ModelAndView modelAndView(Object result) {
		
	  ModelAndView mav = new ModelAndView();
	  mav.setViewName("rest/json");
	  mav.addObject("result", result);
	  
	  return mav;	 
	}
		
//	@RequestMapping(value = MAPPING + "{class}/count", method = RequestMethod.GET)
//	public @ResponseBody long getObjectCount(
//			final HttpServletRequest request,	final HttpServletResponse response,			
//			@PathVariable("class") String classname) throws IOException  {		
//
//		URI objectURI = restService.createURI(getAHRDFClass(classname));
//		
//		return restService.getObjectCount(objectURI);
//	}
//			
//	@RequestMapping(value = MAPPING + "geo", method = RequestMethod.GET)	
//	public @ResponseBody String getGeo(final HttpServletRequest request,
//			final HttpServletResponse response) throws IOException  {		
//		return "Not yet implemented";
//	}
//	
//	@RequestMapping(value = MAPPING + "test", method = RequestMethod.GET)	
//	public ModelAndView getTest(final HttpServletRequest request,
//			final HttpServletResponse response) throws IOException, MalformedQueryException, RepositoryException, QueryEvaluationException  {		
//
//		return modelAndView(restService.testTuple());
//		//return restService.testObject();
//	}

}