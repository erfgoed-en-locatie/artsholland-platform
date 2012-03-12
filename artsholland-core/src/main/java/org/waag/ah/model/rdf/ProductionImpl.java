package org.waag.ah.model.rdf;

import java.util.Set;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.openrdf.annotations.Iri;
import org.openrdf.repository.object.LangString;

@Iri(RDFObject.ah + "Production")
@JsonAutoDetect(fieldVisibility=Visibility.NONE, getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE, creatorVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public abstract class ProductionImpl extends RDFObject implements Production {
		
	@Iri(dc + "title")
  Set<LangString> titles;	
	
	@Iri(ah + "cidn")
	private String cidn;
	
	public String getTitle() {
		for (LangString title: titles) {
			return title.toString();
		}
		return "";
	}
	
	@Override
	public String getCidn() {
		return cidn;
	}
		
}
