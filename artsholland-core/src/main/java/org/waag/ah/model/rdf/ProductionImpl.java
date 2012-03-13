package org.waag.ah.model.rdf;

import java.util.Set;

import org.openrdf.annotations.Iri;
import org.openrdf.repository.object.LangString;

@Iri(AHRDFObjectImpl.ah + "Production")
public class ProductionImpl extends AHRDFObjectImpl implements Production {
	
	@Iri(dc + "title")
  private Set<LangString> titles;
	
	@Iri(ah + "cidn")
	private String cidn;
	
	@Iri(foaf + "homepage")
	private String homepage;	
	
	public String getTitle() {
		for (LangString title: titles) {
			return title.toString();
		}
		return null;
	}
	
	@Override
	public String getCidn() {
		return cidn;
	}

	@Override
	public String getHomepage() {
		return homepage;
	}
		
}
