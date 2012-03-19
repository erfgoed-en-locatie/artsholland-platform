package org.waag.ah.model.rdf;

import org.openrdf.annotations.Iri;

@Iri(AHRDFObject.ah + "Genre")
public abstract class Genre extends AHRDFObject {
	
	@Iri(rdfs + "label")
	public abstract String getLabel();
	
}
