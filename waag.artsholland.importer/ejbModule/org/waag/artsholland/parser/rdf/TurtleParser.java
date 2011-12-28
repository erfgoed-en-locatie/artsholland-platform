package org.waag.artsholland.parser.rdf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Set;

import org.apache.tika.exception.TikaException;
import org.apache.tika.io.CloseShieldInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AbstractParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.OfflineContentHandler;
import org.apache.tika.sax.TaggedContentHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.rdfxml.RDFXMLWriter;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class TurtleParser extends AbstractParser {
	private static final long serialVersionUID = -5985049285377742715L;

	private static final Set<MediaType> SUPPORTED_TYPES = Collections.singleton(
    		MediaType.text("turtle"));
    public static final String MIME_TYPE = "text/turtle";

	private org.openrdf.rio.turtle.TurtleParser turtleParser;

	@Override
	public Set<MediaType> getSupportedTypes(ParseContext context) {
		return SUPPORTED_TYPES;
	}

	public TurtleParser() {
		super();
		turtleParser = new org.openrdf.rio.turtle.TurtleParser();
	}

	@Override
	public void parse(InputStream stream, ContentHandler handler,
			Metadata metadata, ParseContext context) throws IOException,
			SAXException, TikaException {
		
		StringWriter rdfXmlData = new StringWriter();
		turtleParser.setRDFHandler(new RDFXMLWriter(rdfXmlData));
		
		try {
			turtleParser.parse(stream, "http://localhost/");
		} catch (RDFParseException e) {
			e.printStackTrace();
		} catch (RDFHandlerException e) {
			e.printStackTrace();
		}
		
		TaggedContentHandler tagged = new TaggedContentHandler(handler);
        context.getSAXParser().parse(
                new CloseShieldInputStream(new ByteArrayInputStream(rdfXmlData.toString().getBytes())),
                new OfflineContentHandler(tagged));
	}
}
