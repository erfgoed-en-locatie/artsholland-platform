package org.waag.ah.spring.controller;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waag.ah.model.ApiUser;
import org.waag.ah.model.App;
import org.waag.ah.spring.model.ApiUserImpl;
import org.waag.ah.spring.model.AppImpl;
import org.waag.ah.spring.service.ApiUserService;
import org.waag.ah.spring.service.AppService;

@Controller
public class ApiUserController { // implements InitializingBean
	final static Logger logger = LoggerFactory.getLogger(ApiUserController.class);
	
	@Autowired
	private ApiUserService apiUserService;

	@Autowired
	private AppService appService;	
	
	@RequestMapping(value="/vis/user", method=RequestMethod.GET)
	public @ResponseBody Collection<ApiUserImpl> users (
			final HttpServletRequest request,
			final HttpServletResponse response)
			throws IOException {
		Collection<ApiUserImpl> users = apiUserService.findAll();
		return users;
		
	}
	
	@RequestMapping(value="/vis/app", method=RequestMethod.GET)
	public @ResponseBody Collection<AppImpl> apps (
			final HttpServletRequest request,
			final HttpServletResponse response)
			throws IOException {
		Collection<AppImpl> apps = appService.findAll();
		return apps;
		
	}
	
	
	@RequestMapping(value="/vis/user/*", method=RequestMethod.GET)
	public @ResponseBody ApiUser user(
			final HttpServletRequest request,
			final HttpServletResponse response)
			throws IOException {
		
		ApiUserImpl apiUser2 = new ApiUserImpl();
		apiUser2.setEmail("worst@vis.com");
		
		ApiUserImpl apiUser = apiUserService.findApiUserByEmail("bert.spaan@gmail.com");
		
		apiUserService.update(apiUser);
		apiUserService.create(apiUser2);
		List<AppImpl> apps = apiUser.getApps();
//		ApiUser user = new ApiUser();
//		user.setEmail("bert.spaan@gmail.com");
//		userService.addUser(user);

//		User user = new UserImpl("ivsn@boka.com");
//		user.setName("Bokkabia");
//		
//		App app = new AppImpl();
//		app.setApiKey("903284y92uro24uhyro2987yr29887ry297fyihvgwelk");
//		app.setUser(user);	
//		
//		userAdminService.saveUser(user);
//		userAdminService.saveApp(app);
//		
//		List<User> chips = userAdminService.getAllUsers();
//		ModelAndView view = new ModelAndView("admin/user");
		//view.getModel().put("users", chips);
		//view.addObject("users", chips);
		//return apiUser;
		
	
		
		return apiUser;
		
	}
}
