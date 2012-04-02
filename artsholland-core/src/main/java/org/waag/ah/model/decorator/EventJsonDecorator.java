package org.waag.ah.model.decorator;

import org.openrdf.annotations.Iri;
import org.waag.ah.model.rdf.AHRDFObject;
import org.waag.ah.model.rdf.Event;

@Iri(AHRDFObject.ah + "Event")
public abstract class EventJsonDecorator extends Event {
	
	public String getProductionURI() {
		return getProduction().getURI();
	}
	
	public String getVenueURI() {
		return getVenue().getURI();
	}

}
