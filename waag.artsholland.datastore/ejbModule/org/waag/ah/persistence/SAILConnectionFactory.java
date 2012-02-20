package org.waag.ah.persistence;

import java.io.IOException;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;

import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bigdata.rdf.sail.BigdataSail;
import com.bigdata.rdf.sail.BigdataSailRepository;
import com.bigdata.rdf.sail.BigdataSailRepositoryConnection;

@Singleton
public class SAILConnectionFactory extends AbstractConnectionFactory {
	final static Logger logger = LoggerFactory.getLogger(SAILConnectionFactory.class);
	private static BigdataSailRepository repo = null;
	private static final String PROPERTY_FILE = "META-INF/bigdata.properties";

	@PostConstruct
	public void create() throws IOException, RepositoryException {
		Properties properties = loadProperties(PROPERTY_FILE);
		repo = createRepository(properties);
		repo.initialize();
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
	
	public SailRepositoryConnection getConnection() throws RepositoryException { 
		BigdataSailRepositoryConnection conn = 
				(BigdataSailRepositoryConnection) repo.getReadWriteConnection();
		conn.setAutoCommit(false);
		return conn;
    }
	
	public SailRepositoryConnection getReadOnlyConnection() throws RepositoryException { 
		BigdataSailRepositoryConnection conn = 
				(BigdataSailRepositoryConnection) repo.getReadOnlyConnection();
		conn.setAutoCommit(false);
		return conn;
	}	
	
	@Override
	protected BigdataSailRepository createRepository(Properties properties) {
		BigdataSail sail = new BigdataSail(properties);
		return new BigdataSailRepository(sail);		
	}	
}