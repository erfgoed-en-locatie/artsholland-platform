package org.waag.ah.spring.repository;

import java.util.List;
  
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.waag.ah.mongo.model.MongoUser;
 
@Repository
public class MongoUserRepository extends AbstractMongoRepository {
	
    static final Logger logger = LoggerFactory.getLogger(MongoUserRepository.class);
  
    public void logAllPersons() {
        List<MongoUser> results = mongoTemplate.findAll(MongoUser.class);
        logger.info("Total amount of persons: {}", results.size());
        logger.info("Results: {}", results);
    }
 
    public void insertPersonWithNameJohnAndRandomAge() {

 
        MongoUser p = new MongoUser("bert.spaan@gmail.com");
        p.setName("Bert Spaan");
 
        mongoTemplate.insert(p);
    }
 
    /**
     * Create a {@link Person} collection if the collection does not already exists
     */
    public void createPersonCollection() {
        if (!mongoTemplate.collectionExists(MongoUser.class)) {
            mongoTemplate.createCollection(MongoUser.class);
        }
    }
 
    /**
     * Drops the {@link Person} collection if the collection does already exists
     */
    public void dropPersonCollection() {
        if (mongoTemplate.collectionExists(MongoUser.class)) {
            mongoTemplate.dropCollection(MongoUser.class);
        }
    }
}