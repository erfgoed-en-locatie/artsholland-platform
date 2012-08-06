package org.waag.ah.spring.service;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.waag.ah.model.DbObject;

@Service
@Transactional
public abstract class CRUDService<T extends DbObject> {

	// TODO: user Spring transactions
	
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

	public T read(long id) {		
    T dbObject = entityManager.find(type, id);
    return dbObject;
	}

	// TODO: update all fields with reflection?
	public abstract void update(T object);

	public void delete(T object) {		
    T dbObject = entityManager.find(type, object.getId());
    entityManager.remove(dbObject); 
	}

	public Collection<T> findAll() {		
		Query query = entityManager.createQuery("SELECT e FROM " + type.getSimpleName() + " e");
		return (Collection<T>) query.getResultList();		
	}

}
