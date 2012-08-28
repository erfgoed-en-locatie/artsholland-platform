package org.waag.ah.spring.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.waag.ah.model.Importer;
import org.waag.ah.spring.model.ImporterImpl;

import com.mongodb.BasicDBList;
import com.mongodb.CommandResult;

@Service("importerService")
public class ImporterService extends MongoService<Importer>  {
		
	public List<Importer> getImporters() {
				List<Importer> importers = new ArrayList<Importer>();
		
		String json = "{ distinct : \"org.waag.ah.importer.ImportJobMonitor\", key : \"jobClass\" }";
		
		CommandResult result = template.executeCommand(json);
		
		if (result.containsField("values")) {
			BasicDBList values = (BasicDBList) result.get("values");
			for (Object name : values) {
				ImporterImpl importer = new ImporterImpl();
				importer.setId((String) name);
				importers.add(importer);
			}
		}		
		return importers;		
	}
	
	//getImporters
	//db.org.waag.ah.importer.ImportJobMonitor.distinct("jobClass")
	
	/*getImporters
	
	getImports(importer)
	
	truncateImports(importer)
	
	getImport(importId)
	
	scheduleImporter(importer, interval)*/
	
	
	
}
