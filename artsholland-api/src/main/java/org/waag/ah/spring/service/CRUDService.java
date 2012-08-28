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
import org.waag.ah.spring.util.ApiResult;
import org.waag.ah.spring.util.ApiResult.ApiResultType;

@Service
@Transactional
/*
 * TODO: never completely delete objects, only mark as expired
 * Add expired property to DbObject
 */
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

	public ApiResult create(T object) {
		try {
			T dbObject = entityManager.merge(object);
			if (dbObject != null) {
				return new ApiResult(ApiResultType.SUCCESS);	
			} else {
				return new ApiResult(ApiResultType.FAILED);
			}			
		} catch (IllegalArgumentException e) {
			return new ApiResult(ApiResultType.FAILED);
		}
	}

	public T read(Object primaryKey) {		
    T dbObject = entityManager.find(type, primaryKey);
    return dbObject;
	}

	public ApiResult update(T object) {
		if (object != null) {
			T dbObject = entityManager.find(type, object.getId());		
			if (dbObject != null) {
				BeanUtils.copyProperties(object, dbObject);
				entityManager.merge(dbObject);
				return new ApiResult(ApiResultType.SUCCESS);
			}			
		}
		return new ApiResult(ApiResultType.FAILED);
	}

	public ApiResult delete(Object primaryKey) {		
    T dbObject = entityManager.find(type, primaryKey);
    if (dbObject != null) {
    	entityManager.remove(dbObject);
    	return new ApiResult(ApiResultType.SUCCESS);
    }
    return new ApiResult(ApiResultType.FAILED);
	}

	@SuppressWarnings("unchecked")
	public Collection<T> findAll() {		
		Query query = entityManager.createQuery("SELECT e FROM " + type.getSimpleName() + " e");
		return (Collection<T>) query.getResultList();		
	}

}
