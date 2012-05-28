package org.waag.ah.rdf;

import java.util.HashSet;
import java.util.Set;

import org.openrdf.model.URI;
import org.openrdf.model.impl.ValueFactoryImpl;

public class EnricherConfig {
	private Class<? extends GraphEnricher> enricherClass;
	private URI subjectUri;
	private Set<URI> propertyUris = new HashSet<URI>();
	
	private ValueFactoryImpl vf;
	
	public EnricherConfig() {
		this.vf = ValueFactoryImpl.getInstance();
	}
	
	public Class<? extends GraphEnricher> getEnricher() {
		return this.enricherClass;
	}
	
	public void setEnricher(Class<? extends GraphEnricher> clazz) {
		this.enricherClass = clazz;
	}
	
	public URI getSubjectUri() {
		return this.subjectUri;
	}
	
	public void setSubjectUri(URI uri) {
		this.subjectUri = uri;
	}
	
	public void setSubjectUri(String uri) {
		this.setSubjectUri(vf.createURI(uri));
	}
	
	public final Set<URI> getPropertyUris() {
		return this.propertyUris;
	}
	
	public void addPropertyUri(URI uri) {
		this.propertyUris.add(uri);
	}
	
	public void addPropertyUri(String... uris) {
		for (String uri : uris) {
			this.addPropertyUri(vf.createURI(uri));
		}
	}

//	public void addPropertyUri(List<String> uris) {
//		for (String uri : uris) {
//			this.addPropertyUri(uri);
//		}
//	}
	
	public boolean removePropertyUri(URI uri) {
		return this.propertyUris.remove(uri);
	}
	
	public boolean removePropertyUri(String uri) {
		return this.removePropertyUri(vf.createURI(uri));
	}
}
