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
	// private static final Logger logger = LoggerFactory
	// .getLogger(RestService.class);

	RestRelation rootRelation;
	RestRelationQueryGenerator queryGenerator;

	@Autowired
	PropertiesConfiguration platformConfig;

	@Override
	public void afterPropertiesSet() throws Exception {
		RDFFormat.register(RDFJSONFormat.RESTAPIJSON);

		rootRelation = new RestRelation();

		RestRelation eventsRelation =
				rootRelation.addRelation("event",	"ah:Event", RelationQuantity.MULTIPLE, RelationType.SELF, false);
		RestRelation venuesRelation =
				rootRelation.addRelation("venue", "ah:Venue", RelationQuantity.MULTIPLE, RelationType.SELF, false);
		RestRelation productionsRelation = 
				rootRelation.addRelation("production", "ah:Production", RelationQuantity.MULTIPLE, RelationType.SELF, false);
		
		rootRelation.addRelation("genre", "ah:Genre", RelationQuantity.MULTIPLE, RelationType.SELF, false);
		rootRelation.addRelation("venuetype", "ah:VenueType", RelationQuantity.MULTIPLE, RelationType.SELF, false);

		RestRelation eventRelation = 
				eventsRelation.addRelation("cidn", "ah:Event", RelationQuantity.SINGLE, RelationType.SELF, true);
		RestRelation venueRelation = 
				venuesRelation.addRelation("cidn", "ah:Venue", RelationQuantity.SINGLE, RelationType.SELF, true);
		RestRelation productionRelation = 
				productionsRelation.addRelation("cidn", "ah:Production", RelationQuantity.SINGLE, RelationType.SELF, true);

		eventRelation.addRelation("production", "ah:Production",	RelationQuantity.SINGLE, RelationType.FORWARD, false);
		eventRelation.addRelation("venue", "ah:Venue", RelationQuantity.SINGLE, RelationType.FORWARD, false);
		eventRelation.addRelation("room", "ah:Room", RelationQuantity.MULTIPLE, RelationType.FORWARD, false);

		venueRelation.addRelation("event", "ah:Event", RelationQuantity.MULTIPLE, RelationType.BACKWARD, false);
		venueRelation.addRelation("production", "ah:Production",	RelationQuantity.MULTIPLE, RelationType.BACKWARDFORWARD, false);
		venueRelation.addRelation("room", "ah:Room", RelationQuantity.MULTIPLE, RelationType.FORWARD, false);
		venueRelation.addRelation("address", "ah:Address", RelationQuantity.MULTIPLE, RelationType.FORWARD, false);

		productionRelation.addRelation("event", "ah:Event", RelationQuantity.MULTIPLE, RelationType.BACKWARD, false);
		productionRelation.addRelation("venue", "ah:Venue", RelationQuantity.MULTIPLE, RelationType.BACKWARDFORWARD, false);

		RestRelation eventOfferingsRelation = 
				eventRelation.addRelation("offering", "gr:Offering", RelationQuantity.MULTIPLE,	RelationType.FORWARD, false);
		RestRelation eventOfferingRelation = 
		eventOfferingsRelation.addRelation("id", "gr:Offering",	RelationQuantity.SINGLE, RelationType.SELF, true);
		
		eventOfferingRelation.addRelation("price", "gr:PriceSpecification", RelationQuantity.SINGLE, RelationType.FORWARD, false);
		
		RestRelation venueAttachmentRelation = 
				venueRelation.addRelation("attachment", "ah:Attachment", RelationQuantity.MULTIPLE,	RelationType.FORWARD, false);
		venueAttachmentRelation.addRelation("id", "ah:Attachment",	RelationQuantity.SINGLE, RelationType.SELF, true);
		
		RestRelation eventAttachmentRelation = 
				eventRelation.addRelation("attachment", "ah:Attachment", RelationQuantity.MULTIPLE, RelationType.FORWARD, false);
		eventAttachmentRelation.addRelation("id", "ah:Attachment",	RelationQuantity.SINGLE, RelationType.SELF, true);
		
  	// TODO: ?this instead of ?object  ???

		/*
		 * Aanpassen:
		 * ticket
		 */
		
		SPARQLFilter venuesLocalityFilter = new SPARQLFilter("locality", "?object ah:locationAddress ?address . ?address vcard:locality ?locality.", "lcase(?locality) = lcase(\"?parameter\")");
		
		venuesRelation.addFilter(venuesLocalityFilter);

  	SPARQLFilter eventsLocalityFilter = new SPARQLFilter("locality", "?object ah:venue ?venue . ?venue ah:locationAddress ?address . ?address vcard:locality ?locality .", "lcase(?locality) = lcase(\"?parameter\")");
  	SPARQLFilter eventsBeforeFilter = new SPARQLFilter("before", "?object time:hasBeginning ?hasBeginning.", "?hasBeginning < \"?parameter\"^^xsd:dateTime");
  	SPARQLFilter eventsAfterFilter = new SPARQLFilter("after", "?object time:hasBeginning ?hasBeginning.", "?hasBeginning > \"?parameter\"^^xsd:dateTime");
  	eventsRelation.addFilter(eventsLocalityFilter);
  	eventsRelation.addFilter(eventsBeforeFilter);
  	eventsRelation.addFilter(eventsAfterFilter);

  	//public double metersToDegrees(double meters) { return meters / (Math.PI/180) / 6378137; }  	
  	double metersToDegrees = 1 / ((Math.PI/180) * 6378137);
  	
  	SPARQLFilter eventsNearbyFilter = new SPARQLFilter("nearby", "?object ah:venue ?venue . ?venue geo:geometry ?geometry .", "search:distance(?geometry, \"?parameter\"^^<http://rdf.opensahara.com/type/geo/wkt>) < ?distance * " + metersToDegrees);
  	eventsNearbyFilter.addExtraParameter("distance");
   	eventsRelation.addFilter(eventsNearbyFilter);
  	   	
   	SPARQLFilter venuesNearbyFilter = new SPARQLFilter("nearby", "?object geo:geometry ?geometry .", "search:distance(?geometry, \"?parameter\"^^<http://rdf.opensahara.com/type/geo/wkt>) < ?distance * " + metersToDegrees);
   	venuesNearbyFilter.addExtraParameter("distance");
  	venuesRelation.addFilter(venuesNearbyFilter);
    // TODO: add search:within(?geometry, "POLYGON((4 53, 4 54, 5 54, 5 53, 4 53))"^^geo:wkt)  	
  	
  	SPARQLFilter productionGenreFilter = new SPARQLFilter(
  			"genre", "?object <http://purl.org/artsholland/1.0/genre> ?genre .", "?genre = ah:genre?parameter OR ?genre = ah:?parameter"
  			);    	
  	productionsRelation.addFilter(productionGenreFilter);
  	
  	SPARQLFilter venueTypeFilter = new SPARQLFilter(
  			"type", "?object <http://purl.org/artsholland/1.0/venueType> ?type .", "?type = ah:venueType?parameter OR ?type = ah:?parameter"
  			);    	
  	venuesRelation.addFilter(venueTypeFilter);  	
  	
  	/*
  	 * Price
  	 */
   	
  	SPARQLFilter maxPriceFilter = new SPARQLFilter("max_price", "?object gr:offers ?offering . ?offering gr:hasPriceSpecification ?price . ?price gr:hasMinCurrencyValue ?lowPrice .", "?lowPrice <= ?parameter");    	
  	eventsRelation.addFilter(maxPriceFilter);  
  	
  	SPARQLFilter minPriceFilter = new SPARQLFilter("min_price", "?object gr:offers ?offering . ?offering gr:hasPriceSpecification ?price . ?price gr:hasMaxCurrencyValue ?highPrice .", "?highPrice >= ?parameter");
  	eventsRelation.addFilter(minPriceFilter);  	
  	
  	/*
  	 * Free text
  	 */
  	
  	SPARQLFilter searchFilter = new SPARQLFilter("search", "?object dc:description ?desc .", "search:text(?desc, \"?parameter\")");   
  	eventsRelation.addFilter(searchFilter);
  	productionsRelation.addFilter(searchFilter);
  	venuesRelation.addFilter(searchFilter);
  
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

		if (!query.isSingle()) {
			config.setMetaData("page", String.valueOf(params.getPage()));
			config.setMetaData("per_page", String.valueOf(params.getPerPage()));
		}
		
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
