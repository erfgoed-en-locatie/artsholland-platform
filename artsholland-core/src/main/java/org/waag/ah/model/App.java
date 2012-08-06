package org.waag.ah.model;

public interface App { 
	
	/*
		DROP TABLE IF EXISTS app;
	
		CREATE TABLE app
		(
	  	id serial NOT NULL,	  	
	  	apikey text,	
			apiuser_id int,	
			secret text,
			Name text,	
			title text,	
			url text,	
			description text,
	  	CONSTRAINT pk_app_id PRIMARY KEY ( id )
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
	
	public long getId();
	
	public String getApiKey();
	
	public ApiUser getApiUser();
	
	public String getSecret();
	
	public String getName();
	
	public String getTitle();
	
	public String getUrl();
	
	public String getDescription();

}
