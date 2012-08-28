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
import org.waag.ah.model.ApiUser;
import org.waag.ah.model.DbObject;

@SuppressWarnings("serial")
@Entity
@Table(name="apiuser")
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class ApiUserImpl implements ApiUser, Serializable, DbObject<Long> {
		
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
	private Date created = new Date();

	//@OneToMany(mappedBy = "apiUser")//, cascade=CascadeType.ALL)	  
	//private List<AppImpl> apps;
	
	public ApiUserImpl() {
		
	}
	
	public ApiUserImpl(long id) {
			setId(id);
	}
	
	@Override
	public Long getId() {
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
	public String toString() {
		return  "id: " + getId() + ", email: " + email + ", name: " + name;
	}

}
