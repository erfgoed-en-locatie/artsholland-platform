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
public class SAILConnectionFactory extends AbstractConnectionFactory  
		implements RepositoryConnectionFactory {
	final static Logger logger = LoggerFactory.getLogger(SAILConnectionFactory.class);
//	private Logger logger = Logger.getLogger(SAILConnectionFactory.class);
	private static BigdataSailRepository repo = null;
//	private static final String PROPERTY_FILE = "META-INF/bigdata_quads.properties";
//	private static final String PROPERTY_FILE = "META-INF/bigdata_fullfeature.properties";
	private static final String PROPERTY_FILE = "META-INF/bigdata.properties";
	
//	public final static String OBJECT_NAME = "artsholland:service=SAILConnectionFactory";

	@PostConstruct
	public void create() throws IOException, RepositoryException {
//		logger.info("Creating service "+OBJECT_NAME);
		Properties properties = loadProperties(PROPERTY_FILE);
		repo = createRepository(properties);
		repo.initialize();
	}
	
	@PreDestroy
	public void destroy() throws RepositoryException {
		/*logger.info("Closing repository "+repo);
		repo.shutDown();*/
	}
	
	public SailRepositoryConnection getConnection() throws RepositoryException { 
		BigdataSailRepositoryConnection conn = repo.getReadWriteConnection();
		conn.setAutoCommit(false);
//		logger.info("Created connection: "+conn);
		return conn;
    }
	
	public SailRepositoryConnection getReadOnlyConnection() throws RepositoryException { 
		BigdataSailRepositoryConnection conn = repo.getReadOnlyConnection();
		//BigdataSailRepositoryConnection conn = repo.getUnisolatedConnection();
		 
		conn.setAutoCommit(false);
//		logger.info("Created connection: "+conn);
		return conn;
	}	
	
	@Override
	protected BigdataSailRepository createRepository(Properties properties) {
		BigdataSail sail = new BigdataSail(properties);
		return new BigdataSailRepository(sail);		
	}	
}