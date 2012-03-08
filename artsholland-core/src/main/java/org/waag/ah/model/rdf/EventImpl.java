package org.waag.ah.model.rdf;
	
import java.util.Set;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.openrdf.annotations.Iri;

@Iri(RDFObject.ah + "Event")
@JsonAutoDetect(fieldVisibility=Visibility.NONE, getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE, creatorVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public abstract class EventImpl extends RDFObject implements Event {
	
	@Iri(ah + "cidn")
	private String cidn;
	
	@Iri(ah + "production")
	private Set<Production> productions;
	
	@Iri(ah + "venue")
	private Set<Venue> venues;
		
	@Iri(time + "hasBeginning")
	private String hasBeginning;
	
	@Override
	public String getCidn() {
		return cidn;
	}
	
	@Override
	public Set<Venue> getVenues() {
		return venues;
	}
	
	@Override
	public Set<Production> getProductions() {
		return productions;
	}
	
//	@Override
//	public Venue getVenue() {
//		if (venues.size() > 0) {
//			return venues.iterator().next();
//		}
//		return null;
//	}
//	
//	@Override
//	public Production getProduction() {
//		if (productions.size() > 0) {
//			return productions.iterator().next();
//		}
//		return null;
//	}
	
	@Override
	public String getHasBeginning() {
		return hasBeginning;
	}


}
