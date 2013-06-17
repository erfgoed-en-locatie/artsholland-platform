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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waag.ah.UUIDGenerator;
import org.waag.ah.spring.model.AppImpl;
import org.waag.ah.spring.service.AppService;
import org.waag.ah.spring.util.ApiResult;
import org.waag.ah.spring.util.ApiResult.ApiResultType;

@Controller
@RequestMapping("/admin")
public class AppController { // implements InitializingBean
	final static Logger logger = LoggerFactory.getLogger(AppController.class);
	
	@Autowired
	private AppService appService;
	
	@RequestMapping(value="/app", method=RequestMethod.GET)
	public @ResponseBody Collection<AppImpl> findAll (
			@RequestParam(value="order_by", required=false, defaultValue="id") String orderBy,
			@RequestParam(value="desc", required=false, defaultValue="false") boolean desc,
			@RequestParam(value="page", required=false, defaultValue="0") int page,
			@RequestParam(value="per_page", required=false, defaultValue="0") int perPage,
			final HttpServletRequest request,
			final HttpServletResponse response)
			throws IOException {
		Collection<AppImpl> apps = appService.findAll(orderBy, desc, page, perPage);
		return apps;		
	}	
	
	@RequestMapping(value="/app", method=RequestMethod.POST) 
	public @ResponseBody Object createApp(			
			@RequestBody AppImpl app,
			final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {		
		// TODO: do check in separate function		
		app.setApiKey(UUIDGenerator.generate());			
		return appService.create(app);		
	}
	
	@RequestMapping(value="/app/{id}", method=RequestMethod.GET) 
	public @ResponseBody Object findApp(
			@PathVariable long id,	     
			final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {		
		AppImpl app = appService.read(id);
		if (app != null)	{
			return app; 		
		}
		return new Object();
	}	
	
	@RequestMapping(value="/app/{id}", method=RequestMethod.PUT) 
	public @ResponseBody Object updateApp(
			@PathVariable long id,
			@RequestBody AppImpl app,
			final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {		
		if (app.getId() == 0 || app.getId() == id) {
			app.setId(id);			
			return appService.update(app);			
		}
		return new ApiResult(ApiResultType.FAILED);
	}	
	
	@RequestMapping(value="/app/{id}", method=RequestMethod.DELETE) 
	public @ResponseBody ApiResult deleteApp(
			@PathVariable long id, 
			final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {		
			return appService.delete(id);		
	}	
	
	
}
