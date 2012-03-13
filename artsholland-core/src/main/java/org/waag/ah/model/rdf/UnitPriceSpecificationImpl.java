package org.waag.ah.model.rdf;

import java.math.BigDecimal;

import org.openrdf.annotations.Iri;

@Iri(AHRDFObjectImpl.gr + "UnitPriceSpecification")
public class UnitPriceSpecificationImpl extends AHRDFObjectImpl implements UnitPriceSpecification {
			
	@Iri(gr + "hasCurrency")
	private String hasCurrency;
	
	@Iri(gr + "hasMinCurrencyValue")
	public BigDecimal hasMinCurrencyValue;
	
	@Iri(gr + "hasMaxCurrencyValue")
	public BigDecimal hasMaxCurrencyValue;
	
	@Override
	public String getHasCurrency() {
		return hasCurrency;
	}
	
	@Override
	public BigDecimal getHasMinCurrencyValue() {
		return hasMinCurrencyValue;
	}
	
	@Override
	public BigDecimal getHasMaxCurrencyValue() {
		return hasMaxCurrencyValue;
	}
	
	
}
