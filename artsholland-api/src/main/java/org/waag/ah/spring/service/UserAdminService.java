package org.waag.ah.spring.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waag.ah.PlatformConfig;
import org.waag.ah.spring.repository.MongoUserRepository;

@Service("userAdminService")
public class UserAdminService implements InitializingBean  {
	private Logger logger = Logger.getLogger(UserAdminService.class);
	
	@Autowired
	PlatformConfig platformConfig;
		
	@Autowired
	MongoUserRepository userRepository;

	@Override
	public void afterPropertiesSet() throws Exception {
		userRepository.insertPersonWithNameJohnAndRandomAge();
	}
	
	/*getImporters
	
	getImports(importer)
	
	truncateImports(importer)
	
	getImport(importId)
	
	scheduleImporter(importer, interval)*/
	
	
	
}
