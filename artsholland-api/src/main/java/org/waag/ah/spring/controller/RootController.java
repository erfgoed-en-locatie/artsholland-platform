package org.waag.ah.spring.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class RootController {
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public @ResponseBody String root() {
		return "OK";
	}
	
	@Secured("ROLE_API_USER")
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public @ResponseBody String test() {
		return "PING!";
	}
}
