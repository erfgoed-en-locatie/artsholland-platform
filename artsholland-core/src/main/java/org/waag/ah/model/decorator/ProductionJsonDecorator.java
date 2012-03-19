package org.waag.ah.model.decorator;

import org.openrdf.annotations.Iri;
import org.waag.ah.model.rdf.AHRDFObject;
import org.waag.ah.model.rdf.Production;

@Iri(AHRDFObject.ah + "Production")
public abstract class ProductionJsonDecorator extends Production {
	
	public String getTitle() {
		return getLangString(getTitles());
	}
	
	public String getDescription() {
		return getLangString(getDescriptions());
	}
	
	public  String getShortDescription() {
		return getLangString(getShortDescriptions());
	}

	public  String getParty() {
		return getLangString(getParties());
	}
	
	public  String getPeople() {
		return getLangString(getPeoples());
	}

}
