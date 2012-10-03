package org.waag.ah.importer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.apache.tika.exception.TikaException;
import org.apache.tika.io.CloseShieldInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.xml.XMLParser;
import org.apache.tika.sax.OfflineContentHandler;
import org.apache.tika.sax.TaggedContentHandler;
import org.apache.tika.sax.xpath.Matcher;
import org.apache.tika.sax.xpath.XPathParser;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

@SuppressWarnings("serial")
public abstract class AbstractParser extends XMLParser {
	
	@Override
	public final void parse(InputStream stream, ContentHandler handler,
			Metadata metadata, ParseContext context) throws IOException,
			SAXException, TikaException {
		if (metadata.get(Metadata.CONTENT_TYPE) == null) {
			throw new TikaException("No content type set");
		}

		TaggedContentHandler tagged = new TaggedContentHandler(handler);
		ContentHandler wrappedHandler = getContentHandler(tagged, metadata,
				context);
		if (wrappedHandler == null) {
			throw new TikaException(
					"Parsing aborted, unable to init Tika handler");
		}

		try {
			context.getSAXParser().parse(new CloseShieldInputStream(stream),
					new OfflineContentHandler(wrappedHandler));
		} catch (Exception e) {
			tagged.throwIfCauseOf(e);
			throw new TikaException("XML parse error", e);
		}
	}
 	
    protected InputStream getFileContents(Class<?> clazz, String fileName) throws IOException {
    	return clazz.getResourceAsStream(fileName);
    }
    
    protected Matcher getXPathMatcher(String selector) {
        return new XPathParser(null, "").parse(selector);
    }
    
	@Override
	public abstract Set<MediaType> getSupportedTypes(ParseContext context);
	
	@Override
	protected abstract ContentHandler getContentHandler(ContentHandler handler, 
    		Metadata metadata, ParseContext context);	
}
