package org.waag.ah.model.rdf;

import org.openrdf.annotations.Iri;

@Iri(AHRDFObject.ah + "ProductionType")
public abstract class ProductionType extends AHRDFObject  {
	
	@Iri(rdfs + "label")
	public abstract String getLabel();
	
}
