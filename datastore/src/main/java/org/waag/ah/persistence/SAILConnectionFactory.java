package org.waag.ah.persistence;

import java.io.IOException;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;

import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.RepositoryConnectionFactory;

import com.bigdata.rdf.sail.BigdataSail;
import com.bigdata.rdf.sail.BigdataSailRepository;
import com.bigdata.rdf.sail.BigdataSailRepositoryConnection;

@Singleton
public class SAILConnectionFactory extends AbstractConnectionFactory implements RepositoryConnectionFactory {
	final static Logger logger = LoggerFactory.getLogger(SAILConnectionFactory.class);
	private static BigdataSailRepository repo = null;
	private static final String PROPERTY_FILE = "/bigdata.properties";

	@PostConstruct
	public void create() throws IOException, RepositoryException {
		try {
			logger.info("STARING REPO");
			Properties properties = loadProperties(PROPERTY_FILE);
			repo = createRepository(properties);
			repo.initialize();
		} catch (org.openrdf.repository.RepositoryException e) {
			throw (RepositoryException) e;
		}
	}
	
	@PreDestroy
	public void destroy() {
		logger.info("Closing repository "+repo);
//		try {
//			repo.shutDown();
//		} catch (RepositoryException e) {
//			logger.error(e.getMessage());
//		}
	}
	
	public RepositoryConnection getConnection() throws RepositoryException { 
		BigdataSailRepositoryConnection conn = repo.getReadWriteConnection();
		conn.setAutoCommit(false);
		return conn;
    }
	
	public RepositoryConnection getReadOnlyConnection() throws RepositoryException { 
		BigdataSailRepositoryConnection conn = repo.getReadOnlyConnection();
		return conn;
	}	
	
	@Override
	protected BigdataSailRepository createRepository(Properties properties) {
		BigdataSail sail = new BigdataSail(properties);
		return new BigdataSailRepository(sail);		
	}	
}