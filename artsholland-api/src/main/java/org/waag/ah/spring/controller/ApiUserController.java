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
import org.waag.ah.spring.model.ApiUserImpl;
import org.waag.ah.spring.model.AppImpl;
import org.waag.ah.spring.service.ApiUserService;
import org.waag.ah.spring.service.AppService;

@Controller
public class ApiUserController { // implements InitializingBean
	final static Logger logger = LoggerFactory.getLogger(ApiUserController.class);
	
	private static final String MAPPING = "/vis";
	
	@Autowired
	private ApiUserService apiUserService;

	@Autowired
	private AppService appService;
	
	@RequestMapping(value=MAPPING + "/user", method=RequestMethod.GET)
	public @ResponseBody Collection<ApiUserImpl> findAll (
			final HttpServletRequest request,
			final HttpServletResponse response)
			throws IOException {
		Collection<ApiUserImpl> users = apiUserService.findAll();
		return users;		
	}
	
	// curl -v -H "Content-Type: application/json" -X POST http://localhost:8080/vis/user -d '{"email":"vis@chips.com"}'
	@RequestMapping(value=MAPPING + "/user", method=RequestMethod.POST)
	public @ResponseBody String createUser (
			@RequestBody ApiUserImpl apiUser,
			final HttpServletRequest request,
			final HttpServletResponse response)
			throws IOException {
		apiUserService.create(apiUser);
		return "vius";
	}
		
	
	// curl -v -H "Content-Type: application/json" -X PUT http://localhost:8080/vis/user/6 -d '{"name": "Vis", "email":"vis@chips.com"}'
	@RequestMapping(value=MAPPING + "/user/{id}", method=RequestMethod.PUT) 
	public @ResponseBody String update(@PathVariable long id,
			@RequestBody ApiUserImpl apiUser,
			final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {
		apiUser.setId(id);
		apiUserService.update(apiUser);
		return "dsd";
	}
	
	@RequestMapping(value=MAPPING + "/user/{id}", method=RequestMethod.GET) 
	public @ResponseBody ApiUserImpl read(@PathVariable long id,	     
			final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {		
		ApiUserImpl apiUser = apiUserService.read(id);		
		return apiUser;		
	}
	
	// curl -XDELETE http://localhost:8080/vis/user/2
	@RequestMapping(value=MAPPING + "/user/{id}", method=RequestMethod.DELETE) 
	public @ResponseBody String delete(@PathVariable long id,	     
			final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {		
		apiUserService.delete(id);
		//ApiResult vis = new ApiResult(ApiResult.SUCCESS);
		return "Result";
	}
	
	@RequestMapping(value=MAPPING + "/user/{id}/app", method=RequestMethod.GET) 
	public @ResponseBody Collection<AppImpl> findAllApps(@PathVariable long id,	     
			final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {		
		return appService.findAllByApiUserId(id);				
	}
	
}
