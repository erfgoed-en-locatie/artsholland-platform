package org.waag.ah.spring.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.waag.ah.model.User;

@SuppressWarnings("serial")
@Entity //@Table(name="T_ACCOUNT")
public class ApiUser implements User, Serializable {
	
	@Id 
	@GeneratedValue( strategy = GenerationType.AUTO ) 
	@Column //( name = "USER_ID" )
	private long id;
	
	@Column( unique = true, nullable = false ) 
	private String email;

//	@Column
//	private Date created;

	@Column
	private String name;

//	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
//	private Collection<App> apps;
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
//	@Override
	public String getEmail() {
		return email;
	}

//	@Override
	public void setEmail(String email) {
		this.email = email;
	}

//	@Override
//	public Date getCreated() {
//		return created;
//	}

//	@Override
//	public void setCreated(Date created) {
//		this.created = created;
//	}

//	@Override
	public String getName() {
		return name;
	}

//	@Override
	public void setName(String name) {
		this.name = name;
	}

//	@Override
//	public Collection<App> getApps() {
//		return apps;
//	}

	@Override
	public String toString() {
		return  "id: " + id + ", email: " + email + ", name: " + name;
	}	
}
