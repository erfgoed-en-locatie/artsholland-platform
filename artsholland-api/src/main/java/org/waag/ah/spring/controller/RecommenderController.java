package org.waag.ah.spring.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value="/recommmend")
public class RecommenderController {
	final static Logger logger = LoggerFactory.getLogger(RecommenderController.class);

	@Secured("ROLE_API_USER")
	@RequestMapping(method=RequestMethod.POST)
	public ModelAndView restRequest(
			final HttpServletRequest request,
			final HttpServletResponse response)//,
//			@RestRequestParameters(prefixLength=1, paging=true) RestParameters params)
			throws IOException {
		return null;
	}
}
