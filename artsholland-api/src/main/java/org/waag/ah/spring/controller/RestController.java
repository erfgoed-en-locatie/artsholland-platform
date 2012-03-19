package org.waag.ah.spring.controller;

import java.io.IOException;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.datatype.XMLGregorianCalendar;

import org.openrdf.model.URI;
import org.openrdf.model.datatypes.XMLDatatypeUtil;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.waag.ah.model.rdf.AHRDFObject;
import org.waag.ah.model.rdf.Event;
import org.waag.ah.model.rdf.Room;
import org.waag.ah.spring.service.RestService;

@Controller
public class RestController {

	private static final String MAPPING = "/rest/";
	
	@Resource(name = "restService")
	private RestService restService;
	
	@RequestMapping(value = MAPPING + "datumtest", method = RequestMethod.GET)	
	public ModelAndView testDate(
			final HttpServletRequest request,	final HttpServletResponse response,			
			@RequestParam(value="from", defaultValue="1970-01-01T00:00:00Z", required=false) String dtFrom,
			@RequestParam(value="to", defaultValue="2050-01-01T17:00:00Z", required=false) String dtTo) throws MalformedQueryException, RepositoryException, QueryEvaluationException {	
		
		Set<?> result = restService.getEvents(XMLDatatypeUtil.parseCalendar(dtFrom), XMLDatatypeUtil.parseCalendar(dtTo));
		return modelAndView(result);
		
	}
	
	@RequestMapping(value = MAPPING + "{class}/{cidn}", method = RequestMethod.GET)
	public ModelAndView getSingleInstance(
			final HttpServletRequest request,	final HttpServletResponse response,
			@RequestParam(value="lang", defaultValue="nl", required=false) String lang,
			@PathVariable("class") String classname, 
			@PathVariable("cidn") String cidn) throws IOException  {		
		
		AHRDFObject result = restService.getSingleInstance(classname, cidn, lang);	
	  return modelAndView(result);
	  
	}
	
	@RequestMapping(value = MAPPING + "{class}/{cidn}/{associatedClass}", method = RequestMethod.GET)
	public ModelAndView getAssociatedInstanceList(
			final HttpServletRequest request,	final HttpServletResponse response,
			@RequestParam(value="lang", defaultValue="nl", required=false) String lang,
			@PathVariable("class") String classname, 
			@PathVariable("cidn") String cidn,
			@PathVariable("associatedClass") String associatedClassname) throws IOException  {		
		
		Set<AHRDFObject> result = restService.getAssociatedInstanceList(classname, cidn, associatedClassname, lang);	
	  return modelAndView(result);
	  
	}
	

	@RequestMapping(value = MAPPING + "{class}", method = RequestMethod.GET)	
	public ModelAndView getInstanceList(
			final HttpServletRequest request,	final HttpServletResponse response, 
			@PathVariable("class") String classname,
			@RequestParam(value="lang", defaultValue="nl", required=false) String lang,
			@RequestParam(value="count", defaultValue="10", required=false) int count, 
			@RequestParam(value="page", defaultValue="0", required=false) int page) {	

		Set<AHRDFObject> result = restService.getInstanceList(classname, count, page, lang);		
		return modelAndView(result);
		
	}
	
	@RequestMapping(value = MAPPING + "venues/{cidn}/rooms", method = RequestMethod.GET)	
	public ModelAndView getRooms(
			final HttpServletRequest request,	final HttpServletResponse response, 
			@PathVariable("cidn") String cidn) {
		
		Set<Room> result = restService.getRooms(cidn);
	  return modelAndView(result);	  
	  
	}
	

	private ModelAndView modelAndView(Object result) {
	  ModelAndView mav = new ModelAndView();
	  mav.setViewName("rest/json");
	  mav.addObject("result", result);
	  
	  return mav;	 
	}
	
	
	@RequestMapping(value = MAPPING + "{class}/count", method = RequestMethod.GET)
	public @ResponseBody long getInstanceCount(
			final HttpServletRequest request,	final HttpServletResponse response,			
			@PathVariable("class") String classname) throws IOException  {		
		return restService.getInstanceCount(classname);
	}
	
		
	@RequestMapping(value = MAPPING + "geo", method = RequestMethod.GET)	
	public @ResponseBody String getGeo(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException  {		
		return "Not yet implemented";
	}

}