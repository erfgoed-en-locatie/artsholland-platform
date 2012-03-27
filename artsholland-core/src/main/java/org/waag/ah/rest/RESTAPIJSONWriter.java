package org.waag.ah.rest;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import net.fortytwo.sesametools.rdfjson.RDFJSONWriter;

import org.openrdf.model.Statement;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;

import com.google.gson.stream.JsonWriter;

public class RESTAPIJSONWriter extends RDFJSONWriter {
    private final Writer writer;
    //private Set<Statement> graph;
    
    //private StringWriter stringWriter;
    private JsonWriter jsonWriter;
    private RDFGSON rdfGSON;
    
    public RESTAPIJSONWriter(final OutputStream out) {
        this(new OutputStreamWriter(out, Charset.forName("UTF-8")));                
    }

    public RESTAPIJSONWriter(final Writer writer) {
    	super(writer);
    	this.writer = writer;
        
      // TODO: flush bij einde?
    	//stringWriter = new StringWriter();
      jsonWriter = new JsonWriter(writer);
      rdfGSON = new RDFGSON(jsonWriter);
    }

    @Override
    public RDFFormat getRDFFormat() {
        return RDFJSONFormat.RESTAPIJSON;
    }

    @Override
    public void startRDF() throws RDFHandlerException {
    		try {
					jsonWriter.beginArray();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        //graph = new TreeSet<Statement>(new StatementComparator());
    }

    @Override
    public void endRDF() throws RDFHandlerException {    	
    	
       //RDFJSON.graphToRdfJsonPreordered(graph, writer);  
       
       try {
      	 rdfGSON.end(); 
      	 jsonWriter.endArray();
				
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
    public void handleStatement(final Statement statement) throws RDFHandlerException {
    	
        //graph.add(statement);
        
				try {
					rdfGSON.writeStatement(statement);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
        
    }
}
