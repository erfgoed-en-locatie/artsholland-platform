package org.waag.ah.spring.controller;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.waag.ah.model.User;
import org.waag.ah.rest.RestParameters;
import org.waag.ah.spring.annotation.RestRequestParameters;
import org.waag.ah.spring.service.UserAdminService;

@Controller
public class UserAdminController { // implements InitializingBean
	final static Logger logger = LoggerFactory.getLogger(UserAdminController.class);
	
	@Resource(name="userAdminService")
	private UserAdminService userAdminService;

	@RequestMapping(value="/vis/user/*", method=RequestMethod.GET)
	public ModelAndView user(
			final HttpServletRequest request,
			final HttpServletResponse response,
			@RestRequestParameters(prefixLength=1) RestParameters params)
			throws IOException {
		List<User> chips = userAdminService.getAllObjects();
		ModelAndView view = new ModelAndView("admin/user");
		view.addObject("users", chips);
		return view;
	}

	
}
