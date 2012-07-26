package org.waag.ah.spring.repository;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.xml.registry.infomodel.User;

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
public abstract class AbstractMongoRepository<T> {

	Class<T> type;

	@EJB(mappedName = "java:app/datastore/MongoConnectionServiceImpl")
	private MongoConnectionService mongoService;

	@Autowired
	protected PropertiesConfiguration platformConfig;

	protected MongoTemplate mongoTemplate;

	 public AbstractMongoRepository(Class<T> type) {
     this.type = type;
 }
	
	@PostConstruct
	public void connect() {
		Mongo mongo = mongoService.getMongo();
		mongoTemplate = new MongoTemplate(mongo,
				platformConfig.getString("mongo.database"));
	}

	public List<T> getAllObjects() {
		return (List<T>) mongoTemplate.findAll(type.getClass());
	}

	// public List<T> getAllObjects();
	// public void saveObject(T object);
	// public T getObject(String id);
	// public WriteResult updateObject(String id, String name);
	// public void deleteObject(String id);
	// public void createCollection();
	// public void dropCollection();
}