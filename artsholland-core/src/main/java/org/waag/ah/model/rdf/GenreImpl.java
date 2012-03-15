package org.waag.ah.model.rdf;

import org.openrdf.annotations.Iri;

@Iri(AHRDFObjectImpl.ah + "Genre")
public class GenreImpl extends AHRDFObjectImpl implements Genre {
	
	@Override
	@Iri(rdfs + "label")
	public String getLabel() {
		return null;
	}
	
}
