package org.waag.ah.spring.service;
 
import java.util.Collection;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.stereotype.Service;
import org.waag.ah.spring.model.AppImpl;
 
@Service
public class AppService extends CRUDService<AppImpl> {

	public AppImpl findByApiKey(String apiKey) {
		Query query = entityManager.createQuery("from AppImpl a where a.apiKey=:apiKey").setParameter("apiKey", apiKey);
		try {
			AppImpl app = (AppImpl) query.getSingleResult();
			return app;
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public Collection<AppImpl> findAllByApiUserId(long id) {
		Query query = entityManager.createQuery("from AppImpl a where a.apiUser.id=:id").setParameter("id", id);
		try {
			Collection<AppImpl> apps = (Collection<AppImpl>) query.getResultList();
			return apps;
		} catch (NoResultException e) {
			return null;
		}
	}


 


    
	
}
