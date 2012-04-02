package org.waag.ah.rest;

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
		return new RESTAPIJSONWriter(out);
	}

	@Override
	public RDFWriter getWriter(Writer writer) {
		return new RESTAPIJSONWriter(writer);
	}
}
