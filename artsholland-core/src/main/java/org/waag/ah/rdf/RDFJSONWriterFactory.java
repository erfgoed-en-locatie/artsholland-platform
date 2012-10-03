package org.waag.ah.rdf;

import java.io.OutputStream;
import java.io.Writer;

import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.RDFWriterFactory;

public class RDFJSONWriterFactory implements RDFWriterFactory {

	@Override
	public RDFFormat getRDFFormat() {
		return RDFJSONFormat.RESTAPIJSON;
	}

	@Override
	public RDFWriter getWriter(OutputStream out) {
		return new RestJSONWriter(out);
	}

	@Override
	public RDFWriter getWriter(Writer writer) {
		return new RestJSONWriter(writer);
	}
}
