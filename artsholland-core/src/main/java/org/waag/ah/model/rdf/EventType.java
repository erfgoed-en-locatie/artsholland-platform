package org.waag.ah.model.rdf;

import org.openrdf.annotations.Iri;

@Iri(AHRDFObject.ah + "EventType")
public abstract class EventType extends AHRDFObject {
	
	@Iri(rdfs + "label")
	public abstract String getLabel();
	
}
