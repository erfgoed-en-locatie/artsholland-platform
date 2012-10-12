package org.waag.rdf.sesame;

import java.nio.charset.Charset;

import org.openrdf.rio.RDFFormat;

public class RDFJSONFormat extends RDFFormat {
	public static RDFJSONFormat RESTAPIJSON = new RDFJSONFormat();
//    public static final String MIMETYPE = "application/x-waag-artsholland-restapi+json";
    public static final String MIMETYPE = "application/json";

    private RDFJSONFormat() {
        super("RESTAPI/JSON",
                MIMETYPE,
                Charset.forName("UTF-8"),  // See section 3 of the JSON RFC: http://www.ietf.org/rfc/rfc4627.txt
                "json",
                false,  // namespaces are not supported
                true);  // contexts are supported
    }
}
