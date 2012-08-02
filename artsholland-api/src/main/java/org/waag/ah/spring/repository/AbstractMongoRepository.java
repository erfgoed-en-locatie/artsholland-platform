package org.waag.ah.spring.repository;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.waag.ah.service.MongoConnectionService;

import com.mongodb.Mongo;

/**
 * Repository for {@link Person}s
 * 
 */
@Repository
@Singleton
public abstract class AbstractMongoRepository<T, ID extends Serializable> {

	private Class<T> type;

	@EJB(mappedName = "java:app/datastore/MongoConnectionServiceImpl")
	private MongoConnectionService mongoService;

	@Autowired
	protected PropertiesConfiguration platformConfig;

	protected MongoTemplate mongoTemplate;

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void connect() {
		type = ((Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
				.getActualTypeArguments()[0]);

		Mongo mongo = mongoService.getMongo();
		mongoTemplate = new MongoTemplate(mongo,
				platformConfig.getString("mongo.database"));
	}

	public List<T> findAll() {
		return mongoTemplate.findAll(type);
	}
	
	public List<T> findAll(Sort sort) {
		return null;		
	}

  public Page<T> findAll(Pageable pageable) {
		return null;  	
  }

	public void saveObject(T object) {
		mongoTemplate.insert(object);
	}

	public T getObject(ID id) {
		return mongoTemplate.findOne(new Query(Criteria.where("id").is(id)), type);
	}

//	public WriteResult updateObject(String id, String name) {
//		return mongoTemplate.updateFirst(new Query(Criteria.where("id").is(id)),
//				Update.update("name", name), type);
//	}
	
	public void deleteObject(ID id) {
		mongoTemplate.remove(new Query(Criteria.where("id").is(id)), type);
	}


}