package org.waag.ah.service;

import org.waag.ah.Service;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;

public interface MongoConnectionService extends Service {
	public Mongo getMongo();
	public DB getDatabase();
	public DBCollection getCollection(String name);
}
