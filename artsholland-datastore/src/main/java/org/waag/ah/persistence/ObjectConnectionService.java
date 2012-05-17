//package org.waag.ah.persistence;
//
//import java.math.BigDecimal;
//
//import javax.annotation.PostConstruct;
//import javax.ejb.Singleton;
//import javax.xml.datatype.XMLGregorianCalendar;
//
//import org.openrdf.repository.Repository;
//import org.openrdf.repository.RepositoryException;
//import org.openrdf.repository.config.RepositoryConfigException;
//import org.openrdf.repository.object.ObjectConnection;
//import org.openrdf.repository.object.ObjectRepository;
//import org.openrdf.repository.object.config.ObjectRepositoryConfig;
//import org.openrdf.repository.object.config.ObjectRepositoryFactory;
//import org.waag.ah.ObjectConnectionFactory;
//import org.waag.ah.bigdata.BigdataConnectionService;
//import org.waag.ah.model.decorator.EventJsonDecorator;
//import org.waag.ah.model.decorator.ProductionJsonDecorator;
//import org.waag.ah.model.rdf.Attachment;
//import org.waag.ah.model.rdf.AttachmentType;
//import org.waag.ah.model.rdf.EventStatus;
//import org.waag.ah.model.rdf.EventType;
//import org.waag.ah.model.rdf.Genre;
//import org.waag.ah.model.rdf.Offering;
//import org.waag.ah.model.rdf.ProductionType;
//import org.waag.ah.model.rdf.Room;
//import org.waag.ah.model.rdf.UnitPriceSpecification;
//import org.waag.ah.model.rdf.Venue;
//import org.waag.ah.model.rdf.VenueType;
//
//@Singleton
//public class ObjectConnectionService extends BigdataConnectionService implements ObjectConnectionFactory {
//	
//	private ObjectRepositoryConfig config;
//	private ObjectRepository repository;
//	private ObjectRepositoryFactory repositoryFactory;
//	
//	@PostConstruct
//	public void create() {
//		
//		repositoryFactory = new ObjectRepositoryFactory();
//		config = repositoryFactory.getConfig();
//
//		try {		
//			
//			config.addConcept(EventJsonDecorator.class);
//			config.addConcept(ProductionJsonDecorator.class);
//						
//			config.addConcept(Venue.class);
//			config.addConcept(Offering.class);
//			config.addConcept(UnitPriceSpecification.class);			
//			config.addConcept(Attachment.class);
//			
//			config.addConcept(Room.class);			
//			config.addConcept(AttachmentType.class);
//			config.addConcept(EventStatus.class);
//			config.addConcept(EventType.class);
//			config.addConcept(Genre.class);
//			config.addConcept(ProductionType.class);
//			config.addConcept(VenueType.class);
//			
//			config.addDatatype(BigDecimal.class, "xsd:decimal");
//			config.addDatatype(String.class, "xsd:string");
//			config.addDatatype(Integer.class, "xsd:integer");
//			config.addDatatype(XMLGregorianCalendar.class, "xsd:dateTime");
//
//			repository = repositoryFactory.createRepository(config, (Repository) getSail());
//			
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	
//	@Override
//	public ObjectConnection getObjectConnection() throws RepositoryConfigException, RepositoryException {
//		
//		ObjectConnection conn = repository.getConnection();
//		//conn.setNamespace(prefix, name)
//		return conn;
//		
//	}
//	
//}