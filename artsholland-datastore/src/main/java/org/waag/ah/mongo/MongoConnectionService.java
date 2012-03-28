package org.waag.ah.mongo;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.PlatformConfig;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;

@Singleton
public class MongoConnectionService {
	private static final Logger logger = LoggerFactory
			.getLogger(MongoConnectionService.class);

	private Mongo mongo;
	private PropertiesConfiguration config;

	@PostConstruct
	public void connect() {
		try {
			config = PlatformConfig.getConfig(); 
			mongo = new Mongo();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	public DB getConnection() {
		return mongo.getDB(config.getString("mongo.database"));
	}
	
	public DBCollection getCollection(String collection) {
		return getConnection().getCollection(collection);
	}
}
