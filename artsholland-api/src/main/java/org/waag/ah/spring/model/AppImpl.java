package org.waag.ah.spring.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.waag.ah.UUIDGenerator;
import org.waag.ah.model.App;
import org.waag.ah.model.DbObject;

@SuppressWarnings("serial")
@Entity
@Table(name="app")
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class AppImpl implements App, Serializable, DbObject<Long> {
	
	@Id 
	@GeneratedValue(strategy=GenerationType.IDENTITY) 
	@Column
	private long id;
	
	@Column( unique = true, nullable = false ) 
	private String apiKey = UUIDGenerator.generate();

	@Column
	private String secret = UUIDGenerator.generate();
	
	@Column
	private String name;
	
	@Column
	private String title;
	
	@Column
	private String url;
	
	@Column
	private String description;
	
	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Date created = new Date();
	
//	@ManyToOne(cascade=CascadeType.PERSIST)
//	@JoinColumn(name="apiuser_id", nullable=false, referencedColumnName="id")
//	private ApiUserImpl apiUser;
	@Column(name="apiuser_id", nullable=false)
	private long apiUserId;
	
//	@ManyToOne(cascade=CascadeType.PERSIST)
//	@JoinColumn(name="role_id", nullable=false, referencedColumnName="id")
//	@JsonManagedReference
	@Column(name="role_id", nullable=false)
	private long roleId = 1;
	
	@Override
	public Long getId() {
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
	public Long getApiUserId() {
		return apiUserId;
	}

	public void setApiUserId(long apiUserId) {
		this.apiUserId = apiUserId;
	}
	
	@Override
	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	@Override
	public Date getCreated() {
		return created;
	}
	
	public void setCreated(Date created) {
		this.created = created;
	}

	
}
