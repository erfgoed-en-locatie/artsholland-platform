package org.waag.ah.source.uitbase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Set;

import org.apache.log4j.Logger;
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
import org.waag.ah.tika.parser.sax.XSPARQLQueryHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class UitbaseParser extends XMLParser {
	private Logger logger = Logger.getLogger(UitbaseParser.class);
	private static final long serialVersionUID = 116487633414164925L;

	private static final Set<MediaType> SUPPORTED_TYPES = Collections.singleton(
    		MediaType.application("x-waag-uitbase-v3+xml"));
	
    public static final String UITBASEV3_MIME_TYPE = "application/x-waag-uitbase-v3+xml";

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
      
      try {
          context.getSAXParser().parse(
                  new CloseShieldInputStream(stream),
                  new OfflineContentHandler(
                  		getContentHandler(tagged, metadata, context)));
      } catch (SAXException e) {
          tagged.throwIfCauseOf(e);
          throw new TikaException("XML parse error", e);
      }
	}	

	protected ContentHandler getContentHandler(ContentHandler handler, 
    		Metadata metadata, ParseContext context) {
    	if (metadata.get(Metadata.CONTENT_TYPE).equals(UITBASEV3_MIME_TYPE)) {
    		try {
    			String xquery = getFileContents(getClass(), "META-INF/uitbase_v3_new.xquery");
    			// As we don't want to load the entire input document in memory
    			// for XQuery processing, we handle each event node separately.
				return new MatchingContentHandler(
					new XSPARQLQueryHandler(handler, metadata, context, xquery), 
					getXPathMatcher("/nubxml/events/descendant::node()"));
			} catch (TikaException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
		return handler;
    }
    
	/**
	 * @todo Move to utility class.
	 */
	
    public static String getFileContents(Class<?> clazz, String fileName) throws IOException {
    	InputStream input = clazz.getResourceAsStream(fileName);
    	BufferedReader reader = new BufferedReader(new InputStreamReader(input));
    	String str = "";
    	StringBuffer buffer = new StringBuffer();
        while ((str = reader.readLine()) != null) {
            buffer.append(str + "\n");
        }
        reader.close();    
        input.close();    
        return buffer.toString();
    }
    
    private Matcher getXPathMatcher(String selector) {
        return new XPathParser(null, "").parse(selector);
    }
}
