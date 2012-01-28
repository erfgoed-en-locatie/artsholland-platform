package org.waag.ah.persistence;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.jboss.ejb3.annotation.LocalBinding;
import org.jboss.ejb3.annotation.Management;
import org.jboss.ejb3.annotation.Service;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepositoryConnection;
import org.waag.ah.ServiceManagement;

import com.bigdata.rdf.sail.BigdataSail;
import com.bigdata.rdf.sail.BigdataSailRepository;
import com.bigdata.rdf.sail.BigdataSailRepositoryConnection;

@Service(objectName=SAILConnectionFactory.OBJECT_NAME)
@Management(ServiceManagement.class)
@LocalBinding(jndiBinding = "java:global/SAILConnectionFactory")  
public class SAILConnectionFactory extends AbstractConnectionFactory  
		implements ServiceManagement, RepositoryConnectionFactory {
	private Logger logger = Logger.getLogger(SAILConnectionFactory.class);
	private static BigdataSailRepository repo = null;
//	private static final String PROPERTY_FILE = "META-INF/bigdata_quads.properties";
//	private static final String PROPERTY_FILE = "META-INF/bigdata_fullfeature.properties";
	private static final String PROPERTY_FILE = "META-INF/bigdata.properties";
	
	public final static String OBJECT_NAME = "artsholland:service=SAILConnectionFactory";

	public void create() throws IOException, RepositoryException {
//		logger.info("Creating service "+OBJECT_NAME);
		Properties properties = loadProperties(PROPERTY_FILE);
		repo = createRepository(properties);
		repo.initialize();
	}
	
	public void destroy() {
//		logger.info("Closing repository "+repo);
	}
	
	public SailRepositoryConnection getConnection() throws RepositoryException { 
		BigdataSailRepositoryConnection conn = 
				(BigdataSailRepositoryConnection) repo.getReadWriteConnection();
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