//package org.waag.spring.service;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.springframework.stereotype.Service;
//import org.waag.ah.api.ApplicationType;
//import org.waag.ah.api.UserAccountType;
//import org.waag.ah.model.Application;
//import org.waag.ah.model.User;
//
//@Service("adminService")
//public class AdminService {
//
//	public void createUser(User user) {
//		
//	}
//	
//	public List<Application> getApplicationList() {
//		List<Application> appList = new ArrayList<Application>();
//		ApplicationType appType1 = new ApplicationType();
//		appType1.setName("App 1");
//		appType1.setURL("http://artsholland.com");
//		UserAccountType user = new UserAccountType();
//		user.setFirstName("Piet");
//		user.setLastName("Peters");
//		user.setEmailAddress("pietpeters@gmail.com");
//		appType1.setUserAccount(user);
//		ApplicationType appType2 = new ApplicationType();
//		appType2.setName("App 2");
//		appList.add(appType1);
//		appList.add(appType2);
//		return appList;
//	}
//}
