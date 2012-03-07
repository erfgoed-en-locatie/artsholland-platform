package org.waag.ah.model.rdf;

import org.openrdf.annotations.Iri;

@Iri(RDFObject.ah + "Production")
public abstract class ProductionImpl extends RDFObject implements Production {
		
	@Iri(dc + "title")
  String title;	
	
	public String getTitle() {
		return title;
	}
		
}
