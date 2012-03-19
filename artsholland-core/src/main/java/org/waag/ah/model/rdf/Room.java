package org.waag.ah.model.rdf;

import org.openrdf.annotations.Iri;

@Iri(AHRDFObject.ah + "Room")
public abstract class Room extends AHRDFObject  {
			
	@Iri(rdfs + "label")
	public abstract String getLabel();	
	
}
