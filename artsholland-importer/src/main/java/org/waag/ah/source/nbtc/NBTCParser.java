package org.waag.ah.source.nbtc;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
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
import org.apache.tika.sax.xpath.MatchingContentHandler;
import org.apache.tika.sax.xpath.XPathParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.tika.handler.XSPARQLQueryHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

@SuppressWarnings("serial")
public class NBTCParser extends XMLParser {
	private Logger logger = LoggerFactory.getLogger(NBTCParser.class);
	private static final long serialVersionUID = 116487633414164925L;

	private static final Set<MediaType> SUPPORTED_TYPES = new HashSet<MediaType>() {{ 
		add(MediaType.application("x-waag-nbtc-pois+xml")); 
	}};
    
	@Override
	public Set<MediaType> getSupportedTypes(ParseContext context) {
		return SUPPORTED_TYPES;
	}

	public void parse(
          InputStream stream, ContentHandler handler,
          Metadata metadata, ParseContext context)
          throws IOException, SAXException, TikaException {
      if (metadata.get(Metadata.CONTENT_TYPE) == null) {
      	throw new TikaException("No content type set");
      }

      TaggedContentHandler tagged = new TaggedContentHandler(handler);
      ContentHandler wrappedHandler = getContentHandler(tagged, metadata, context);
      if (wrappedHandler == null) {
    	  throw new TikaException("Parsing aborted, unable to init Tika handler");
      }
      
      try {
          context.getSAXParser().parse(
                  new CloseShieldInputStream(stream),
                  new OfflineContentHandler(wrappedHandler));
      } catch (SAXException e) {
    	  e.printStackTrace();
          tagged.throwIfCauseOf(e);
          throw new TikaException("XML parse error", e);
      }
	}	

	protected ContentHandler getContentHandler(ContentHandler handler, 
    		Metadata metadata, ParseContext context) {		
		try {
			InputStream xquery = NBTCParser.class.getResourceAsStream("nbtc_pois.xsparql");
			if (xquery == null) {
				throw new IOException("XQuery definition file not found");
			}
			return new MatchingContentHandler(
					new XSPARQLQueryHandler(handler, metadata, context, xquery, null),
					getXPathMatcher("/Pois/descendant::node()"));

		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
    }
    
    private Matcher getXPathMatcher(String selector) {
        return new XPathParser(null, "").parse(selector);
    }
}
