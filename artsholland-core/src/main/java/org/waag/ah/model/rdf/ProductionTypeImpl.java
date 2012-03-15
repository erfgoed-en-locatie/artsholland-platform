package org.waag.ah.model.rdf;

import org.openrdf.annotations.Iri;

@Iri(AHRDFObjectImpl.ah + "ProductionType")
public class ProductionTypeImpl extends AHRDFObjectImpl implements ProductionType {
	
	@Override
	@Iri(rdfs + "label")
	public String getLabel() {
		return null;
	}
	
}
