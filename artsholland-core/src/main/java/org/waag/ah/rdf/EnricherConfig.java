package org.waag.ah.rdf;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

public class EnricherConfig {
	private Class<? extends GraphEnricher> enricherClass;
	private URI subjectUri;
	private Set<URI> includeUris = new HashSet<URI>();
	private Set<URI> excludeUris = new HashSet<URI>();
	private ValueFactory vf;
	
	public EnricherConfig() {
		this.vf = ValueFactoryImpl.getInstance();
	}
	
	public Class<? extends GraphEnricher> getEnricher() {
		return this.enricherClass;
	}
	
	public void setEnricher(Class<? extends GraphEnricher> clazz) {
		this.enricherClass = clazz;
	}
	
	public ValueFactory getValueFactory() {
		return this.vf;
	}
	
	public URI getObjectUri() {
		return this.subjectUri;
	}
	
	public void setObjectUri(URI uri) {
		this.subjectUri = uri;
	}
	
	public void setObjectUri(String uri) {
		this.setObjectUri(vf.createURI(uri));
	}
	
	public final Set<URI> getIncludeUris() {
		return this.includeUris;
	}
	
	private void addIncludeUri(URI uri) {
		this.includeUris.add(uri);
	}

	public void addIncludeUri(List<String> uris) {
		for (String uri : uris) {
			this.addIncludeUri(uri);
		}
	}
	
	public void addIncludeUri(String... uris) {
		for (String uri : uris) {
			this.addIncludeUri(vf.createURI(uri));
		}
	}
	
	public final Set<URI> getExcludeUris() {
		return this.excludeUris;
	}
	
	private void addExcludeUri(URI uri) {
		this.excludeUris.add(uri);
	}

	public void addExcludeUri(List<String> uris) {
		for (String uri : uris) {
			this.addExcludeUri(uri);
		}
	}
	
	public void addExcludeUri(String... uris) {
		for (String uri : uris) {
			this.addExcludeUri(vf.createURI(uri));
		}
	}
	
//	public boolean removePropertyUri(URI uri) {
//		return this.includeUris.remove(uri);
//	}
	
//	public boolean removePropertyUri(String uri) {
//		return this.removePropertyUri(vf.createURI(uri));
//	}
}
