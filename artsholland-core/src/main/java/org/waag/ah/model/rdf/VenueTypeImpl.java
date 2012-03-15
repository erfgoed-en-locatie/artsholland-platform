package org.waag.ah.model.rdf;

import org.openrdf.annotations.Iri;

@Iri(AHRDFObjectImpl.ah + "VenueType")
public class VenueTypeImpl extends AHRDFObjectImpl implements VenueType {
	
	@Override
	@Iri(rdfs + "label")
	public String getLabel() {
		return null;
	}
	
}
