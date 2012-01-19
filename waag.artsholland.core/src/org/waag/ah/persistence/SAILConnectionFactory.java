package org.waag.ah.persistence;

import java.io.IOException;
import java.util.Properties;

import javax.annotation.PreDestroy;
import javax.ejb.Singleton;

import org.apache.log4j.Logger;
import org.jboss.ejb3.annotation.LocalBinding;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.sail.SailRepositoryConnection;

import com.bigdata.rdf.sail.BigdataSail;
import com.bigdata.rdf.sail.BigdataSailRepository;

@Singleton
@LocalBinding(jndiBinding = "java:global/SAILConnectionFactory")  
public class SAILConnectionFactory extends AbstractConnectionFactory  
		implements RepositoryConnectionFactory {
	private Logger logger = Logger.getLogger(SAILConnectionFactory.class);
	private static SailRepository repository = null;
	private static final String PROPERTY_FILE = "META-INF/bigdata_quads.properties";
//	private static final String PROPERTY_FILE = "META-INF/bigdata_fullfeature.properties";
	
	public SailRepositoryConnection getConnection() 
			throws IOException, RepositoryException {
		if (repository == null) {
			synchronized (this) {
				if (repository == null) {
					repository = createRepository(
							loadProperties(PROPERTY_FILE));
					repository.initialize();
					logger.info("Initialized repository: "+repository);
				}
			}
		}
		SailRepositoryConnection conn = repository.getConnection();
		conn.setAutoCommit(false);
		logger.info("Returning connection: "+conn);
		return conn;
    }
	
	@Override
	protected SailRepository createRepository(Properties properties) {
		BigdataSail sail = new BigdataSail(properties);
		return new BigdataSailRepository(sail);		
	}
	
	@PreDestroy
	@Override
	public void close() {
		if (repository != null) {
			try {
				repository.shutDown();
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
		}
	}	
}
