package org.waag.ah.spring.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.waag.ah.model.Import;
import org.waag.ah.spring.model.ImportImpl;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@Service("importService")
public class ImportService extends MongoService<Import>  {

	public List<Import> findAllByImporterId(String id) {
		List<Import> imports = new ArrayList<Import>();
		
		BasicDBObject query = new BasicDBObject();
		query.put("jobClass", id);
		
		DBCollection coll = mongoService.getCollection("org.waag.ah.importer.ImportJobMonitor");
		
		// Paging:
		// List obj = collection.find( query ).skip( 1000 ).limit( 100 ).toArray();
		
		DBCursor cursor = coll.find(query);
		
		 while (cursor.hasNext()) {
	     DBObject obj = cursor.next();	     

	     boolean success = false;
	     Date timestamp = new Date();
	     	          
	     success = (Boolean) obj.get("success");	  
    	 ObjectId objId = (ObjectId) obj.get("_id");	     
    	 timestamp = new Date((Long) obj.get("timestamp"));	     
    	 
	     ImportImpl importImpl = new ImportImpl();
	     
	     importImpl.setId(objId.toString());
	     importImpl.setSuccessful(success);
	     importImpl.setTimestamp(timestamp);
	     
	     imports.add(importImpl);	     
		 }
		
		return imports;
	}
		
	
	
	
	
}
