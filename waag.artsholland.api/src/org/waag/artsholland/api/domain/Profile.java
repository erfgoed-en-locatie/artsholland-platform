package org.waag.artsholland.api.domain;

import java.net.URISyntaxException;
import java.net.URL;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;

import com.clarkparsia.empire.SupportsRdfId;
import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfsClass;

@Entity
@Namespaces({"foaf", "http://xmlns.com/foaf/0.1/"})
@RdfsClass("foaf:Person")
@XmlRootElement(name="profile")
public class Profile implements SupportsRdfId, org.waag.artsholland.model.Profile {
	private String name;
	private String email;
	private URL url;

	@Override
	public URL getUrl() {
		return this.url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public RdfKey getRdfId() {
		try {
			return new URIKey(getUrl().toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void setRdfId(RdfKey theId) {
		
	}
}
