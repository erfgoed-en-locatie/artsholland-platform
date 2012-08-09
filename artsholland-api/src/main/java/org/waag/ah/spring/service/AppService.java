package org.waag.ah.spring.service;
 
import java.util.Collection;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.stereotype.Service;
import org.waag.ah.model.Role;
import org.waag.ah.spring.model.AppImpl;
import org.waag.ah.spring.model.RoleImpl;
 
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
		Query query = entityManager.createQuery("from AppImpl a where a.apiUserId=:id").setParameter("id", id);
		try {
			@SuppressWarnings("unchecked")
			Collection<AppImpl> apps = (Collection<AppImpl>) query.getResultList();
			return apps;
		} catch (NoResultException e) {
			return null;
		}
	}

	public Role getAppRoleByApiKey(String apiKey) {
		Query query = entityManager.createQuery("from RoleImpl a where a.id=(select app.roleId from AppImpl app where app.apiKey=:apiKey)").setParameter("apiKey", apiKey);
		try {
			RoleImpl role = (RoleImpl) query.getSingleResult();
			return role;
		} catch (NoResultException e) {
			return null;
		}
	}

	public void deleteAllByApiUserId(long apiUserId) {		
		Query query = entityManager.createQuery("delete from AppImpl where apiUserId = :apiUserId").setParameter("apiUserId", apiUserId);
    query.executeUpdate();		
	}	
}
