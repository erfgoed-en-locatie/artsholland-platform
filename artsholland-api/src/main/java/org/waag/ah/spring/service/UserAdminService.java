package org.waag.ah.spring.service;

import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waag.ah.PlatformConfig;
import org.waag.ah.model.App;
import org.waag.ah.model.User;
import org.waag.ah.spring.repository.AppRepository;
import org.waag.ah.spring.repository.UserRepository;

@Service("userAdminService")
public class UserAdminService implements InitializingBean  {
	
	@Autowired
	PlatformConfig platformConfig;
		
	@Autowired
	UserRepository userRepository;

	@Autowired
	AppRepository appRepository;
	
	@Override
	public void afterPropertiesSet() throws Exception {
	}

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}
	
	public void saveUser(User user) {
		userRepository.saveObject(user);
	}

	public void saveApp(App app) {
		appRepository.saveObject(app);		
	}
	
	/*getImporters
	
	getImports(importer)
	
	truncateImports(importer)
	
	getImport(importId)
	
	scheduleImporter(importer, interval)*/
	
	
	
}
