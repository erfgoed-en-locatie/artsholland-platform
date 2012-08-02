package org.waag.ah.spring.model;

import java.util.Collection;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.waag.ah.model.App;
import org.waag.ah.model.User;

@Entity
@Table(name="user")
public class User2 implements User {
	// TODO implements Serializable
	
	@Id
	@Column
	private String email;
	
	@Column
	private Date created;
	
	@Column
	private String name;
	
	@OneToMany(mappedBy="user", cascade = CascadeType.ALL)
  private Collection<App> apps;

	
	@Override
	public String getEmail() {
		return email;
	}

	@Override
	public void setEmail(String email) {
		this.email = email;		
	}

	@Override
	public Date getCreated() {
		return created;
	}

	@Override
	public void setCreated(Date created) {
		this.created = created;		
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Collection<App> getApps() {
		return apps;
	}

}
