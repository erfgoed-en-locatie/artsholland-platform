package org.waag.ah.spring.repository;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;
import org.waag.ah.service.MongoConnectionService;

import com.mongodb.Mongo;
 
/**
 * Repository for {@link Person}s
 *
 */
@Repository
@Singleton
public abstract class AbstractMongoRepository {
 
		@Autowired
		protected MongoConnectionService mongoConnectionService;
		
		@Autowired
		protected PropertiesConfiguration platformConfig;
    
		protected MongoTemplate mongoTemplate;
		
		@PostConstruct   
		public void connect() {
    	Mongo mongo = mongoConnectionService.getMongo();
			mongoTemplate = new MongoTemplate(mongo, platformConfig.getString("mongo.database"));
		}
}