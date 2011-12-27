package org.waag.artsholland.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")
public class RestController {
	@RequestMapping(
			value = "/", 
	        method = RequestMethod.GET)
	public @ResponseBody String getObjects() {
		return "OK";
	}
}
