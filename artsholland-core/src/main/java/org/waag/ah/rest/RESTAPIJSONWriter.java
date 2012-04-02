package org.waag.ah.rest;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Set;
import java.util.TreeSet;

import net.fortytwo.sesametools.StatementComparator;
import net.fortytwo.sesametools.rdfjson.RDFJSONWriter;

import org.openrdf.model.Statement;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;

public class RESTAPIJSONWriter extends RDFJSONWriter {
    private final Writer writer;
    private Set<Statement> graph;
    
    public RESTAPIJSONWriter(final OutputStream out) {
        this(new OutputStreamWriter(out, Charset.forName("UTF-8")));
    }

    public RESTAPIJSONWriter(final Writer writer) {
    	super(writer);
        this.writer = writer;
    }

    @Override
    public RDFFormat getRDFFormat() {
        return RDFJSONFormat.RESTAPIJSON;
    }

    @Override
    public void startRDF() throws RDFHandlerException {
        graph = new TreeSet<Statement>(new StatementComparator());
    }

    @Override
    public void endRDF() throws RDFHandlerException {
        RDFJSON.graphToRdfJsonPreordered(graph, writer);
        try {
            writer.flush();
        } catch (IOException e) {
            throw new RDFHandlerException(e);
        }
    }
    
    @Override
    public void handleStatement(final Statement statement) throws RDFHandlerException {
        graph.add(statement);
    }
}
