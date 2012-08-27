package org.waag.ah.spring.service;

import javax.ejb.EJB;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.waag.ah.PlatformConfig;
import org.waag.ah.RepositoryConnectionFactory;
import org.waag.ah.model.Importer;
import org.waag.ah.mongo.MongoConnectionServiceImpl;
import org.waag.ah.service.MongoConnectionService;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

@Service("importerService")
public class ImporterService implements InitializingBean  {
	private Logger logger = Logger.getLogger(ImporterService.class);
	
	@Autowired
	PlatformConfig platformConfig;
	
	@EJB(mappedName = "java:app/datastore/MongoConnectionServiceImpl")
	private MongoConnectionService mongoService;

	private DBCollection collection;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		
		collection = mongoService.getCollection("org.waag.ah.importer.importJobMonitor");
		
		//MongoOperations mongoOps = new MongoTemplate(new Mongo(), "artsholland");

   // DBCollection vis = mongoOps.getCollection("artsholland.org.waag.ah.importer.ImportJobMonitor");

    
		
//		coll = mongoService.getCollection(ImportJobMonitor.class.getName());
//		coll.setObjectClass(ImportJobResult.class);		
	}
	
	public Importer getImporters() {
		DBObject vis = new BasicDBObject();
		return null;
		//collection.find(query, fields, numToSkip, batchSize)
		
	}
	
	//getImporters
	//db.org.waag.ah.importer.ImportJobMonitor.distinct("jobClass")
	
	/*getImporters
	
	getImports(importer)
	
	truncateImports(importer)
	
	getImport(importId)
	
	scheduleImporter(importer, interval)*/
	
	
	
}
