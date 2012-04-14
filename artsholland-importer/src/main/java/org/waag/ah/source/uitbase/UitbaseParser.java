package org.waag.ah.source.uitbase;

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
import org.waag.ah.tika.parser.sax.XSPARQLQueryHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class UitbaseParser extends XMLParser {
	private Logger logger = LoggerFactory.getLogger(UitbaseParser.class);
	private static final long serialVersionUID = 116487633414164925L;

	@SuppressWarnings("serial")
	private static final Set<MediaType> SUPPORTED_TYPES = new HashSet<MediaType>() {{ 
		add(MediaType.application("x-waag-uitbase-v3+xml")); 
		add(MediaType.application("x-waag-uitbase-v4+xml")); 
		
		//TODO: delete
		/*
		add(MediaType.application("x-waag-uitbase-v4-event+xml"));
		add(MediaType.application("x-waag-uitbase-v4-production+xml"));
		add(MediaType.application("x-waag-uitbase-v4-location+xml"));
		add(MediaType.application("x-waag-uitbase-v4-group+xml"));
		
		add(MediaType.application("x-waag-uitbase-v4-event-old+xml"));
		add(MediaType.application("x-waag-uitbase-v4-production-old+xml"));
		add(MediaType.application("x-waag-uitbase-v4-location-old+xml"));
		add(MediaType.application("x-waag-uitbase-v4-group-old+xml"));*/
	}};
	
	/*private static final Set<MediaType> SUPPORTED_TYPES = Collections.singleton(
    		MediaType.application("x-waag-uitbase-v3+xml"));*/
	
    public static final String UITBASEV3_MIME_TYPE = "application/x-waag-uitbase-v3+xml";
    public static final String UITBASEV4_MIME_TYPE = "application/x-waag-uitbase-v4+xml";
    
		//TODO: delete
    /*
    public static final String UITBASEV4_EVENT_MIME_TYPE = "application/x-waag-uitbase-v4-event+xml";
    public static final String UITBASEV4_PRODUCTION_MIME_TYPE = "application/x-waag-uitbase-v4-production+xml";
    public static final String UITBASEV4_LOCATION_MIME_TYPE = "application/x-waag-uitbase-v4-location+xml";
    public static final String UITBASEV4_GROUP_MIME_TYPE = "application/x-waag-uitbase-v4-group+xml"; 
*/
    
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
          tagged.throwIfCauseOf(e);
          throw new TikaException("XML parse error", e);
      }
	}	

	protected ContentHandler getContentHandler(ContentHandler handler, 
    		Metadata metadata, ParseContext context) {		
		try {
			// As we don't want to load the entire input document in memory
			// for XQuery processing, we handle each node separately
			// (event/production/location/group).
			if (metadata.get(Metadata.CONTENT_TYPE).equals(UITBASEV3_MIME_TYPE)) {
				logger.debug("Parsing V3 document");
				InputStream xquery = getFileContents(getClass(), "v3/event.xsparql");    			
				return new MatchingContentHandler(
					new XSPARQLQueryHandler(handler, metadata, context, xquery, ""), 
					getXPathMatcher("/nubxml/events/descendant::node()"));
				
			} else if (metadata.get(Metadata.CONTENT_TYPE).equals(UITBASEV4_MIME_TYPE)) {
				logger.debug("Parsing V4 document");
				
				InputStream xquery = getFileContents(getClass(), "v4.xsparql");
				
				if (xquery == null) {
					throw new IOException("XQuery definition file not found");
				}
				
				return new MatchingContentHandler(
						new XSPARQLQueryHandler(handler, metadata, context, xquery, "event", "production", "location", "group"),
						getXPathMatcher("/descendant::node()"));
				
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
		return handler;
    }
    
	/**
	 * @todo Move to utility class.
	 */
	
    public static InputStream getFileContents(Class<?> clazz, String fileName) throws IOException {
    	InputStream input = clazz.getResourceAsStream(fileName);
//    	BufferedReader reader = new BufferedReader(new InputStreamReader(input));
    	return input;
//    	String str = "";
//    	StringBuffer buffer = new StringBuffer();
//        while ((str = reader.readLine()) != null) {
//            buffer.append(str + "\n");
//        }
//        reader.close();
//        input.close();
//        return buffer.toString();
    }
    
    private Matcher getXPathMatcher(String selector) {
        return new XPathParser(null, "").parse(selector);
    }
}
