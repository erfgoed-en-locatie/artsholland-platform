package org.waag.ah.model.rdf;

import org.openrdf.annotations.Iri;

@Iri(AHRDFObject.ah + "EventStatus")
public abstract class EventStatus extends AHRDFObject {
	
	@Iri(rdfs + "label")
	public abstract String getLabel();
	
}
