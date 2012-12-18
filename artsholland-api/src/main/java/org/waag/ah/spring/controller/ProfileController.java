package org.waag.ah.spring.controller;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waag.ah.rest.model.RestProfile;
import org.waag.ah.spring.service.ProfileService;

@Controller
@RequestMapping(value="/profile")
public class ProfileController {
	final Logger logger = LoggerFactory.getLogger(ProfileController.class);
	
	@Resource(name="profileService")
	private ProfileService profileService;
	
	@Secured("ROLE_API_USER")
	@RequestMapping(method=RequestMethod.POST)
	@ResponseBody
	public String restRequest(
			final HttpServletRequest request,
			final HttpServletResponse response,
			final @RequestParam("facebook_id") String fbId,
			final @RequestParam("facebook_authkey") String fbAuthKey) {
		RestProfile profile = profileService.createProfileByFacebookId(fbId, fbAuthKey);
		return profile.getUrl().toExternalForm();
	}
	
	@Secured("ROLE_API_USER")
	@RequestMapping(value="/{profileId}", method=RequestMethod.GET)
	@ResponseBody
	public String restRequest(
			final HttpServletRequest request,
			final HttpServletResponse response,
			final @PathVariable("profileId") String profileId) throws IOException {
		try {
			RestProfile profile = profileService.getProfileById(profileId);
			return profile.getUrl().toExternalForm();
		} catch (NullPointerException e) {
			response.sendError(404, "Unknown user");
		}
		return null;
	}
}
