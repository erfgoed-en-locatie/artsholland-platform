package org.waag.ah.spring.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waag.ah.spring.model.ApiUser;
import org.waag.ah.spring.service.UserService;

@Controller
public class UserAdminController { // implements InitializingBean
	final static Logger logger = LoggerFactory.getLogger(UserAdminController.class);
	
	//@Resource(name="userService")
	@Autowired
	private UserService userService;

	@RequestMapping(value="/vis/user/*", method=RequestMethod.GET)
	public @ResponseBody ApiUser user(
			final HttpServletRequest request,
			final HttpServletResponse response)
			throws IOException {
		
		ApiUser user = userService.findUserByEmail("bartsimpson@example.com");
		
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
		return user;
	}
}
