package org.waag.ah.model.rdf;

import java.util.Set;

import org.openrdf.annotations.Iri;
import org.openrdf.repository.object.LangString;

@Iri(AHRDFObject.ah + "Production")
public abstract class Production extends AHRDFObject {
	
	@Iri(dc + "title")
	public abstract Set<LangString> getTitles();
	
	@Iri(dc + "description")
	public abstract Set<LangString> getDescriptions();
	
	@Iri(ah + "shortDescription")
	public abstract Set<LangString> getShortDescriptions();
	
	@Iri(ah + "party")
	public abstract Set<LangString> getParties();
	
	@Iri(ah + "people")
	public abstract Set<LangString> getPeoples();
	
	@Iri(ah + "cidn")
	public abstract String getCidn();
	
	@Iri(foaf + "homepage")
	public abstract String getHomepage();	
	
	@Iri(ah + "genre")
	public abstract Genre getGenre();	
	
	@Iri(ah + "productionType")
	public abstract ProductionType getProductionType();	
	
	@Iri(ah + "tag")
	public abstract Set<String> getTags();
			
}
