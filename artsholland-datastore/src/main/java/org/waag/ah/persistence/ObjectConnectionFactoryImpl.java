package org.waag.ah.persistence;

import java.io.IOException;
import java.math.BigDecimal;

import javax.annotation.PostConstruct;
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
import org.waag.ah.model.rdf.EventImpl;
import org.waag.ah.model.rdf.OfferingImpl;
import org.waag.ah.model.rdf.ProductionImpl;
import org.waag.ah.model.rdf.RoomImpl;
import org.waag.ah.model.rdf.UnitPriceSpecificationImpl;
import org.waag.ah.model.rdf.VenueImpl;

@Singleton
public class ObjectConnectionFactoryImpl extends SAILConnectionFactory implements ObjectConnectionFactory {
	
	private ObjectRepositoryConfig config;
	private ObjectRepository repository;
	private ObjectRepositoryFactory repositoryFactory;
	
	@PostConstruct
	public void create() {
		
		repositoryFactory = new ObjectRepositoryFactory();
		config = repositoryFactory.getConfig();
		
		try {		
			
			if (getRepository() == null) {
				super.create();
			}		
			
			config.addConcept(EventImpl.class);
			config.addConcept(RoomImpl.class);			
			config.addConcept(VenueImpl.class);
			config.addConcept(OfferingImpl.class);
			config.addConcept(UnitPriceSpecificationImpl.class);
			config.addConcept(ProductionImpl.class);
			
			config.addDatatype(BigDecimal.class, "xsd:decimal");
			config.addDatatype(String.class, "xsd:string");
			config.addDatatype(Integer.class, "xsd:integer");
			config.addDatatype(XMLGregorianCalendar.class, "xsd:dateTime");

			repository = repositoryFactory.createRepository(config, getRepository());
			
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