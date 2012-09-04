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
import org.waag.ah.spring.model.ApiUserImpl;
import org.waag.ah.spring.model.AppImpl;
import org.waag.ah.spring.service.ApiUserService;
import org.waag.ah.spring.service.AppService;
import org.waag.ah.spring.util.ApiResult;
import org.waag.ah.spring.util.ApiResult.ApiResultType;

@Controller
@RequestMapping("/admin")
public class ApiUserController { // implements InitializingBean
	final static Logger logger = LoggerFactory.getLogger(ApiUserController.class);
		
	@Autowired
	private ApiUserService apiUserService;

	@Autowired
	private AppService appService;

	// TODO: use generic request parameter resolver, just like REST API does.
	@RequestMapping(value="/user", method=RequestMethod.GET)
	public @ResponseBody Collection<ApiUserImpl> findAll(
			@RequestParam(value="order_by", required=false, defaultValue="id") String orderBy,
			@RequestParam(value="desc", required=false, defaultValue="false") boolean desc,
			@RequestParam(value="page", required=false, defaultValue="0") int page,
			@RequestParam(value="per_page", required=false, defaultValue="0") int perPage,
			final HttpServletRequest request,
			final HttpServletResponse response)
			throws IOException {
		Collection<ApiUserImpl> users = apiUserService.findAll(orderBy, desc, page, perPage);
		return users;		
	}
	
	// curl -v -H "Content-Type: application/json" -X POST http://localhost:8080/admin/user -d '{"email":"admin@chips.com"}'
	@RequestMapping(value="/user", method=RequestMethod.POST)
	public @ResponseBody Object createUser (
			@RequestBody ApiUserImpl apiUser,
			final HttpServletRequest request,
			final HttpServletResponse response)
			throws IOException {		
		return apiUserService.create(apiUser);		
	}
		
	
	// curl -v -H "Content-Type: application/json" -X PUT http://localhost:8080/admin/user/6 -d '{"name": "admin", "email":"admin@chips.com"}'
	@RequestMapping(value="/user/{id}", method=RequestMethod.PUT) 
	public @ResponseBody Object update(
			@PathVariable long id,
			@RequestBody ApiUserImpl apiUser,
			final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {		
		if (apiUser.getId() == 0 || apiUser.getId() == id) {
			apiUser.setId(id);
			return apiUserService.update(apiUser);
		}		
		return new ApiResult(ApiResultType.FAILED);
	}
	
	@RequestMapping(value="/user/{id}", method=RequestMethod.GET) 
	public @ResponseBody ApiUserImpl findById(
			@PathVariable long id,	     
			final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {		
		ApiUserImpl apiUser = apiUserService.read(id);
		return apiUser;		
	}
	
	// curl -XDELETE http://localhost:8080/admin/user/2
	@RequestMapping(value="/user/{id}", method=RequestMethod.DELETE) 
	public @ResponseBody ApiResult delete(
			@PathVariable long id,	     
			final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {		
		appService.deleteAllByApiUserId(id);
		return apiUserService.delete(id);
	}
	
	/*
	 * ======================================== Apps =======================================
	 */
	
	@RequestMapping(value="/user/{id}/app", method=RequestMethod.GET) 
	public @ResponseBody Collection<AppImpl> findAllApps(
			@PathVariable long id,	     
			final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {		
		return appService.findAllByApiUserId(id);				
	}	

	
}
