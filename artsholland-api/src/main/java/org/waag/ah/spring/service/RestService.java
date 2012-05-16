package org.waag.ah.spring.service;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.rio.RDFFormat;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waag.ah.rdf.RDFJSONFormat;
import org.waag.ah.rdf.RDFWriterConfig;
import org.waag.ah.rdf.RdfQueryDefinition;
import org.waag.ah.rest.RestParameters;
import org.waag.ah.rest.model.RestRelation;
import org.waag.ah.rest.model.RestRelation.RelationQuantity;
import org.waag.ah.rest.model.RestRelation.RelationType;
import org.waag.ah.rest.model.SPARQLFilter;
import org.waag.ah.rest.util.RestRelationQueryGenerator;

@Service("restService")
public class RestService implements InitializingBean {
//	private static final Logger logger = LoggerFactory
//			.getLogger(RestService.class);

	RestRelation rootRelation;
	RestRelationQueryGenerator queryGenerator;
	
	@Autowired
	PropertiesConfiguration platformConfig;

	@Override
	public void afterPropertiesSet() throws Exception {
		RDFFormat.register(RDFJSONFormat.RESTAPIJSON);

		rootRelation = new RestRelation();

		RestRelation eventsRelation =
				rootRelation.addRelation("event",	"Event", RelationQuantity.MULTIPLE, RelationType.SELF, false);
		RestRelation venuesRelation =
				rootRelation.addRelation("venue", "Venue", RelationQuantity.MULTIPLE, RelationType.SELF, false);
		RestRelation productionsRelation = 
				rootRelation.addRelation("production", "Production", RelationQuantity.MULTIPLE, RelationType.SELF, false);
		
		rootRelation.addRelation("genre", "Genre", RelationQuantity.MULTIPLE, RelationType.SELF, false);
		rootRelation.addRelation("venuetype", "VenueType", RelationQuantity.MULTIPLE, RelationType.SELF, false);

		RestRelation eventRelation = 
				eventsRelation.addRelation("cidn", "Event", RelationQuantity.SINGLE, RelationType.SELF, true);
		RestRelation venueRelation = 
				venuesRelation.addRelation("cidn", "Venue", RelationQuantity.SINGLE, RelationType.SELF, true);
		RestRelation productionRelation = 
				productionsRelation.addRelation("cidn", "Production", RelationQuantity.SINGLE, RelationType.SELF, true);

		eventRelation.addRelation("production", "Production",	RelationQuantity.SINGLE, RelationType.FORWARD, false);
		eventRelation.addRelation("venue", "Venue", RelationQuantity.SINGLE, RelationType.FORWARD, false);
		eventRelation.addRelation("room", "Room", RelationQuantity.MULTIPLE, RelationType.FORWARD, false);

		venueRelation.addRelation("event", "Event", RelationQuantity.MULTIPLE, RelationType.BACKWARD, false);
		venueRelation.addRelation("production", "Production",	RelationQuantity.MULTIPLE, RelationType.BACKWARDFORWARD, false);
		venueRelation.addRelation("room", "Room", RelationQuantity.MULTIPLE, RelationType.FORWARD, false);

		productionRelation.addRelation("event", "Event", RelationQuantity.MULTIPLE, RelationType.BACKWARD, false);
		productionRelation.addRelation("venue", "Venue", RelationQuantity.MULTIPLE, RelationType.BACKWARDFORWARD, false);

		RestRelation eventTicketRelation = 
				eventRelation.addRelation("ticket", "Ticket", RelationQuantity.MULTIPLE,	RelationType.FORWARD, false);
		eventTicketRelation.addRelation("id", "Ticket",	RelationQuantity.SINGLE, RelationType.SELF, true);
		
		RestRelation venueAttachmentRelation = 
				venueRelation.addRelation("attachment", "Attachment", RelationQuantity.MULTIPLE,	RelationType.FORWARD, false);
		venueAttachmentRelation.addRelation("id", "Attachment",	RelationQuantity.SINGLE, RelationType.SELF, true);
		
		RestRelation eventAttachmentRelation = 
				eventRelation.addRelation("attachment", "Attachment", RelationQuantity.MULTIPLE, RelationType.FORWARD, false);
		eventAttachmentRelation.addRelation("id", "Attachment",	RelationQuantity.SINGLE, RelationType.SELF, true);
		
  	// TODO: ?this instead of ?object  ???
  	//SPARQLFilter venuesLocalityFilter = new SPARQLFilter("locality", "?object vcard:locality ?locality.", "fn:lower-case(?locality) = fn:lower-case(\"?parameter\")");
  	SPARQLFilter venuesLocalityFilter = new SPARQLFilter("locality", "?object vcard:locality ?locality.", "lcase(?locality) = lcase(\"?parameter\")");
  	venuesRelation.addFilter(venuesLocalityFilter);

  	SPARQLFilter eventsLocalityFilter = new SPARQLFilter("locality", "?object <http://purl.org/artsholland/1.0/venue> ?venue . ?venue vcard:locality ?locality .", "fn:lower-case(?locality) = fn:lower-case(\"?parameter\")");
  	SPARQLFilter eventsBeforeFilter = new SPARQLFilter("before", "?object time:hasBeginning ?hasBeginning.", "?hasBeginning < \"?parameter\"^^xsd:dateTime");
  	SPARQLFilter eventsAfterFilter = new SPARQLFilter("after", "?object time:hasBeginning ?hasBeginning.", "?hasBeginning > \"?parameter\"^^xsd:dateTime");
  	eventsRelation.addFilter(eventsLocalityFilter);
  	eventsRelation.addFilter(eventsBeforeFilter);
  	eventsRelation.addFilter(eventsAfterFilter);

  	//public double metersToDegrees(double meters) { return meters / (Math.PI/180) / 6378137; } 	
  	
  	SPARQLFilter eventsNearbyFilter = new SPARQLFilter("nearby", "?object <http://purl.org/artsholland/1.0/venue> ?venue . ?venue <http://purl.org/artsholland/1.0/wkt> ?geometry .", "search:distance(?geometry, \"?parameter\"^^<http://rdf.opensahara.com/type/geo/wkt>) < ?distance * 365440977.627703");
  	eventsNearbyFilter.addExtraParameter("distance");
   	eventsRelation.addFilter(eventsNearbyFilter);
  	   	
   	SPARQLFilter venuesNearbyFilter = new SPARQLFilter("nearby", "?object <http://purl.org/artsholland/1.0/wkt> ?geometry .", "search:distance(?geometry, \"?parameter\"^^<http://rdf.opensahara.com/type/geo/wkt>) < ?distance * 365440977.627703");
   	venuesNearbyFilter.addExtraParameter("distance");
  	venuesRelation.addFilter(venuesNearbyFilter);
  	
  	SPARQLFilter productionGenreFilter = new SPARQLFilter(
  			"genre", "?object <http://purl.org/artsholland/1.0/genre> ?genre .", "?genre = ah:genre?parameter OR ?genre = ah:?parameter"
  			);    	
  	productionsRelation.addFilter(productionGenreFilter);
  	
  	SPARQLFilter venueTypeFilter = new SPARQLFilter(
  			"type", "?object <http://purl.org/artsholland/1.0/venueType> ?type .", "?type = ah:venueType?parameter OR ?type = ah:?parameter"
  			);    	
  	venuesRelation.addFilter(venueTypeFilter);
  	
  	
  	// search:text(?bandname, "Florence & Machine") &&
    // search:within(?geometry, "POLYGON((4 53, 4 54, 5 54, 5 53, 4 53))"^^geo:wkt)
  	
  	queryGenerator = new RestRelationQueryGenerator(rootRelation);
	}

	public RdfQueryDefinition getObjectQuery(RestParameters params)
			throws MalformedQueryException {
		RdfQueryDefinition query = queryGenerator.generate(params);
		RDFWriterConfig config = getDefaultWriterConfig(params);
		query.setWriterConfig(config);
		config.setWrapResults(false);
		return query;
	}

	public RdfQueryDefinition getPagedQuery(RestParameters params)
			throws MalformedQueryException {
		RdfQueryDefinition query = queryGenerator.generate(params);
		if (query == null) {
			throw new MalformedQueryException();
		}
		RDFWriterConfig config = getDefaultWriterConfig(params);
		query.setWriterConfig(config);
		
		config.setMetaData("page", String.valueOf(params.getPage()));
		config.setMetaData("per_page", String.valueOf(params.getPerPage()));
		
		config.setWrapResults(true);
		return query;
	}
	
	private RDFWriterConfig getDefaultWriterConfig(RestParameters params) {
		RDFWriterConfig config = new RDFWriterConfig();
		config.setPrettyPrint(params.getPretty());
		config.setBaseUri(platformConfig.getString("platform.baseUri"));		
		return config;
	}
}
