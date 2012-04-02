package org.waag.ah.model.rdf;

import org.openrdf.annotations.Iri;
import org.openrdf.model.URI;

@Iri(AHRDFObject.ah + "Attachment")
public abstract class Attachment extends AHRDFObject {

//$muri dc:title {if ($title eq "") then () else $title}^^xsd:string.
//$muri dc:description {if ($description eq "") then () else $description}^^xsd:string.					
//$muri foaf:depiction {$medium/ref}.
	
	@Iri(dc + "title")
	public abstract String getTitle();

	@Iri(dc + "description")
	public abstract String getDescription();

	@Iri(ah + "url")
	public abstract URI getUrl();
	
	@Iri(ah + "attachmentType")
	public abstract AttachmentType getAttachmentType();

}
