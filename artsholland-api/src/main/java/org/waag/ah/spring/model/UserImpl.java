//package org.waag.ah.spring.model;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//import org.springframework.data.annotation.Id;
//import org.springframework.data.mongodb.core.mapping.Document;
//import org.waag.ah.model.App;
//import org.waag.ah.model.User;
//
//@Document(collection="user")
//public class UserImpl implements User {
//
//	@Id
//	private String email;
//	private Date created;
//	private String name;
//
//	public UserImpl(String email) {		
//		this.email = email;
//	}
//	
//	@Override
//	public String getEmail() {
//		return email;
//	}
//
//	public void setEmail(String email) {
//		this.email = email;
//	}
//	
//	@Override
//	public String getName() {
//		return name;
//	}
//
//	public void setName(String name) {
//		this.name = name;
//	}
//
//	@Override
//	public Date getCreated() {
//		return created;
//	}
//	
//	public void setCreated(Date created) {
//		this.created = created;
//	}
//
//	@Override
//	public List<App> getApps() {
//		return null;
//		//search apps where id = this.id
//	}
//
//	
//}
