package org.waag.ah.model;

public interface Role { 
	
	/*
		DROP TABLE IF EXISTS role;
	
		CREATE TABLE role
		(
		  id serial NOT NULL,
		  role text NOT NULL UNIQUE,
		  CONSTRAINT pk_role_id PRIMARY KEY (id )
		)
		WITH (
		  OIDS=FALSE
		);
		ALTER TABLE role
		  OWNER TO postgres;
	*/
	
	public long getId();
	
	public String getRole();

}