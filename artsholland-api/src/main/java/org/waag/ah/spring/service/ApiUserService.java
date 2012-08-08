package org.waag.ah.spring.service;
 
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.stereotype.Service;
import org.waag.ah.spring.model.ApiUserImpl;
 
@Service
public class ApiUserService extends CRUDService<ApiUserImpl> {

	public ApiUserImpl findByEmail(String email) {
		Query query = entityManager.createQuery("from ApiUserImpl a where a.email=:email").setParameter("email", email);
		try {
			ApiUserImpl a = (ApiUserImpl) query.getSingleResult();
			return a;
		} catch (NoResultException e) {
			return null;
		}
	}

}
