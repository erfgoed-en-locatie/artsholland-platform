package org.waag.ah.model.rdf;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonAutoDetect(fieldVisibility=Visibility.NONE, getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE, creatorVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public interface Event extends org.openrdf.repository.object.RDFObject {
	
	
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
	public Production getProduction();

	
	/*@JsonProperty
	public Venue getVenue();*/
	
	@JsonProperty
	public String getHasBeginning();
	


}
