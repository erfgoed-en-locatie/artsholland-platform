package org.waag.ah.mongo;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.PlatformConfigHelper;
import org.waag.ah.service.MongoConnectionService;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;

@Startup
@Singleton
public class MongoConnectionServiceImpl implements MongoConnectionService {
	private static final Logger logger = LoggerFactory
			.getLogger(MongoConnectionServiceImpl.class);

	private Mongo mongo;
	private PropertiesConfiguration config;

	@PostConstruct
	public void connect() {
		try {
			config = PlatformConfigHelper.getConfig(); 
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
