package org.waag.ah.spring.controller;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waag.ah.model.PrivateObject;
import org.waag.ah.spring.model.PrivateObjectImpl;
import org.waag.ah.spring.service.PrivateObjectService;
import org.waag.ah.spring.util.ApiResult;

@Controller
@RequestMapping("/private")
public class PrivateObjectController {
	final static Logger logger = LoggerFactory.getLogger(PrivateObjectController.class);
	
	@Autowired
	private PrivateObjectService privateObjectService;

	
	@RequestMapping(value="/object", method=RequestMethod.GET)
	public @ResponseBody Collection<PrivateObjectImpl> findAll (
			final HttpServletRequest request,
			final HttpServletResponse response)
			throws IOException {		
		return privateObjectService.findAll();
	}
	
	// curl -v -H "Content-Type: application/json" -X POST http://localhost:8080/private/object -d '{ "uri": "http://data.artsholland.com/venue/91366345-e70d-4a19-9ed2-b242436de106", "data": {"geheim": "Dit is moeilijk geheim!"}}'
	@RequestMapping(value="/object", method=RequestMethod.POST)
	public @ResponseBody ApiResult createPrivateObject (
			@RequestBody PrivateObjectImpl privateObject,
			final HttpServletRequest request,
			final HttpServletResponse response)
			throws IOException {	
		return privateObjectService.create(privateObject);		
	}
		
	@RequestMapping(value="/object/{id}", method=RequestMethod.GET)
	public @ResponseBody PrivateObject findById(
			@PathVariable String id, 
			final HttpServletRequest request,
			final HttpServletResponse response)
			throws IOException {
		return privateObjectService.find(id);
	}
	
	
}
