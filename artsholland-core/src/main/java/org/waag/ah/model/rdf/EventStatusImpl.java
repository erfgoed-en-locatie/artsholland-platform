package org.waag.ah.model.rdf;

import org.openrdf.annotations.Iri;

@Iri(AHRDFObjectImpl.ah + "EventStatus")
public class EventStatusImpl extends AHRDFObjectImpl implements EventStatus {
	
	@Override
	@Iri(rdfs + "label")
	public String getLabel() {
		return null;
	}
	
}
