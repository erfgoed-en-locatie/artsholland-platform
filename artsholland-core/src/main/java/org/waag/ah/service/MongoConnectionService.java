package org.waag.ah.service;

import org.waag.ah.Service;

import com.mongodb.DBCollection;

public interface MongoConnectionService extends Service {
	DBCollection getCollection(String name);
}
