package org.waag.ah.spring.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.waag.ah.model.DbObject;
import org.waag.ah.model.Role;

@SuppressWarnings("serial")
@Entity
@Table(name="role")
public class RoleImpl implements Role, Serializable, DbObject {
	
	@Id 
	@GeneratedValue(strategy=GenerationType.IDENTITY) 
	@Column
	private long id;
	
	@Column( unique = true, nullable = false ) 
	private String role;
	
	@Override
	public long getId() {
		return id;
	}
	
	@Override
	public String getRole() {
		return role;
	}
	
}
