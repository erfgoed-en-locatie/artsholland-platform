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
import org.waag.ah.rest.RESTParameters;
import org.waag.ah.rest.model.RestRelation;
import org.waag.ah.rest.model.RestRelation.RelationQuantity;
import org.waag.ah.rest.model.RestRelation.RelationType;
import org.waag.ah.rest.util.RestRelationQueryTaskGenerator;

@Service("restService")
public class RestService implements InitializingBean {
//	private static final Logger logger = LoggerFactory
//			.getLogger(RestService.class);

	RestRelation rootRelation;
	RestRelationQueryTaskGenerator queryTaskGenerator;
	
	@Autowired
	PropertiesConfiguration platformConfig;

	@Override
	public void afterPropertiesSet() throws Exception {
		RDFFormat.register(RDFJSONFormat.RESTAPIJSON);

		rootRelation = new RestRelation();

		RestRelation eventsRelation = rootRelation.addRelation("events",
				"Event", RelationQuantity.MULTIPLE, RelationType.SELF, false);
		RestRelation venuesRelation = rootRelation.addRelation("venues",
				"Venue", RelationQuantity.MULTIPLE, RelationType.SELF, false);
		RestRelation productionsRelation = rootRelation.addRelation(
				"productions", "Production", RelationQuantity.MULTIPLE,
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
		eventRelation.addRelation("rooms", "Room", RelationQuantity.MULTIPLE,
				RelationType.FORWARD, false);

		venueRelation.addRelation("events", "Event", RelationQuantity.MULTIPLE,
				RelationType.BACKWARD, false);
		venueRelation.addRelation("productions", "Production",
				RelationQuantity.MULTIPLE, RelationType.BACKWARDFORWARD, false);
		venueRelation.addRelation("rooms", "Room", RelationQuantity.MULTIPLE,
				RelationType.FORWARD, false);

		productionRelation.addRelation("events", "Event",
				RelationQuantity.MULTIPLE, RelationType.BACKWARD, false);
		productionRelation.addRelation("venues", "Venue",
				RelationQuantity.MULTIPLE, RelationType.BACKWARDFORWARD, false);

		RestRelation venueAttachmentRelation = venueRelation.addRelation(
				"attachments", "Attachment", RelationQuantity.MULTIPLE,
				RelationType.FORWARD, false);
		venueAttachmentRelation.addRelation("id", "Attachment",
				RelationQuantity.SINGLE, RelationType.SELF, true);

		queryTaskGenerator = new RestRelationQueryTaskGenerator(rootRelation);
	}

	public RdfQueryDefinition getObjectQuery(RESTParameters params)
			throws MalformedQueryException {
		RdfQueryDefinition query = queryTaskGenerator.generate(params);
		query.setWriterConfig(getDefaultWriterConfig(params));
		return query;
	}

	public RdfQueryDefinition getPagedQuery(RESTParameters params)
			throws MalformedQueryException {
		RdfQueryDefinition query = queryTaskGenerator.generate(params);
		if (query == null) {
			throw new MalformedQueryException();
		}
		RDFWriterConfig config = getDefaultWriterConfig(params);
		query.setWriterConfig(config);
		config.setMetaData("page", String.valueOf(params.getPage()));
		config.setMetaData("limit", String.valueOf(params.getResultLimit()));
		return query;
	}
	
	private RDFWriterConfig getDefaultWriterConfig(RESTParameters params) {
		RDFWriterConfig config = new RDFWriterConfig();
		config.setPrettyPrint(params.getPretty());
		config.setBaseUri(platformConfig.getString("platform.baseUri"));		
		return config;
	}
}
