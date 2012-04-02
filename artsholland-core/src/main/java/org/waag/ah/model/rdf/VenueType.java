package org.waag.ah.model.rdf;

import org.openrdf.annotations.Iri;

@Iri(AHRDFObject.ah + "VenueType")
public abstract class VenueType extends AHRDFObject {
	
	@Iri(rdfs + "label")
	public abstract String getLabel();
	
}
