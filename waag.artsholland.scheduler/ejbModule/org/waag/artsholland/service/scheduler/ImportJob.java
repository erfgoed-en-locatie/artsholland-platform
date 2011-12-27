//package org.waag.artsholland.service.scheduler;
//
//import java.net.MalformedURLException;
//import java.net.URL;
//
//import javax.enterprise.inject.Model;
//
//import com.mongodb.BasicDBObject;
//import com.mongodb.DBObject;
//
//@Model
//public class ImportJob {
//	 private URL url;
//
//	 public BasicDBObject toDBObject() {
//		 BasicDBObject doc = new BasicDBObject();
//		 
//		 doc.put("url", url.toString());
//		 
//		 return doc;
//	 }
//	 
//	 public static ImportJob fromDBObject(DBObject doc) {
//		 ImportJob job = new ImportJob();
//	 
//		 try {
//			job.url = new URL((String) doc.get("url"));
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		}
//	
//		 return job;
//	 }
//	 
//	 @Override
//	 public String toString() {
//		 return url.toString();
//	 }
//}
