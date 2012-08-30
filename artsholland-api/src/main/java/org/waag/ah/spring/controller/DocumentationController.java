package org.waag.ah.spring.controller;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waag.ah.spring.service.DocumentationService;

@Controller
@RequestMapping("/doc")
public class DocumentationController {
	final static Logger logger = LoggerFactory.getLogger(DocumentationController.class);
	
	@Autowired
	private DocumentationService documentationService;
	
	@RequestMapping(value="/{name}", method=RequestMethod.GET)
	public @ResponseBody Object renderByName(
			@PathVariable String name, 
			final HttpServletRequest request,
			final HttpServletResponse response)
			throws IOException {
		return documentationService.render(name);
	}	
	
}
