package org.waag.ah.model.rdf;

import java.util.Set;

import org.openrdf.annotations.Bind;
import org.openrdf.annotations.Iri;
import org.openrdf.annotations.Sparql;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.repository.object.LangString;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.RDFObject;

public abstract class AHRDFObject implements RDFObject {
	 
	public static final String rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public static final String rdfs = "http://www.w3.org/2000/01/rdf-schema#";
	public static final String owl = "http://www.w3.org/2002/07/owl#";
	public static final String dc = "http://purl.org/dc/elements/1.1/";
	public static final String foaf = "http://xmlns.com/foaf/0.1/";
	public static final String xsd = "http://www.w3.org/2001/XMLSchema#";
	public static final String time = "http://www.w3.org/2006/time#";
	public static final String gr = "http://purl.org/goodrelations/v1#";
	public static final String geo = "http://www.w3.org/2003/01/geo/wgs84_pos#";
	public static final String vcard = "http://www.w3.org/2006/vcard/ns#";

	public static final String nub = "http://resources.uitburo.nl/";
	public static final String ah = "http://data.artsholland.com/";
	
	String lang = null;

	@Override
	public abstract ObjectConnection getObjectConnection();

	@Override
	public abstract Resource getResource();
	
	@Iri(rdfs + "label")
	public abstract String getLabel();
	
	/*
	 * TODO: Paging not possible using this function (or is it?)... 
	 */
  @Sparql("SELECT DISTINCT ?instance WHERE { { ?instance ?p $this. } UNION { $this ?p ?instance. } ?instance a ?class. }")
  //@Sparql("SELECT DISTINCT ?instance WHERE { ?instance ?p $this. ?instance a ?class. }")
  public abstract Set<AHRDFObject> getLinkedObjects(@Bind("class") URI classURI);  
  
	protected String getLangString(Set<LangString> langStrings) {
		
		if (lang == null) {
			ObjectConnection conn = getObjectConnection();
			if (conn != null) {
				lang = conn.getLanguage();
			}
		}
		
		for (LangString langString: langStrings) {
			if (lang.equals(langString.getLang())) {
				return langString.toString();
			}
		}
		return null;		
	}
	
	public String getURI() {
		return getResource().toString();
	}
	
	protected String getURI(AHRDFObject rdfObject) {
		if (rdfObject != null) {
			return rdfObject.getURI();
		}
		return null;		
	}
	
}
