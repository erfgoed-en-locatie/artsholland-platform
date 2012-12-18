package org.waag.ah.spring.controller;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.waag.ah.rest.model.RestProfile;
import org.waag.ah.spring.service.ProfileService;
import org.waag.ah.spring.service.RecommenderService;

@Controller
@RequestMapping(value="/recommmend")
public class RecommenderController {
	final Logger logger = LoggerFactory.getLogger(RecommenderController.class);
	
	@Resource(name="profileService")
	private ProfileService profileService;
	
	@Autowired RecommenderService recommenderService;
	
	@Secured("ROLE_API_USER")
	@RequestMapping(method=RequestMethod.GET)
	public ModelAndView restRequest(
			final HttpServletRequest request,
			final HttpServletResponse response,
			final @RequestParam("profile_id") String profileId)
			throws IOException {
		RestProfile profile = profileService.getProfileById(profileId);
		List<String> recommendations = recommenderService.recommendForProfile(profile);
		ModelAndView view = new ModelAndView();
		view.addObject(recommendations);
		return view;
	}
}
