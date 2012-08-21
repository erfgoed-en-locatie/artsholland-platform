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
import org.waag.ah.mongo.MongoConnectionServiceImpl;
import org.waag.ah.service.MongoConnectionService;

import com.mongodb.DBCollection;
import com.mongodb.Mongo;

@Service("importerService")
public class ImporterService implements InitializingBean  {
	private Logger logger = Logger.getLogger(ImporterService.class);
	
	@Autowired
	PlatformConfig platformConfig;
	
	
	
	@Override
	public void afterPropertiesSet() throws Exception {
		
    //MongoOperations mongoOps = new MongoTemplate(new Mongo(), "artsholland");

   // DBCollection vis = mongoOps.getCollection("artsholland.org.waag.ah.importer.ImportJobMonitor");

    
		
//		coll = mongoService.getCollection(ImportJobMonitor.class.getName());
//		coll.setObjectClass(ImportJobResult.class);		
	}
	
	

	
	/*getImporters
	
	getImports(importer)
	
	truncateImports(importer)
	
	getImport(importId)
	
	scheduleImporter(importer, interval)*/
	
	
	
}
