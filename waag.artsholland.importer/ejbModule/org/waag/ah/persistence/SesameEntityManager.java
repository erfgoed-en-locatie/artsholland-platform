package org.waag.ah.persistence;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.metamodel.Metamodel;

import org.openrdf.repository.object.ObjectRepository;

public class SesameEntityManager implements EntityManager {
	private ObjectRepository repository;

	public SesameEntityManager(ObjectRepository repository) {
		this.repository = repository;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean contains(Object arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Query createNamedQuery(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> TypedQuery<T> createNamedQuery(String arg0, Class<T> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Query createNativeQuery(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Query createNativeQuery(String arg0, Class arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Query createNativeQuery(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Query createQuery(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> TypedQuery<T> createQuery(CriteriaQuery<T> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> TypedQuery<T> createQuery(String arg0, Class<T> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void detach(Object arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T> T find(Class<T> arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T find(Class<T> arg0, Object arg1, Map<String, Object> arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T find(Class<T> arg0, Object arg1, LockModeType arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T find(Class<T> arg0, Object arg1, LockModeType arg2,
			Map<String, Object> arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void flush() {
		// TODO Auto-generated method stub

	}

	@Override
	public CriteriaBuilder getCriteriaBuilder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getDelegate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EntityManagerFactory getEntityManagerFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FlushModeType getFlushMode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LockModeType getLockMode(Object arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Metamodel getMetamodel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T getReference(Class<T> arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EntityTransaction getTransaction() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isOpen() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void joinTransaction() {
		// TODO Auto-generated method stub

	}

	@Override
	public void lock(Object arg0, LockModeType arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void lock(Object arg0, LockModeType arg1, Map<String, Object> arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T> T merge(T arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void persist(Object arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void refresh(Object arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void refresh(Object arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void refresh(Object arg0, LockModeType arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void refresh(Object arg0, LockModeType arg1, Map<String, Object> arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void remove(Object arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFlushMode(FlushModeType arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setProperty(String arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T> T unwrap(Class<T> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
