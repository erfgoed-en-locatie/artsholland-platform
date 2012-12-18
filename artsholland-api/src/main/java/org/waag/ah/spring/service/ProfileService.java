package org.waag.ah.spring.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waag.ah.PlatformConfig;
import org.waag.ah.rest.model.RestProfile;
import org.waag.artsholland.recommender.RecommenderController;

//@Singleton
@Service("profileService")
public class ProfileService /*implements InitializingBean*/  {
	final Logger logger = Logger.getLogger(ProfileService.class);

	private Map<String, RestProfile> profiles = new HashMap<String, RestProfile>();
	private Map<String, String> fbIdToUserId = new HashMap<String, String>();
	
	@Autowired PlatformConfig platformConfig;
	@Autowired RecommenderController recommender; 

//	@Override
//	public void afterPropertiesSet() throws Exception {
//	}

	public RestProfile getProfileById(String profileId) {
		return profiles.get(profileId);
	}
	
	public RestProfile getProfileByFacebookId(String fbId) {
		return profiles.get(fbIdToUserId.get(fbId));
	}
	
	public RestProfile createProfileByFacebookId(String fbId, String fbAuthKey) {
		if (!fbIdToUserId.containsKey(fbId)) {
			String uri = platformConfig.getString("platform.baseUri")+"/profile/eddepet";
			RestProfile profile = new RestProfile(uri);
			profile.setFacebookCredentials(Long.parseLong(fbId), fbAuthKey);
			profiles.put("eddepet", profile);
			fbIdToUserId.put(fbId, "eddepet");
			
			recommender.createFbUser(Long.parseLong(fbId), fbAuthKey);
		}		
		return getProfileByFacebookId(fbId);
	}
}
