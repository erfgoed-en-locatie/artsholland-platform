package org.waag.ah.spring.service;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.waag.ah.model.DbObject;

@Service
@Transactional
public abstract class CRUDService<T extends DbObject> {

	// TODO: use Spring transactions
	
	protected Class<T> type;
	
	@PersistenceContext
	protected EntityManager entityManager;

	@PostConstruct
	@SuppressWarnings("unchecked")
	public void postConstruct() {
		type = ((Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
				.getActualTypeArguments()[0]);
	}

	public void create(T object) {		
		entityManager.merge(object); 
	}

	public T read(Object primaryKey) {		
    T dbObject = entityManager.find(type, primaryKey);
    return dbObject;
	}

	public void update(T object) {
		T dbObject = entityManager.find(type, object.getId());		
		if (dbObject != null) {
			BeanUtils.copyProperties(object, dbObject);
		}		
		entityManager.merge(dbObject);
	}

	public void delete(Object primaryKey) {		
    T dbObject = entityManager.find(type, primaryKey);
    if (dbObject != null) {
    	entityManager.remove(dbObject);
    }
	}

	@SuppressWarnings("unchecked")
	public Collection<T> findAll() {		
		Query query = entityManager.createQuery("SELECT e FROM " + type.getSimpleName() + " e");
		return (Collection<T>) query.getResultList();		
	}

}
