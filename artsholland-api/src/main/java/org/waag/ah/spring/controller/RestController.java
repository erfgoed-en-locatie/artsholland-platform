package org.waag.ah.spring.controller;

import java.io.IOException;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.waag.ah.spring.service.RestService;

@Controller
public class RestController {

	private static final String MAPPING = "/rest/";
	
	@Resource(name = "restService")
	private RestService restService;
	
	@RequestMapping(value = MAPPING + "{class}/{cidn}", method = RequestMethod.GET)
	public ModelAndView getSingleInstance(
			final HttpServletRequest request,	final HttpServletResponse response, 
			@PathVariable("class") String classname, 
			@PathVariable("cidn") String cidn) throws IOException  {		
		
		Object result = restService.getSingleInstance(classname, cidn);
	  return modelAndView(result);
	}	
	

	@RequestMapping(value = MAPPING + "{class}", method = RequestMethod.GET)	
	public ModelAndView getInstanceList(
			final HttpServletRequest request,	final HttpServletResponse response, 
			@PathVariable("class") String classname,
			@RequestParam(value="count", defaultValue="10", required=false) int count, 
			@RequestParam(value="page", defaultValue="0", required=false) int page) {	

		Set<?> result = restService.getInstanceList(classname, count, page);
		return modelAndView(result);
		
	}
	
	@RequestMapping(value = MAPPING + "venues/{cidn}/rooms", method = RequestMethod.GET)	
	public ModelAndView getRooms(
			final HttpServletRequest request,	final HttpServletResponse response, 
			@PathVariable("cidn") String cidn) {
		
		Set<?> result = restService.getRooms(cidn);
	  return modelAndView(result);	  
	  
	}
	

	private ModelAndView modelAndView(Object result) {
	  ModelAndView mav = new ModelAndView();
	  mav.setViewName("rest/json");
	  mav.addObject("result", result);
	  
	  return mav;	 
	}
	
//@RequestMapping(value = MAPPING + "test", method = RequestMethod.GET)	
//public ModelAndView testDate(
//		final HttpServletRequest request,	final HttpServletResponse response,			
//		@RequestParam(value="before", defaultValue="0", required=false) String before,
//		@RequestParam(value="after", defaultValue="0", required=false) String after) throws DatatypeConfigurationException {	
//	
//	
//	XMLGregorianCalendar dateTimeBefore = DatatypeFactory.newInstance().newXMLGregorianCalendar(before);
//	XMLGregorianCalendar dateTimeAfter = DatatypeFactory.newInstance().newXMLGregorianCalendar(after);
//	
//	Set<?> result = restService.getEvents(dateTimeBefore, dateTimeAfter);
//	return modelAndView(result);
//	
//}
//
	
		
	@RequestMapping(value = MAPPING + "geo", method = RequestMethod.GET)	
	public  @ResponseBody String getGeo(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException  {		
		return "Not yet implemented";
	}

}