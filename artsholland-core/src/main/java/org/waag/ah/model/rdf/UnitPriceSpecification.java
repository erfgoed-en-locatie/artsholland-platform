package org.waag.ah.model.rdf;

import java.math.BigDecimal;

import org.openrdf.annotations.Iri;

@Iri(AHRDFObject.gr + "UnitPriceSpecification")
public abstract class UnitPriceSpecification extends AHRDFObject {

	@Iri(gr + "hasCurrency")
	public abstract String getHasCurrency();
	
	@Iri(gr + "hasMinCurrencyValue")
	public abstract BigDecimal getHasMinCurrencyValue();
	
	@Iri(gr + "hasMaxCurrencyValue")
	public abstract BigDecimal getHasMaxCurrencyValue();
	
	
}
