package org.waag.ah.model.rdf;

import org.openrdf.annotations.Iri;

@Iri(AHRDFObject.ah + "AttachmentType")
public abstract class AttachmentType extends AHRDFObject {
	
	@Iri(rdfs + "label")
	public abstract String getLabel();
	
}
