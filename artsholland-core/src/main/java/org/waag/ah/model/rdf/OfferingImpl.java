package org.waag.ah.model.rdf;

import org.openrdf.annotations.Iri;

@Iri(AHRDFObjectImpl.gr + "Offering")
public class OfferingImpl extends AHRDFObjectImpl implements Offering {
		
	@Iri(gr + "name")
	private String name;
	
	@Iri(dc + "description")
	private String description;
	
	@Iri(gr + "hasPriceSpecification")
	private UnitPriceSpecification unitPriceSpecification;
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String getDescription() {
		return description;
	}
	
	@Override
	public UnitPriceSpecification getUnitPriceSpecification() {
		return unitPriceSpecification;
	}	
	
}
