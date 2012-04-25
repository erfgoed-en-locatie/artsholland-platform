package org.waag.ah.rest;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Map.Entry;

import net.fortytwo.sesametools.rdfjson.RDFJSONWriter;

import org.openrdf.model.Statement;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.waag.ah.rdf.ConfigurableRDFWriter;
import org.waag.ah.rdf.RDFGSON;
import org.waag.ah.rdf.RDFJSONFormat;
import org.waag.ah.rdf.RDFWriterConfig;

import com.google.gson.stream.JsonWriter;

public class RestJSONWriter extends RDFJSONWriter implements
		ConfigurableRDFWriter {
	private final Writer writer;

	private JsonWriter jsonWriter;
	private RDFGSON rdfGSON;
	private RDFWriterConfig config;

	public RestJSONWriter(final OutputStream out) {
		this(new OutputStreamWriter(out, Charset.forName("UTF-8")));
	}

	public RestJSONWriter(final Writer writer) {
		super(writer);
		this.writer = writer;

		jsonWriter = new JsonWriter(writer);
		rdfGSON = new RDFGSON(jsonWriter);		
	}

	@Override
	public void setConfig(RDFWriterConfig config) {
		this.config = config;
		
		if (config.isPrettyPrint()) {
			jsonWriter.setIndent("\t");
		} else {
			jsonWriter.setIndent("");
		}
		
	}

	@Override
	public RDFFormat getRDFFormat() {
		return RDFJSONFormat.RESTAPIJSON;
	}

	@Override
	public void startRDF() throws RDFHandlerException {
		try {			
			if (config.isWrapResults()) {
				jsonWriter.beginObject();
				if (config.getMetaData() != null && config.getMetaData().size() > 0) {
					jsonWriter.name("metadata");
					jsonWriter.beginObject();
					for (Entry<String, String> entry : config.getMetaData().entrySet()) {
						jsonWriter.name(entry.getKey());
						jsonWriter.value(entry.getValue());
					}
					jsonWriter.endObject();
				}

				jsonWriter.name("results");
				jsonWriter.beginArray();		
			}					
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void endRDF() throws RDFHandlerException {

		try {
			rdfGSON.end();
			if (config.isWrapResults()) {
				jsonWriter.endArray();
				jsonWriter.endObject();
			}
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			writer.flush();
		} catch (IOException e) {
			throw new RDFHandlerException(e);
		}
	}

	@Override
	public void handleStatement(final Statement statement)
			throws RDFHandlerException {

		try {
			rdfGSON.writeStatement(statement);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
