package org.waag.ah.model.rdf;

import org.openrdf.annotations.Iri;

@Iri(AHRDFObject.gr + "Offering")
public abstract class Offering extends AHRDFObject {
		
	@Iri(gr + "name")
	public abstract String getName();
	
	@Iri(dc + "description")
	public abstract String getDescription();
	
	@Iri(gr + "hasPriceSpecification")
	public abstract UnitPriceSpecification getUnitPriceSpecification();
	
}
