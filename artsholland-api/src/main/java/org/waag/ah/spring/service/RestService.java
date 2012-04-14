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

		RestRelation eventsRelation = rootRelation.addRelation("event",
				"Event", RelationQuantity.MULTIPLE, RelationType.SELF, false);
		RestRelation venuesRelation = rootRelation.addRelation("venue",
				"Venue", RelationQuantity.MULTIPLE, RelationType.SELF, false);
		RestRelation productionsRelation = rootRelation.addRelation(
				"production", "Production", RelationQuantity.MULTIPLE,
				RelationType.SELF, false);

		RestRelation eventRelation = eventsRelation.addRelation("cidn",
				"Event", RelationQuantity.SINGLE, RelationType.SELF, true);
		RestRelation venueRelation = venuesRelation.addRelation("cidn",
				"Venue", RelationQuantity.SINGLE, RelationType.SELF, true);
		RestRelation productionRelation = productionsRelation.addRelation(
				"cidn", "Production", RelationQuantity.SINGLE,
				RelationType.SELF, true);

		eventRelation.addRelation("production", "Production",
				RelationQuantity.SINGLE, RelationType.FORWARD, false);
		eventRelation.addRelation("venue", "Venue", RelationQuantity.SINGLE,
				RelationType.FORWARD, false);
		eventRelation.addRelation("room", "Room", RelationQuantity.MULTIPLE,
				RelationType.FORWARD, false);

		venueRelation.addRelation("event", "Event", RelationQuantity.MULTIPLE,
				RelationType.BACKWARD, false);
		venueRelation.addRelation("production", "Production",
				RelationQuantity.MULTIPLE, RelationType.BACKWARDFORWARD, false);
		venueRelation.addRelation("room", "Room", RelationQuantity.MULTIPLE,
				RelationType.FORWARD, false);

		productionRelation.addRelation("event", "Event",
				RelationQuantity.MULTIPLE, RelationType.BACKWARD, false);
		productionRelation.addRelation("venue", "Venue",
				RelationQuantity.MULTIPLE, RelationType.BACKWARDFORWARD, false);

		RestRelation venueAttachmentRelation = venueRelation.addRelation(
				"attachment", "Attachment", RelationQuantity.MULTIPLE,
				RelationType.FORWARD, false);
		venueAttachmentRelation.addRelation("id", "Attachment",
				RelationQuantity.SINGLE, RelationType.SELF, true);

		RestRelation eventAttachmentRelation = eventRelation.addRelation(
				"attachment", "Attachment", RelationQuantity.MULTIPLE,
				RelationType.FORWARD, false);
		eventAttachmentRelation.addRelation("id", "Attachment",
				RelationQuantity.SINGLE, RelationType.SELF, true);

		// TODO: ?this instead of ?object ???
		SPARQLFilter venuesLocalityFilter = new SPARQLFilter("locality",
				"?object vcard:locality ?locality.",
				"?locality = \"?parameter\"");
		venuesRelation.addFilter(venuesLocalityFilter);

		SPARQLFilter eventsLocalityFilter = new SPARQLFilter(
				"locality",
				"?object <http://purl.org/artsholland/1.0/venue> ?venue . ?venue vcard:locality ?locality .",
				"?locality = \"?parameter\"");
		SPARQLFilter eventsBeforeFilter = new SPARQLFilter("before",
				"?object time:hasBeginning ?hasBeginning.",
				"?hasBeginning < \"?parameter\"^^xsd:dateTime");
		SPARQLFilter eventsAfterFilter = new SPARQLFilter("after",
				"?object time:hasBeginning ?hasBeginning.",
				"?hasBeginning > \"?parameter\"^^xsd:dateTime");
		eventsRelation.addFilter(eventsLocalityFilter);
		eventsRelation.addFilter(eventsBeforeFilter);
		eventsRelation.addFilter(eventsAfterFilter);

		queryGenerator = new RestRelationQueryGenerator(rootRelation);
	}

	public RdfQueryDefinition getObjectQuery(RestParameters params)
			throws MalformedQueryException {
		RdfQueryDefinition query = queryGenerator.generate(params);
		query.setWriterConfig(getDefaultWriterConfig(params));
		return query;
	}

	public RdfQueryDefinition getPagedQuery(RestParameters params)
			throws MalformedQueryException {
		RdfQueryDefinition query = queryGenerator.generate(params);
		RDFWriterConfig config = getDefaultWriterConfig(params);
		query.setWriterConfig(config);
		config.setMetaData("page", String.valueOf(params.getPage()));
		config.setMetaData("limit", String.valueOf(params.getResultLimit()));
		return query;
	}

	private RDFWriterConfig getDefaultWriterConfig(RestParameters params) {
		RDFWriterConfig config = new RDFWriterConfig();
		config.setPrettyPrint(params.getPretty());
		config.setBaseUri(platformConfig.getString("platform.baseUri"));
		return config;
	}
}
