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
	private Set<Production> productions;
	
	@Iri(ah + "venue")
	private Set<Venue> venues;
	
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
	public Set<Venue> getVenues() {
		return venues;
	}
	
	public String getVenueJson() {
		return "Not yet implemented";
	}
	
	@Override
	public Set<Room> getRooms() {
		return rooms;
	}

	
	@Override
	public Set<Production> getProductions() {
		return productions;
	}
	
	@Override
	public String getProductionJson() {
		return "Not yet implemented";
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
