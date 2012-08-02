package org.waag.ah.spring.controller;

import java.io.IOException;
import java.util.ArrayList;
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
import org.springframework.web.servlet.ModelAndView;
import org.waag.ah.spring.model.User2;
import org.waag.ah.spring.service.UserService;

@Controller
public class UserController { // implements InitializingBean
	final static Logger logger = LoggerFactory.getLogger(UserController.class);
	
	private static final String MAPPING = "/vis/";
	
	//@Resource(name="userService")
	@Autowired
	private UserService userService;

	@RequestMapping(value=MAPPING + "user", method=RequestMethod.GET)
	public @ResponseBody Collection<User2> user(
			final HttpServletRequest request,
			final HttpServletResponse response)
			throws IOException {
		
				
		User2 user1 = new User2();
		user1.setEmail("bert.spaan@gmail.com");
		user1.setName("Bert Spaan");
		
		User2 user2 = new User2();
		user2.setEmail("jos@chips.nl");
		user2.setName("Jos Chips");
		
		//userService.addUser(user);
		
//		App app = new AppImpl();
//		app.setApiKey("903284y92uro24uhyro2987yr29887ry297fyihvgwelk");
//		app.setUser(user);	
//		
//		userAdminService.saveUser(user);
//		userAdminService.saveApp(app);
//		
//		List<User> chips = userAdminService.getAllUsers();

		List<User2> users = new ArrayList<User2>();
		users.add(user1);
		users.add(user2);
		
		return users;
		
		//ModelAndView view = new ModelAndView("admin/user");
		//view.getModel().put("users", chips);
		//view.addObject("users", chips);
		//return user;
	}

	
}
