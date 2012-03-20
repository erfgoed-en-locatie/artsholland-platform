package org.waag.ah.model.json;

import java.util.Set;

import javax.xml.datatype.XMLGregorianCalendar;

import org.codehaus.jackson.annotate.JsonProperty;
import org.waag.ah.model.rdf.EventStatus;
import org.waag.ah.model.rdf.EventType;

public abstract class EventJson extends AHRDFObjectJson  {
		
	/*
	ah:cidn [http]	"00bd62aa-c90d-4b72-ad74-08f87a4248c9"^^<xsd:string>	-
	ah:eventStatus [http]	ah:EventStatusNormal [http]	-
	ah:eventType [http]	ah:EventType [http]	-
	ah:production [http]	ah:productions/4fc3decf7aca3c54b08e1f5f3ba63e04 [http]	-
	ah:room [http]	ah:venues/d275aaf5ba0fea3f847b7b64fbcf4cdd/rooms/kantine [http]	-
	ah:ticket [http]	ah:events/00bd62aa-c90d-4b72-ad74-08f87a4248c9/tickets/jeugd [http]	-
	ah:ticket [http]	ah:events/00bd62aa-c90d-4b72-ad74-08f87a4248c9/tickets/normaal [http]	-
	ah:venue [http]	ah:venues/d275aaf5ba0fea3f847b7b64fbcf4cdd [http]	-
	dc:created [http]	"2012-03-05T09:17:02Z"^^<xsd:string>	-
	
	owl:sameAs [http]	nub:events/00bd62aa-c90d-4b72-ad74-08f87a4248c9 [http]	-
	time:hasBeginning [http]	"2012-04-02T08:00:00Z"	-
	time:hasEnd [http]	"2012-04-02T10:00:00Z"
	*/
	
	@JsonProperty
	public abstract String getURI();
	
	@JsonProperty
	public abstract String getCidn();
	
	@JsonProperty
	public abstract Set<OfferingJson> getTickets();
		
	@JsonProperty("production")
	public abstract String getProductionURI();
		
	@JsonProperty("venue")
	public abstract String getVenueURI();
	
	@JsonProperty
	public abstract Set<RoomJson> getRooms();
		
	@JsonProperty
	public abstract XMLGregorianCalendar getHasBeginning();
	
	@JsonProperty
	public abstract XMLGregorianCalendar getHasEnd();

	@JsonProperty
	public abstract EventType getEventType();
	
	@JsonProperty
	public abstract EventStatus getEventStatus();

}
