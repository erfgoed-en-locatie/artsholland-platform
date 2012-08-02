package org.waag.ah.spring.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.waag.ah.model.App;
 
@Repository
public class AppRepository extends AbstractMongoRepository<App, String> {
	
		static final Logger logger = LoggerFactory.getLogger(AppRepository.class);
  

 


}