package org.waag.artsholland.api.controller;

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
	public ModelAndView query(
			@RequestParam(value="q", required=false) String query) {
      ModelAndView mav = new ModelAndView();
      mav.setViewName("sparql");
      if (query != null) {
    	  mav.addObject("queryResults", doQuery(query));
      }
      return mav;
	}
	
	private List<String> doQuery(String query) {
		List<String> result = new ArrayList<String>();
		return result;
	}
}
