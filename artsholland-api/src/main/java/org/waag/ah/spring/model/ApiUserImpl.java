package org.waag.ah.spring.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.waag.ah.model.ApiUser;
import org.waag.ah.model.DbObject;

@SuppressWarnings("serial")
@Entity
@Table(name="apiuser")
public class ApiUserImpl implements ApiUser, Serializable, DbObject {
		
	@Id 
	@GeneratedValue( strategy = GenerationType.IDENTITY ) 
	@Column 
	private long id;
	
	@Column( unique = true, nullable = false ) 
	private String email;

	@Column
	private String name;
	
	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@OneToMany(mappedBy = "apiUser")//, cascade=CascadeType.ALL)	  
	private List<AppImpl> apps;
	
	@Override
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	@Override
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	@JsonIgnore
	public List<AppImpl> getApps() {
		return apps;
	}

	@Override
	public String toString() {
		return  "id: " + getId() + ", email: " + email + ", name: " + name;
	}	
}
