package org.waag.ah.model;

import java.util.Date;

public interface App { 
	
	/*
		DROP TABLE IF EXISTS app;
	
		CREATE TABLE app
		(
	  	id serial NOT NULL,	  	
	  	apikey text,	
			apiuser_id int,	
			secret text,
			name text,	
			title text,	
			url text,	
			description text,
      created timestamp NOT NULL default now(),	
      role_id integer NOT NULL,
  		CONSTRAINT pk_app_id PRIMARY KEY (id ),
  		CONSTRAINT fk_app_role_id FOREIGN KEY (role_id)
      	REFERENCES role (id) MATCH SIMPLE
      	ON UPDATE NO ACTION ON DELETE NO ACTION,
  		CONSTRAINT fk_app_user_id FOREIGN KEY (apiuser_id)
      	REFERENCES apiuser (id) MATCH SIMPLE
      	ON UPDATE NO ACTION ON DELETE NO ACTION
		)
		WITH (
	  	OIDS=FALSE
		);
		ALTER TABLE app
		OWNER TO artsholland;
	*/
	
	public Long getId();
	
	public String getApiKey();
	
	public Long getApiUserId();
	
	public String getSecret();
	
	public String getName();
	
	public String getTitle();
	
	public String getUrl();
	
	public String getDescription();

	public Long getRoleId();
	
	public Date getCreated();

}
