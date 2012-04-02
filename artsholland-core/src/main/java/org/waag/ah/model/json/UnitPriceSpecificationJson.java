package org.waag.ah.model.json;

import java.math.BigDecimal;

import org.codehaus.jackson.annotate.JsonProperty;

public abstract class UnitPriceSpecificationJson extends AHRDFObjectJson {

	@JsonProperty
	public abstract String getHasCurrency();
	
	@JsonProperty
	public abstract BigDecimal getHasMinCurrencyValue();
	
	@JsonProperty
	public abstract BigDecimal getHasMaxCurrencyValue();
	
}
