package org.waag.ah.persistence;

import java.io.IOException;
import java.util.Properties;

import javax.annotation.PreDestroy;
import javax.ejb.Singleton;

import org.apache.log4j.Logger;
import org.jboss.ejb3.annotation.LocalBinding;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sparql.SPARQLConnection;
import org.openrdf.repository.sparql.SPARQLRepository;

@Singleton
@LocalBinding(jndiBinding = "java:global/SPARQLConnectionFactory") 
public class SPARQLConnectionFactory extends AbstractConnectionFactory 
		implements RepositoryConnectionFactory {
	private Logger logger = Logger.getLogger(SPARQLConnectionFactory.class);
	private SPARQLRepository repository;
	private static final String PROPERTY_FILE = "META-INF/bigdata_sparql.properties";
	
	@Override
	public SPARQLConnection getConnection() throws IOException,
			RepositoryException {
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
		return (SPARQLConnection) repository.getConnection();
	}
	
	@Override
	protected SPARQLRepository createRepository(Properties props) {
		return new SPARQLRepository(
				props.getProperty("org.waag.ah.persistence.sparql.queryEndpointUrl"), 
				props.getProperty("org.waag.ah.persistence.sparql.updateEndpointUrl"));
	}

	@PreDestroy
	@Override
	public void close() throws IOException {
		try {
			repository.shutDown();
		} catch (RepositoryException e) {
			throw new IOException(e.getMessage(), e);
		}
	}
}
