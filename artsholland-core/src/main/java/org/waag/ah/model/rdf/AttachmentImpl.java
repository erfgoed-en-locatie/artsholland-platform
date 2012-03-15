package org.waag.ah.model.rdf;

import org.openrdf.annotations.Iri;

@Iri(AHRDFObjectImpl.ah + "Attachment")
public class AttachmentImpl extends AHRDFObjectImpl implements Attachment {

//$muri dc:title {if ($title eq "") then () else $title}^^xsd:string.
//$muri dc:description {if ($description eq "") then () else $description}^^xsd:string.					
//$muri foaf:depiction {$medium/ref}.
	
	@Override
	@Iri(dc + "title")
	public String getTitle() {
		return null;
	}

	@Override
	@Iri(dc + "description")
	public String getDescription() {
		return null;
	}

	@Override
	@Iri(foaf + "depiction")
	public String getDepiction() {
		return null;
	}

}
