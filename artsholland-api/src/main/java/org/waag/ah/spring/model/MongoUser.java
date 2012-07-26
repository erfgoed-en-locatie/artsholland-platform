package org.waag.ah.spring.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.waag.ah.model.Application;
import org.waag.ah.model.User;

@Document
public class MongoUser implements User {

	@Id
	private String email;
	private Date created;
	private String name;
	private List<Application> applications;

	public MongoUser(String email) {
		this.email = email;
	}
	
	@Override
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Date getCreated() {
		return created;
	}
	
	public void setCreated(Date created) {
		this.created = created;
	}

	@Override
	public List<Application> getApplications() {
		return applications;
	}

	public void setApplications(List<Application> applications) {
		this.applications = applications;
	}

	
}
