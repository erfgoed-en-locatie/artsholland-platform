package org.waag.ah.spring.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waag.ah.PlatformConfig;
import org.waag.ah.model.User;
import org.waag.ah.spring.repository.UserRepository;

@Service("userAdminService")
public class UserAdminService implements InitializingBean  {
	private Logger logger = Logger.getLogger(UserAdminService.class);
	
	@Autowired
	PlatformConfig platformConfig;
		
	@Autowired
	UserRepository userRepository;

	@Override
	public void afterPropertiesSet() throws Exception {
	}

	public List<User> getAllObjects() {
		return userRepository.getAllObjects();
	}
	
	/*getImporters
	
	getImports(importer)
	
	truncateImports(importer)
	
	getImport(importId)
	
	scheduleImporter(importer, interval)*/
	
	
	
}
