package org.waag.ah.cascading;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.ToXMLContentHandler;
import org.waag.ah.cascading.tap.ParsedDocumentDatum;
import org.xml.sax.ContentHandler;

import bixo.datum.ParsedDatum;

public class TikaCallable implements Callable<ParsedDatum> {
    private Parser parser;
	private InputStream input;
	private Metadata metadata;
	private ParseContext parseContext;

	public TikaCallable(Parser parser, InputStream input, Metadata metadata, ParseContext parseContext) {
        this.parser = parser;
        this.input = input;
        this.metadata = metadata;
        this.parseContext = parseContext;
    }
    
    @Override
    public ParsedDatum call() throws Exception {
        try {        	
        	ContentHandler handler = new ToXMLContentHandler("UTF-8"); 
            parser.parse(input, handler, metadata, parseContext);
            return new ParsedDocumentDatum(metadata.get(Metadata.RESOURCE_NAME_KEY), handler.toString(), makeMap(metadata));
        } catch (Exception e) {
            // Generic exception that's OK to re-throw
            throw e;
        } catch (NoSuchMethodError e) {
            throw new RuntimeException("Attempting to use excluded parser");
        } catch (Throwable t) {
            // Make sure nothing inside Tika can kill us
            throw new RuntimeException("Serious shut-down error thrown from Tika", t);
        }
    }
    
    private static Map<String, String> makeMap(Metadata metadata) {
        Map<String, String> result = new HashMap<String, String>();
        
        for (String key : metadata.names()) {
            result.put(key, metadata.get(key));
        }
        
        return result;
    }    
}
