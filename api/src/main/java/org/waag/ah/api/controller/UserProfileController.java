//package org.waag.ah.api.controller;
//
//import javax.annotation.Resource;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.waag.ah.api.domain.Profile;
//import org.waag.ah.api.service.ProfileService;
//
//
//@Controller
//public class UserProfileController {
//	  
//	@Resource(name="profileService")
//	private ProfileService profileService;
//	
//	@RequestMapping(
//			value = "/rest/profile",
//			method = RequestMethod.POST) 
////			headers="Accept=application/xml, application/json")
//	public @ResponseBody Profile addProfile(@RequestBody Profile profile) {
//		return profileService.add(profile);
//	}
//	 
//	@RequestMapping(
//			value = "/rest/profile/{id}", 
//			method = RequestMethod.GET)
//	public @ResponseBody Profile getPerson(@PathVariable("id") Long id) {
//		return profileService.get(id);
//	}
//}
