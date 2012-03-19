package org.waag.ah.sesame;

import java.math.BigDecimal;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.datatypes.XMLDatatypeUtil;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectQuery;
import org.openrdf.repository.object.ObjectRepository;
import org.openrdf.repository.object.config.ObjectRepositoryConfig;
import org.openrdf.repository.object.config.ObjectRepositoryFactory;
import org.waag.ah.model.rdf.AttachmentImpl;
import org.waag.ah.model.rdf.AttachmentTypeImpl;
import org.waag.ah.model.rdf.EventImpl;
import org.waag.ah.model.rdf.EventStatusImpl;
import org.waag.ah.model.rdf.EventTypeImpl;
import org.waag.ah.model.rdf.GenreImpl;
import org.waag.ah.model.rdf.OfferingImpl;
import org.waag.ah.model.rdf.ProductionImpl;
import org.waag.ah.model.rdf.ProductionTypeImpl;
import org.waag.ah.model.rdf.RoomImpl;
import org.waag.ah.model.rdf.UnitPriceSpecificationImpl;
import org.waag.ah.model.rdf.VenueImpl;
import org.waag.ah.model.rdf.VenueTypeImpl;

import com.bigdata.journal.Options;
import com.bigdata.rdf.sail.BigdataSail;
import com.bigdata.rdf.sail.BigdataSailRepository;

public class DateTimeTest {

	public static void main(String[] args) throws ConfigurationException,
			RepositoryException, DatatypeConfigurationException,
			RepositoryConfigException, MalformedQueryException,
			QueryEvaluationException {
		PropertiesConfiguration properties = new PropertiesConfiguration(
				"bigdata.properties");
//		properties.setProperty(Options.FILE, "/tmp/test.jnl");
		properties.setProperty(Options.FILE, "/data/db/bigdata/bigdata.jnl");

		BigdataSail sail = new BigdataSail(
				ConfigurationConverter.getProperties(properties));
		BigdataSailRepository repo = new BigdataSailRepository(sail);
		repo.initialize();
		ObjectConnection conn = getConnection(repo);
		ValueFactory vf = conn.getValueFactory();

		conn.setAutoCommit(false);
//		conn.clear();
//		conn.commit();

//		Statement test = vf.createStatement(
//				vf.createURI("http://example.com/1"), 
//				vf.createURI("http://www.w3.org/2006/time#hasBeginning"), 
//				vf.createLiteral(DatatypeFactory.newInstance().newXMLGregorianCalendar("2012-01-01T17:00:00Z")));
		
//		System.out.println(test);
		
		conn.add(vf.createStatement(
				vf.createURI("http://example.com/1"), 
				vf.createURI("http://www.w3.org/2006/time#hasBeginning"), 
				vf.createLiteral(DatatypeFactory.newInstance().newXMLGregorianCalendar("2012-01-01T17:00:00Z"))));
		conn.add(vf.createStatement(
				vf.createURI("http://example.com/2"), 
				vf.createURI("http://www.w3.org/2006/time#hasBeginning"), 
				vf.createLiteral(DatatypeFactory.newInstance().newXMLGregorianCalendar("2012-01-05T17:00:00Z"))));
		conn.commit();

		ObjectQuery query = conn.prepareObjectQuery(QueryLanguage.SPARQL, 
			"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" + 
			"PREFIX time: <http://www.w3.org/2006/time#>\n" + 
			"SELECT DISTINCT ?datePub WHERE { " +
			"	?instance time:hasBeginning ?datePub" +
//			"	FILTER(?datePub >= '2011-01-01'^^xsd:dateTime && ?datePub < '2013-01-01'^^xsd:dateTime)." +
			"	FILTER(?datePub >= ?dtFrom && ?datePub < ?dtTo)." +
			"} ORDER BY DESC(?datePub) LIMIT 10"
		);
		
//		query.setBinding("dtFrom", conn.getValueFactory().createLiteral(
//			DatatypeFactory.newInstance().newXMLGregorianCalendar("2009-01-01T17:00:00Z")));
//		query.setBinding("dtTo", conn.getValueFactory().createLiteral(
//			DatatypeFactory.newInstance().newXMLGregorianCalendar("2014-02-01T17:00:00Z")));
		query.setBinding("dtFrom", conn.getValueFactory().createLiteral(
				XMLDatatypeUtil.parseCalendar("2009-01-01T17:00:00Z")));
		query.setBinding("dtTo", conn.getValueFactory().createLiteral(
				XMLDatatypeUtil.parseCalendar("2014-02-01T17:00:00Z")));
//		query.setBinding("dtFrom", conn.getValueFactory().createLiteral("2009-01-01T17:00:00Z", vf.createURI("http://www.w3.org/2001/XMLSchema#dateTime")));
//		query.setBinding("dtTo", conn.getValueFactory().createLiteral("2014-02-01T17:00:00Z", vf.createURI("http://www.w3.org/2001/XMLSchema#dateTime")));

		System.out.println(query.evaluate().asSet());
	}

	private static ObjectConnection getConnection(BigdataSailRepository repo)
			throws RepositoryConfigException, RepositoryException {
		ObjectRepositoryFactory repositoryFactory = new ObjectRepositoryFactory();
		ObjectRepositoryConfig config = repositoryFactory.getConfig();

		config.addConcept(EventImpl.class);
		config.addConcept(RoomImpl.class);
		config.addConcept(VenueImpl.class);
		config.addConcept(OfferingImpl.class);
		config.addConcept(UnitPriceSpecificationImpl.class);
		config.addConcept(ProductionImpl.class);
		config.addConcept(AttachmentImpl.class);
		config.addConcept(AttachmentTypeImpl.class);
		config.addConcept(EventStatusImpl.class);
		config.addConcept(EventTypeImpl.class);
		config.addConcept(GenreImpl.class);
		config.addConcept(ProductionTypeImpl.class);
		config.addConcept(VenueTypeImpl.class);

		config.addDatatype(BigDecimal.class, "xsd:decimal");
		config.addDatatype(String.class, "xsd:string");
		config.addDatatype(Integer.class, "xsd:integer");
		config.addDatatype(XMLGregorianCalendar.class, "xsd:dateTime");

		ObjectRepository repository = repositoryFactory.createRepository(
				config, repo);
		ObjectConnection conn = repository.getConnection();

		return conn;
	}
}
