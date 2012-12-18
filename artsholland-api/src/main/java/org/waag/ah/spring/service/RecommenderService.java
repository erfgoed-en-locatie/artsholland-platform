package org.waag.ah.spring.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waag.ah.PlatformConfig;
import org.waag.ah.rest.model.RestProfile;
import org.waag.artsholland.recommender.RecommenderController;

@Service("recommenderService")
public class RecommenderService { //implements InitializingBean {
	final Logger logger = Logger.getLogger(RecommenderService.class);
	
	@Autowired RecommenderController recommender;
	@Autowired PlatformConfig platformConfig;

//	@Override
//	public void afterPropertiesSet() throws Exception {
//		recommender = new RecommenderController();
//	}

	public List<String> recommendForProfile(RestProfile profile) {
		List<String> result = recommender.facebookRecommend(0l, 10);
		return result;
	}
}
