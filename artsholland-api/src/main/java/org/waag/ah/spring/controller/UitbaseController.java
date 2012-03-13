package org.waag.ah.spring.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.waag.ah.spring.service.UitbaseService;

@Controller
public class UitbaseController {
//	private Logger logger = Logger.getLogger(UitbaseController.class);

	private static final String MAPPING = "/uitbase/";
	
//	@Resource(name = "uitbaseService")
	private UitbaseService uitbaseService = new UitbaseService();
	
	@RequestMapping(value = MAPPING + "**", method = RequestMethod.GET)	
	public void proxy(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException  {
		
		String path = request.getServletPath().substring(MAPPING.length());
		uitbaseService.proxyQuery(request, response, path);
	}
}