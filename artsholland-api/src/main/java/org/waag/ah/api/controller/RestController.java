package org.waag.ah.api.controller;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.result.MultipleResultException;
import org.openrdf.result.NoResultException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waag.ah.api.service.RestService;
import org.waag.ah.model.RoomImpl;

@Controller
public class RestController {

	private static final String MAPPING = "/rest/";
	
	@Resource(name = "restService")
	private RestService restService;
	
	@RequestMapping(value = MAPPING + "{class}/{cidn}", method = RequestMethod.GET)	
	public  @ResponseBody String getInstance(
			final HttpServletRequest request,	final HttpServletResponse response, 
			@PathVariable("class") String classname, @PathVariable("cidn") String cidn,
			@RequestParam(value="count", defaultValue="10", required=false) int count, 
			@RequestParam(value="page", defaultValue="0", required=false) int page) throws IOException  {		
		return restService.getClass(classname, cidn, count, page);		
	}
	
	@RequestMapping(value = MAPPING + "rooms", method = RequestMethod.GET)	
	public Set<RoomImpl> getRooms(
			final HttpServletRequest request,	final HttpServletResponse response,
			@RequestParam(value="count", defaultValue="10", required=false) int count, 
			@RequestParam(value="page", defaultValue="0", required=false) int page) throws IOException  {		
		try {
			return restService.getRooms(count, page);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;	
	}
	
	@RequestMapping(value = MAPPING + "geo", method = RequestMethod.GET)	
	public  @ResponseBody String getGeo(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException  {		
		return "geo";
	}
}