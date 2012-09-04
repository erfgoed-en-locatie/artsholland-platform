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
import org.waag.ah.rest.RestParameters;
import org.waag.ah.spring.util.ApiResult;
import org.waag.ah.spring.util.ApiResult.ApiResultType;

@Service
@Transactional
/*
 * TODO: never completely delete objects, only mark as expired
 * Add expired property to DbObject
 */
public abstract class CRUDService<T extends DbObject<?>> {

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

	public Object create(T object) {
		try {
			T dbObject = entityManager.merge(object);
			if (dbObject != null) {
				return dbObject;	
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

	public Object update(T object) {
		if (object != null) {
			T dbObject = entityManager.find(type, object.getId());		
			if (dbObject != null) {
				BeanUtils.copyProperties(object, dbObject);
				return entityManager.merge(dbObject);				
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

	public Collection<T> findAll(int page, int perPage) {		
		return findAll("id", false, page, perPage);		
	}
	
	public Collection<T> findAll() {		
		return findAll("id", false, 0, 0);		
	}
	
	@SuppressWarnings("unchecked")
	public Collection<T> findAll(String orderBy, boolean desc, int page, int perPage) {		
		Query query = entityManager.createQuery("SELECT e FROM " + type.getSimpleName() + " e ORDER BY e." + orderBy + (desc ? " DESC" : ""));
		if (!(page == 0 && perPage == 0)) {
			// If one of the paging parameters are non-zero, do paging
			query.setFirstResult(page);
			if (perPage == 0) {
				// TODO: use other default...
				query.setMaxResults((int) RestParameters.DEFAULT_RESULT_LIMIT);	
			} else {
				query.setMaxResults(perPage);
			}
		}		
		return (Collection<T>) query.getResultList();		
	}

}
