package org.waag.ah.spring.service;

import java.lang.reflect.ParameterizedType;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.waag.ah.PlatformConfig;
import org.waag.ah.service.MongoConnectionService;

@Service
public abstract class MongoService<T extends Object> /*MongoCRUDService*/ {
	
	protected Class<T> type;
	
	@Autowired
	PlatformConfig platformConfig;
	
	@EJB(mappedName = "java:app/datastore/MongoConnectionServiceImpl")
	protected MongoConnectionService mongoService;

	protected MongoTemplate template;

	@PostConstruct
	@SuppressWarnings("unchecked")
	public void postConstruct() {
		type = ((Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
				.getActualTypeArguments()[0]);
		template = new MongoTemplate(mongoService.getMongo(), platformConfig.getString("mongo.database"));
	}
	
	public T find(Object primaryKey) {
		return null;
	}
	
	//findAll

}
