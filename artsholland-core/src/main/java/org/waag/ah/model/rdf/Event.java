package org.waag.ah.model.rdf;
	
import java.util.Set;

import javax.xml.datatype.XMLGregorianCalendar;

import org.openrdf.annotations.Iri;

@Iri(AHRDFObject.ah + "Event")
public abstract class Event extends AHRDFObject {
	
	@Iri(ah + "cidn")
	public abstract String getCidn();
	
	@Iri(ah + "ticket")
	public abstract Set<Offering> getTickets();
	
	@Iri(ah + "production")
	public abstract Production getProduction();
	
	@Iri(ah + "venue")
	public abstract Venue getVenue();
	
	@Iri(ah + "room")
	public abstract Set<Room> getRooms();
		
	@Iri(time + "hasBeginning")
	public abstract XMLGregorianCalendar getHasBeginning();
	
	@Iri(time + "hasEnd")
	public abstract XMLGregorianCalendar getHasEnd();
	
	@Iri(ah + "eventType")
	public abstract EventType getEventType();
	
	@Iri(ah + "eventStatus")
	public abstract EventStatus getEventStatus();

}
