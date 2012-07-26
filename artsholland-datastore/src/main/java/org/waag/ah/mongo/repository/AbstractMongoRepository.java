package org.waag.ah.mongo.repository;

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
public abstract class AbstractMongoRepository {
 
		@Autowired
		protected MongoConnectionService mongoConnectionService;
		
		@Autowired
		protected PropertiesConfiguration platformConfig;
    
		protected MongoTemplate mongoTemplate;
		
    public AbstractMongoRepository() {
    	Mongo mongo = mongoConnectionService.getMongo();
			mongoTemplate = new MongoTemplate(mongo, platformConfig.getString("mongo.database"));
		}
}