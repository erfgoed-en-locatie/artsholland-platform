package org.waag.ah.model.rdf;

import java.math.BigDecimal;

import org.codehaus.jackson.annotate.JsonProperty;

public interface UnitPriceSpecification extends AHRDFObject {

	@JsonProperty
	public String getHasCurrency();
	
	@JsonProperty
	public BigDecimal getHasMinCurrencyValue();
	
	@JsonProperty
	public BigDecimal getHasMaxCurrencyValue();
	
}
