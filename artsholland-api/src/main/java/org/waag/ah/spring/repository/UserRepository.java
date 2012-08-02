package org.waag.ah.spring.repository;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.waag.ah.model.App;
import org.waag.ah.model.User;

@Repository
public class UserRepository extends AbstractMongoRepository<User, String> {

	static final Logger logger = LoggerFactory.getLogger(UserRepository.class);
	
	public List<App> findAllApps(User user) {
		return mongoTemplate.find(new Query(Criteria.where("user").is("1001")), App.class);
	}




	// public void insertPersonWithNameJohnAndRandomAge() {
	// UserImpl p = new UserImpl("bert.spaan@gmail.com");
	// p.setName("Bert Spaan");
	// mongoTemplate.insert(p);
	// }

	// public void ucreatePersonCollection() {
	// if (!mongoTemplate.collectionExists(UserImpl.class)) {
	// mongoTemplate.createCollection(UserImpl.class);
	// }
	// }

}