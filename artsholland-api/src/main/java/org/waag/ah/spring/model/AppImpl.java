package org.waag.ah.spring.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.waag.ah.model.App;
import org.waag.ah.model.ApiUser;
import org.waag.ah.model.DbObject;

@SuppressWarnings("serial")
@Entity
@Table(name="app")
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class AppImpl implements App, Serializable, DbObject {
	
	@Id 
	@GeneratedValue(strategy=GenerationType.IDENTITY) 
	@Column
	private long id;
	
	@Column( unique = true, nullable = false ) 
	private String apiKey;

	@Column
	private String secret;
	
	@Column
	private String name;
	
	@Column
	private String title;
	
	@Column
	private String url;
	
	@Column
	private String description;
	
//	@ManyToOne(fetch = FetchType.LAZY, targetEntity=ApiUserImpl.class)
//@JoinColumn(name = "apiuser_id", nullable = false/*, referencedColumnName="id"*/)
	@ManyToOne(optional=false)
	@JoinColumn(name="apiuser_id", nullable=false, referencedColumnName="id")
	private ApiUserImpl apiUser;
	
	@ManyToOne(optional=false)
	@JoinColumn(name="role_id", nullable=false, referencedColumnName="id")
	private RoleImpl role;
	
	@Override
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	@Override
	public String getApiKey() {
		return apiKey;
	}

	@Override
	public String getSecret() {
		return secret;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}

	public void setDescription(String description) {
		this.description = description;		
	}

	@Override
	public ApiUser getApiUser() {
		return apiUser;
	}

	public void setApiUser(ApiUserImpl apiUser) {
		this.apiUser = apiUser;
	}
	
	@Override
	public String getRole() {
		return role.getRole();
	}
	
}
