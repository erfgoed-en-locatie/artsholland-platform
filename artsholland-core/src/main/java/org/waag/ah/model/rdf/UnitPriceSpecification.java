package org.waag.ah.model.rdf;

import java.math.BigDecimal;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.openrdf.repository.object.RDFObject;

@JsonAutoDetect(fieldVisibility=Visibility.NONE, getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE, creatorVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
@JsonSerialize(include=Inclusion.NON_EMPTY)
public interface UnitPriceSpecification extends RDFObject {

	@JsonProperty
	public abstract String getHasCurrency();
	
	@JsonProperty
	public abstract BigDecimal getHasMinCurrencyValue();
	
	@JsonProperty
	public abstract BigDecimal getHasMaxCurrencyValue();
	
}
