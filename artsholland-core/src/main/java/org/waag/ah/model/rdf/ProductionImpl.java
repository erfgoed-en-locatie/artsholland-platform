package org.waag.ah.model.rdf;

import java.util.Set;

import org.openrdf.annotations.Iri;
import org.openrdf.annotations.Sparql;
import org.openrdf.repository.object.LangString;

@Iri(AHRDFObjectImpl.ah + "Production")
public class ProductionImpl extends AHRDFObjectImpl implements Production {
	
	@Iri(dc + "title")
  private Set<LangString> titles;
	
	@Iri(dc + "description")
  private Set<LangString> descriptions;
	
	@Iri(ah + "cidn")
	private String cidn;
	
	@Iri(foaf + "homepage")
	private String homepage;	
	
	public String getTitle() {
		return getLangString(titles);
	}
	
	public String getDescription() {
		return getLangString(descriptions);
	}
	
	@Override
	public String getCidn() {
		return cidn;
	}

	@Override
	public String getHomepage() {
		return homepage;
	}
	
	@Override
  @Sparql("PREFIX ah: <http://data.artsholland.com/> " + "SELECT ?event { ?event ah:production $this }")
  public Set<Event> getEvents() {
  	return null;
  }
		
}
