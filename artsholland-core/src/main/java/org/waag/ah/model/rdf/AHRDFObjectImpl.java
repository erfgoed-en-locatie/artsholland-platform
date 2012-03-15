package org.waag.ah.model.rdf;

import java.util.Set;

import org.openrdf.model.Resource;
import org.openrdf.repository.object.LangString;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.RDFObject;

public class AHRDFObjectImpl implements RDFObject {
	 
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
	
	public String getURI() {
		return getResource().toString();
	}

	@Override
	public ObjectConnection getObjectConnection() {
		return null;
	}

	@Override
	public Resource getResource() {
		return null;
	}
	
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
	
	protected String getLangString(LangString langString) {
		if (langString != null) {
			return langString.toString();
		}
		return null;		
	}
	
	protected String getURI(AHRDFObject rdfObject) {
		if (rdfObject != null) {
			return rdfObject.getURI();
		}
		return null;		
	}
	
}
