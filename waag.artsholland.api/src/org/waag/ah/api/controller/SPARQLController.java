package org.waag.ah.api.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping("/sparql")
public class SPARQLController {
	
	@RequestMapping(method = RequestMethod.GET) 
	public ModelAndView getQuery(
			@RequestParam(value="q", required=false) String query) {
      ModelAndView mav = new ModelAndView();
      mav.setViewName("sparql");
      if (query != null) {
    	  mav.addObject("queryResults", doQuery(query));
      }
      return mav;
	}
	
	@RequestMapping(method = RequestMethod.POST) 
	public ModelAndView postQuery(@RequestParam("q") String query) {
		return getQuery(query);
	}
	
	private List<String> doQuery(String query) {
		List<String> result = new ArrayList<String>();
		return result;
	}
}