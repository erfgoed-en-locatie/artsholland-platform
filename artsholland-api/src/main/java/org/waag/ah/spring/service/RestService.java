package org.waag.ah.spring.service;

import java.io.IOException;
import java.util.concurrent.FutureTask;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.repository.RepositoryConnection;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.waag.ah.PlatformConfig;
import org.waag.ah.RepositoryConnectionFactory;
import org.waag.ah.bigdata.BigdataQueryService;
import org.waag.ah.bigdata.BigdataQueryService.QueryTask;
import org.waag.ah.rest.RESTParameters;
import org.waag.ah.rest.model.RestRelation;
import org.waag.ah.rest.model.SPARQLFilter;
import org.waag.ah.rest.model.RestRelation.RelationQuantity;
import org.waag.ah.rest.model.RestRelation.RelationType;

@Service("restService")
public class RestService implements InitializingBean, DisposableBean {
	//private static final Logger logger = LoggerFactory.getLogger(RestService.class);
	
	@EJB(mappedName="java:app/datastore/BigdataQueryService")
	private BigdataQueryService context;

	@EJB(mappedName="java:app/datastore/BigdataConnectionService")
	private RepositoryConnectionFactory connFactory;
	
//	@EJB(mappedName = "java:app/datastore/ObjectConnectionService")
//	private ObjectConnectionFactory connFactory;

	private PropertiesConfiguration config;
	private RepositoryConnection conn;
	
	RestRelation rootRelation;
	RestRelationQueryTaskGenerator queryTaskGenerator;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		config = PlatformConfig.getConfig(); 
		conn = connFactory.getConnection();
		
		rootRelation = new RestRelation();
  	
  	RestRelation eventsRelation = rootRelation.addRelation("events", "Event", RelationQuantity.MULTIPLE, RelationType.SELF, false);
  	RestRelation venuesRelation = rootRelation.addRelation("venues", "Venue", RelationQuantity.MULTIPLE, RelationType.SELF, false);
  	RestRelation productionsRelation = rootRelation.addRelation("productions", "Production", RelationQuantity.MULTIPLE, RelationType.SELF, false);
  	
  	// TODO: ?this instead of ?object  ???
  	SPARQLFilter venuesLocalityFilter = new SPARQLFilter("locality", "?object vcard:locality ?locality.", "?locality = \"?parameter\"");
  	venuesRelation.addFilter(venuesLocalityFilter);

  	SPARQLFilter eventsLocalityFilter = new SPARQLFilter("locality", "?object <http://purl.org/artsholland/1.0/venue> ?venue . ?venue vcard:locality ?locality .", "?locality = \"?parameter\"");
  	SPARQLFilter eventsBeforeFilter = new SPARQLFilter("before", "?object time:hasBeginning ?hasBeginning.", "?hasBeginning < \"?parameter\"^^xsd:dateTime");
  	SPARQLFilter eventsAfterFilter = new SPARQLFilter("after", "?object time:hasBeginning ?hasBeginning.", "?hasBeginning > \"?parameter\"^^xsd:dateTime");
  	eventsRelation.addFilter(eventsLocalityFilter);
  	eventsRelation.addFilter(eventsBeforeFilter);
  	eventsRelation.addFilter(eventsAfterFilter);
  	//?time < "2012-06-02T17:00:00Z"^^xsd:dateTime
  	
  	RestRelation eventRelation = eventsRelation.addRelation("cidn", "Event", RelationQuantity.SINGLE, RelationType.SELF, true);
  	RestRelation venueRelation = venuesRelation.addRelation("cidn", "Venue", RelationQuantity.SINGLE, RelationType.SELF, true);
  	RestRelation productionRelation = productionsRelation.addRelation("cidn", "Production", RelationQuantity.SINGLE, RelationType.SELF, true);
  	
  	eventRelation.addRelation("productions", "Production", RelationQuantity.MULTIPLE, RelationType.FORWARD, false);
  	eventRelation.addRelation("venues", "Venue", RelationQuantity.MULTIPLE, RelationType.FORWARD, false);
  	eventRelation.addRelation("rooms", "Room", RelationQuantity.MULTIPLE, RelationType.FORWARD, false);
  	
  	venueRelation.addRelation("events", "Event", RelationQuantity.MULTIPLE, RelationType.BACKWARD, false);
  	venueRelation.addRelation("productions", "Production", RelationQuantity.MULTIPLE, RelationType.BACKWARDFORWARD, false);
  	venueRelation.addRelation("rooms", "Room", RelationQuantity.MULTIPLE, RelationType.FORWARD, false);
  	
  	productionRelation.addRelation("events", "Event", RelationQuantity.MULTIPLE, RelationType.BACKWARD, false);
  	productionRelation.addRelation("venues", "Venue", RelationQuantity.MULTIPLE, RelationType.BACKWARDFORWARD, false);
  	
  	RestRelation venueAttachmentRelation = venueRelation.addRelation("attachments", "Attachment", RelationQuantity.MULTIPLE, RelationType.FORWARD, false);
  	venueAttachmentRelation.addRelation("id", "Attachment", RelationQuantity.SINGLE, RelationType.SELF, true); 	
		
  	RestRelation eventAttachmentRelation = eventRelation.addRelation("attachments", "Attachment", RelationQuantity.MULTIPLE, RelationType.FORWARD, false);
  	eventAttachmentRelation.addRelation("id", "Attachment", RelationQuantity.SINGLE, RelationType.SELF, true); 	
		
  	
  	queryTaskGenerator = new RestRelationQueryTaskGenerator(context, conn, config.getString("platform.baseUri"), rootRelation);
	}

	@Override
	public void destroy() throws Exception {
		conn.close();
	}	
	
	public void restRequest(HttpServletRequest request,
			HttpServletResponse response, RESTParameters params) throws IOException {
	
		try {			
			QueryTask queryTask = queryTaskGenerator.generate(response.getOutputStream(), params);			
			if (queryTask != null) {
				final FutureTask<Void> ft = new FutureTask<Void>(queryTask);
				
				response.setStatus(HttpServletResponse.SC_OK);
				context.executeQueryTask(ft);
				ft.get();	
			} else {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			}			
		} catch (MalformedQueryException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,
				e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
				e.getMessage());
		}
	}		

//	public Set<?> getEvents(XMLGregorianCalendar dateTimeFrom,
//			XMLGregorianCalendar dateTimeTo) throws MalformedQueryException, RepositoryException, QueryEvaluationException {
//		
//		ObjectQuery query = conn.prepareObjectQuery(QueryLanguage.SPARQL, 
//				"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" + 
//				"PREFIX time: <http://www.w3.org/2006/time#>\n" + 
//				"SELECT DISTINCT ?instance WHERE { " +
//				"	?instance time:hasBeginning ?datePub" +
//				"	FILTER(?datePub >= ?dtFrom && ?datePub < ?dtTo)." +
//				"} ORDER BY DESC(?datePub) LIMIT 10"
//			);
//		
////			query.setBinding("dtFrom", conn.getValueFactory().createLiteral(
////					XMLDatatypeUtil.parseCalendar("2009-01-01T17:00:00Z")));
////			query.setBinding("dtTo", conn.getValueFactory().createLiteral(
////					XMLDatatypeUtil.parseCalendar("2014-02-01T17:00:00Z")));
//		
//		query.setBinding("dtFrom", conn.getValueFactory().createLiteral(dateTimeFrom));
//		query.setBinding("dtTo", conn.getValueFactory().createLiteral(dateTimeTo));
//	}

}
