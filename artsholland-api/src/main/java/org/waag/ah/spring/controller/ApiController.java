package org.waag.ah.spring.controller;
//package org.waag.ah.spring.controller;
//
//import java.io.IOException;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.waag.ah.api.service.ApiService;
//
//@Controller
//public class ApiController {
//
//	private static final String MAPPING = "/api/";
//	
//	@Resource(name = "apiService")
//	private ApiService apiService;
//	
//	@RequestMapping(value = MAPPING + "event/{id}", method = RequestMethod.GET)	
//	public  @ResponseBody String getEvent(final HttpServletRequest request,
//			final HttpServletResponse response, @PathVariable("id") String id) throws IOException  {		
//		return apiService.getEvent(id);		
//	}
//}