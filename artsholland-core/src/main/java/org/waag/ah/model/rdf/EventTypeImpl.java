package org.waag.ah.model.rdf;

import org.openrdf.annotations.Iri;

@Iri(AHRDFObjectImpl.ah + "EventType")
public class EventTypeImpl extends AHRDFObjectImpl implements EventType {
	
	@Override
	@Iri(rdfs + "label")
	public String getLabel() {
		return null;
	}
	
}
