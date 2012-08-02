package org.waag.ah.spring.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waag.ah.PlatformConfig;

@Service("importAdminService")
public class ImportAdminService implements InitializingBean  {
	private Logger logger = Logger.getLogger(ImportAdminService.class);
	
	@Autowired
	PlatformConfig platformConfig;
	
	/*@Autowired
	ImporterRepository importerRepository;
	
	@Autowired
	ImportRepository importRepository;*/

	@Override
	public void afterPropertiesSet() throws Exception {
		
	}
	
	/*getImporters
	
	getImports(importer)
	
	truncateImports(importer)
	
	getImport(importId)
	
	scheduleImporter(importer, interval)*/
	
	
	
}
