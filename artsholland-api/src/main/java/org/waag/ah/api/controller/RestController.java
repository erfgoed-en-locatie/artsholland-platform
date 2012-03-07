package org.waag.ah.api.controller;

import java.io.IOException;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.waag.ah.api.service.RestService;
import org.waag.ah.model.rdf.Event;
import org.waag.ah.model.rdf.Production;
import org.waag.ah.model.rdf.Room;

@Controller
public class RestController {

	private static final String MAPPING = "/rest/";
	
	@Resource(name = "restService")
	private RestService restService;
	
	@RequestMapping(value = MAPPING + "{class}/{cidn}", method = RequestMethod.GET)	
	public @ResponseBody String getInstance(
			final HttpServletRequest request,	final HttpServletResponse response, 
			@PathVariable("class") String classname, @PathVariable("cidn") String cidn,
			@RequestParam(value="count", defaultValue="10", required=false) int count, 
			@RequestParam(value="page", defaultValue="0", required=false) int page) throws IOException  {		
		return "Not yet implemented";		
	}
	
	@RequestMapping(value = MAPPING + "{class}", method = RequestMethod.GET)	
	public ModelAndView getInstance(
			final HttpServletRequest request,	final HttpServletResponse response, 
			@PathVariable("class") String classname,
			@RequestParam(value="count", defaultValue="10", required=false) int count, 
			@RequestParam(value="page", defaultValue="0", required=false) int page) {	
		
		Set<?> result = restService.getList(classname, count, page);
		
	  ModelAndView mav = new ModelAndView();
	  mav.setViewName("rest/json");
	  mav.addObject("result", result);
	  return mav;
	  
	}
	
	@RequestMapping(value = MAPPING + "geo", method = RequestMethod.GET)	
	public  @ResponseBody String getGeo(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException  {		
		return "geo";
	}
}