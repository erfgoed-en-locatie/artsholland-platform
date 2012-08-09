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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waag.ah.spring.model.AppImpl;
import org.waag.ah.spring.service.AppService;

@Controller
public class AppController { // implements InitializingBean
	final static Logger logger = LoggerFactory.getLogger(AppController.class);
	
	private static final String MAPPING = "/admin";
	
	@Autowired
	private AppService appService;
	
	@RequestMapping(value=MAPPING + "/app", method=RequestMethod.GET)
	public @ResponseBody Collection<AppImpl> findAll (
			final HttpServletRequest request,
			final HttpServletResponse response)
			throws IOException {
		Collection<AppImpl> apps = appService.findAll();
		return apps;		
	}
	
	@RequestMapping(value=MAPPING + "/app/{id}", method=RequestMethod.GET)
	public @ResponseBody AppImpl findById(
			@PathVariable long id, 
			final HttpServletRequest request,
			final HttpServletResponse response)
			throws IOException {
		return appService.read(id);		
	}
	
}
