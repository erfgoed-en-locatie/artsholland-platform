package org.waag.ah.model.rdf;
	
import java.util.Set;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.openrdf.annotations.Iri;

@Iri(RDFObject.ah + "Event")
@JsonAutoDetect(fieldVisibility=Visibility.NONE, getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE, creatorVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public abstract class EventImpl extends RDFObject implements Event {
	
	@Iri(ah + "venue")
	private Set<Venue> venues;
	
	@Iri(ah + "cidn")
	private String cidn;
	
	@Iri(ah + "production")
	private Set<Production> productions;
		
	@Iri(time + "hasBeginning")
	private String hasBeginning;
	
	@Override
	public Set<Production> getProductions() {
		return productions;
	}
	
	@Override
	public String getCidn() {
		return cidn;
	}
	
	@Override
	public Set<Venue> getVenues() {
		return venues;
	}
	
	@Override
	public String getHasBeginning() {
		return hasBeginning;
	}


}
