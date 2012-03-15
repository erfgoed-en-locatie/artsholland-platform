package org.waag.ah.model.rdf;
	
import java.util.Set;

import javax.xml.datatype.XMLGregorianCalendar;

import org.openrdf.annotations.Iri;

@Iri(AHRDFObjectImpl.ah + "Event")
public class EventImpl extends AHRDFObjectImpl implements Event {
	
	@Iri(ah + "cidn")
	private String cidn;
	
	@Iri(ah + "ticket")
	private Set<Offering> tickets;
	
	@Iri(ah + "production")
	private Production production;
	
	@Iri(ah + "venue")
	private Venue venue;
	
	@Iri(ah + "room")
	private Set<Room> rooms;
		
	@Iri(time + "hasBeginning")
	public XMLGregorianCalendar hasBeginning;
	
	@Iri(time + "hasEnd")
	public XMLGregorianCalendar hasEnd;
	
	@Override
	public String getCidn() {
		return cidn;
	}
	
	@Override
	public Venue getVenue() {
		return venue;
	}
	
	@Override
	public String getVenueURI() {
		return getURI(getVenue());
	}
	
	@Override
	public Set<Room> getRooms() {
		return rooms;
	}
	
	@Override
	public Production getProduction() {
		return production;
	}
	
	@Override
	public String getProductionURI() {
		return getURI(getProduction());
	}
	
	@Override
	public Set<Offering> getTickets() {
		return tickets;
	}

	@Override
	public String getHasBeginning() {
		if (hasBeginning instanceof XMLGregorianCalendar) {
			return hasBeginning.toString();			
		}
		return null;
	}

	@Override
	public String getHasEnd() {
		if (hasEnd instanceof XMLGregorianCalendar) {
			return hasEnd.toString();
		}
		return null;
	}

}
