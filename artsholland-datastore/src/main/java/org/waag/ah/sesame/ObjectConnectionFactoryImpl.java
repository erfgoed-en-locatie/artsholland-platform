package org.waag.ah.sesame;

import java.io.IOException;
import java.math.BigDecimal;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.xml.datatype.XMLGregorianCalendar;

import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectRepository;
import org.openrdf.repository.object.config.ObjectRepositoryConfig;
import org.openrdf.repository.object.config.ObjectRepositoryFactory;
import org.openrdf.repository.object.exceptions.ObjectStoreConfigException;
import org.waag.ah.ObjectConnectionFactory;
import org.waag.ah.RepositoryConnectionFactory;
import org.waag.ah.model.rdf.EventImpl;
import org.waag.ah.model.rdf.ProductionImpl;
import org.waag.ah.model.rdf.RoomImpl;

@Singleton
public class ObjectConnectionFactoryImpl implements ObjectConnectionFactory {
	private ObjectRepositoryConfig config;
	private ObjectRepository repository;
	private ObjectRepositoryFactory repositoryFactory;
	
	@EJB(mappedName="java:module/BigdataConnectionService")
	private RepositoryConnectionFactory bigdataConnection;
	
	@PostConstruct
	public void create() {
		
		repositoryFactory = new ObjectRepositoryFactory();
		config = repositoryFactory.getConfig();
		
		try {		
			config.addConcept(ProductionImpl.class);			
			config.addConcept(RoomImpl.class);
			config.addConcept(EventImpl.class);
			config.addConcept(VenueImpl.class);			
			
			config.addDatatype(BigDecimal.class, "xsd:decimal");
			config.addDatatype(String.class, "xsd:string");
			config.addDatatype(Integer.class, "xsd:integer");
			config.addDatatype(XMLGregorianCalendar.class, "xsd:dateTime");

			repository = repositoryFactory.createRepository(config, 
					bigdataConnection.getRepository());
			
		} catch (ObjectStoreConfigException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RepositoryConfigException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public ObjectConnection getObjectConnection() throws RepositoryConfigException, RepositoryException {
		
		ObjectConnection conn = repository.getConnection();
		//conn.setNamespace(prefix, name)
		return conn;
		
	}	
	
}