package org.waag.ah.spring.service;

import javax.ejb.EJB;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waag.ah.PlatformConfig;
import org.waag.ah.service.MongoConnectionService;

@Service("importerService")
public class ImporterService implements InitializingBean  {
	private Logger logger = Logger.getLogger(ImporterService.class);
	
	@Autowired
	PlatformConfig platformConfig;
	
	private @EJB MongoConnectionService mongoService;
	
	/*@Autowired
	ImporterRepository importerRepository;
	
	@Autowired
	ImportRepository importRepository;*/

	@Override
	public void afterPropertiesSet() throws Exception {
//		coll = mongoService.getCollection(ImportJobMonitor.class.getName());
//		coll.setObjectClass(ImportJobResult.class);		
	}
	
	

	
	/*getImporters
	
	getImports(importer)
	
	truncateImports(importer)
	
	getImport(importId)
	
	scheduleImporter(importer, interval)*/
	
	
	
}
