package org.waag.ah.model.rdf;
	
import org.openrdf.annotations.Iri;

@Iri(RDFObject.ah + "Event")
public abstract class EventImpl extends RDFObject implements Event {
	
	/*@Iri(ah + "venue")
	private Venue venue;*/
	
	@Iri(ah + "production")
	private ProductionImpl production;
		
	@Iri(time + "hasBeginning")
	private String hasBeginning;
	
	@Override
	public ProductionImpl getProduction() {
		return production;
	}
	
	/*@Override
	public Venue getVenue() {
		return venue;
	}*/
	
	@Override
	public String getHasBeginning() {
		return hasBeginning;
	}

	public String getResourceString() {
		
		return getResource().toString();
		
	}

}
