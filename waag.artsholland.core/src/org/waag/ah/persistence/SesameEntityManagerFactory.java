//package org.waag.ah.persistence;
//
//import java.io.File;
//import java.net.URL;
//import java.util.Map;
//
//import javax.persistence.Cache;
//import javax.persistence.EntityManager;
//import javax.persistence.EntityManagerFactory;
//import javax.persistence.PersistenceUnitUtil;
//import javax.persistence.criteria.CriteriaBuilder;
//import javax.persistence.metamodel.Metamodel;
//
//import org.apache.log4j.Logger;
//import org.openrdf.repository.Repository;
//import org.openrdf.repository.RepositoryException;
//import org.openrdf.repository.config.RepositoryConfigException;
//import org.openrdf.repository.http.HTTPRepository;
//import org.openrdf.repository.object.config.ObjectRepositoryFactory;
//import org.openrdf.repository.sail.SailRepository;
//import org.openrdf.sail.nativerdf.NativeStore;
//
//public class SesameEntityManagerFactory implements EntityManagerFactory {
//	private Logger logger = Logger.getLogger(SesameEntityManagerFactory.class);
//	private ObjectRepositoryFactory objectRepository = 
//			new ObjectRepositoryFactory();
//	private Repository repository;
//	private boolean closed = false;
//
//	protected SesameEntityManagerFactory(Repository repository) 
//			throws RepositoryException {
//		logger.info("Creating repository");
//		if (closed) {
//			throw new IllegalStateException();
//		}
//		this.repository = repository;
//		this.repository.initialize();
//	}
//	
//	public SesameEntityManagerFactory(File dataDir) throws RepositoryException {
//		this(new SailRepository(new NativeStore(dataDir)));
//	}
//
//	public SesameEntityManagerFactory(URL server, String repositoryID) 
//			throws RepositoryException {
//		this(new HTTPRepository(server.toString(), repositoryID));
//	}
//
//	@Override
//	public void close() {
//		if (closed) {
//			throw new IllegalStateException();
//		}
//		logger.info("Close called");
//		try {
//			repository.shutDown();
//			closed  = true;
//		} catch (RepositoryException e) {
//			e.printStackTrace();
//		}
//	}
//
//	@Override
//	public EntityManager createEntityManager() {
//		if (closed) {
//			throw new IllegalStateException();
//		}
//		try {
//			return new SesameEntityManager(
//					objectRepository.createRepository(repository));
//		} catch (RepositoryConfigException e) {
//			e.printStackTrace();
//		} catch (RepositoryException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//	@SuppressWarnings("rawtypes")
//	@Override
//	public EntityManager createEntityManager(Map map) {
//		if (closed) {
//			throw new IllegalStateException();
//		}
//		return createEntityManager();
//	}
//
//	@Override
//	public Cache getCache() {
//		return null;
//	}
//
//	@Override
//	public CriteriaBuilder getCriteriaBuilder() {
//		return null;
//	}
//
//	@Override
//	public Metamodel getMetamodel() {
//		return null;
//	}
//
//	@Override
//	public PersistenceUnitUtil getPersistenceUnitUtil() {
//		return null;
//	}
//
//	@Override
//	public Map<String, Object> getProperties() {
//		return null;
//	}
//
//	@Override
//	public boolean isOpen() {
//		return closed;
//	}
//}
