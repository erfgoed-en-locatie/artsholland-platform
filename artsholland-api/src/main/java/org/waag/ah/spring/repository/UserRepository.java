package org.waag.ah.spring.repository;

import java.util.List;
  
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.waag.ah.model.User;
import org.waag.ah.spring.model.UserImpl;
 
@Repository
public class UserRepository extends AbstractMongoRepository<User> {
	
    static final Logger logger = LoggerFactory.getLogger(UserRepository.class);
  
    public void logAllPersons() {
        List<UserImpl> results = mongoTemplate.findAll(UserImpl.class);
        logger.info("Total amount of persons: {}", results.size());
        logger.info("Results: {}", results);
        
    }
    

    
 
//    public void insertPersonWithNameJohnAndRandomAge() {
//        UserImpl p = new UserImpl("bert.spaan@gmail.com");
//        p.setName("Bert Spaan");
//        mongoTemplate.insert(p);
//    }
 
//    public void ucreatePersonCollection() {
//        if (!mongoTemplate.collectionExists(UserImpl.class)) {
//            mongoTemplate.createCollection(UserImpl.class);
//        }
//    }
 
    
}