	package org.waag.ah.spring.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openrdf.rio.RDFFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
	
	@RequestMapping(value = MAPPING + "{class}/{cidn}", method = RequestMethod.GET)
	public void getObject(
			final HttpServletRequest request,
			final HttpServletResponse response,
			@RequestParam(value = "lang", defaultValue = "nl", required = false) String lang,
			@PathVariable("class") String classname,
			@PathVariable("cidn") String cidn) throws IOException {
	
		RESTParametersImpl params = new RESTParametersImpl();
		
		params.setObjectURI(classname + "/" + cidn);
		params.setObjectClass(getAHRDFClass(classname));
		params.setLanguage(lang);
		
		restService.getObject(request, response, params);
	}

	@RequestMapping(value = MAPPING + "{class}/{cidn}/{linkedClass}", method = RequestMethod.GET)
	public void getLinkedObjects(
		final HttpServletRequest request,
		final HttpServletResponse response,
		@RequestParam(value = "lang", defaultValue = "nl", required = false) String lang,
		@RequestParam(value = "limit", defaultValue = "10", required = false) long limit,
		@RequestParam(value = "page", defaultValue = "1", required = false) long page,
		@PathVariable("class") String classname,
		@PathVariable("cidn") String cidn,
		@PathVariable("linkedClass") String linkedClassname)
		throws IOException {
		RESTParametersImpl params = new RESTParametersImpl();		
		
		params.setObjectURI(classname + "/" + cidn);
		params.setObjectClass(getAHRDFClass(classname));
		params.setLinkedClass(getAHRDFClass(linkedClassname));
		params.setResultLimit(limit);
		params.setPage(page);
		params.setLanguage(lang);
		
		restService.getLinkedObjects(request, response, params);	
	}
	
	@RequestMapping(value = MAPPING + "{class}", method = RequestMethod.GET)	
	public void getObjects(final HttpServletRequest request, final HttpServletResponse response, 
			@PathVariable("class") String classname,
			@RequestParam(value="lang", defaultValue="nl", required=false) String lang,
			@RequestParam(value="limit", defaultValue="10", required=false) long limit, 
			@RequestParam(value="page", defaultValue="1", required=false) long page) throws IOException {	
		RESTParametersImpl params = new RESTParametersImpl();
		
		params.setObjectClass(getAHRDFClass(classname));
		params.setResultLimit(limit);
		params.setPage(page);
		params.setLanguage(lang);
		
		restService.getObjects(request, response, params);
	}
	
	
//	private ModelAndView modelAndView(Object result) {		
//	  ModelAndView mav = new ModelAndView();
//	  mav.setViewName("rest/json");
//	  mav.addObject("result", result);
//	  
//	  return mav;	 
//	}
		
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
	
	@RequestMapping(value = MAPPING + "geo", method = RequestMethod.GET)	
	public @ResponseBody String getGeo(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException  {		
		return "Not yet implemented";
	}


}