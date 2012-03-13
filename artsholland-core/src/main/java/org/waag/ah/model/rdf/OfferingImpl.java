package org.waag.ah.model.rdf;

import java.util.Set;

import org.openrdf.annotations.Iri;

@Iri(AHRDFObjectImpl.gr + "Offering")
public class OfferingImpl extends AHRDFObjectImpl implements Offering {
		
	@Iri(gr + "name")
	private String name;
	
	@Iri(gr + "hasPriceSpecification")
	private Set<UnitPriceSpecification> unitPriceSpecifications;
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public Set<UnitPriceSpecification> getUnitPriceSpecifications() {
		return unitPriceSpecifications;
	}	
	
}
