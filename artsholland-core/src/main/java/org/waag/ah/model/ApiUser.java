package org.waag.ah.model;

import java.util.Collection;
import java.util.Date;


public interface ApiUser {
	
	/*
		DROP TABLE IF EXISTS apiuser;

		CREATE TABLE apiuser
		(
	  	id serial NOT NULL,
	  	email text NOT NULL UNIQUE,
	  	name text,
	  	created timestamp NOT NULL default now(),	
	  	CONSTRAINT pk_apiuser_id PRIMARY KEY ( id )
		)
		WITH (
	  	OIDS=FALSE
		);
		ALTER TABLE apiuser
  	OWNER TO artsholland;
	*/
	
	public long getId();
		
	public String getEmail();
		
	public String getName();
		
	public Date getCreated();
		
	public Collection<? extends App> getApps();
	
}
