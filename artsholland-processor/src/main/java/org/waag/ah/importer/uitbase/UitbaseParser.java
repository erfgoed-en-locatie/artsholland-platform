package org.waag.ah.importer.uitbase;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.stream.StreamSource;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.xpath.Matcher;
import org.apache.tika.sax.xpath.MatchingContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.importer.AbstractParser;
import org.waag.ah.tika.XSPARQLQueryHandler;
import org.xml.sax.ContentHandler;

/**
 * Parser for NUB Uitbase data.
 * 
 * @author Raoul Wissink <raoul@raoul.net>
 */
@SuppressWarnings("serial")
public class UitbaseParser extends AbstractParser {
	private Logger logger = LoggerFactory.getLogger(UitbaseParser.class);

	private static final Set<MediaType> SUPPORTED_TYPES = new HashSet<MediaType>() {{ 
		add(MediaType.application("x-waag-uitbase-v3+xml")); 
		add(MediaType.application("x-waag-uitbase-v4+xml")); 
	}};
	
    public static final String UITBASEV3_MIME_TYPE = "application/x-waag-uitbase-v3+xml";
    public static final String UITBASEV4_MIME_TYPE = "application/x-waag-uitbase-v4+xml";
    
	@Override
	public Set<MediaType> getSupportedTypes(ParseContext context) {
		return SUPPORTED_TYPES;
	}

	protected ContentHandler getContentHandler(ContentHandler handler, 
    		Metadata metadata, ParseContext context) {		
		try {
			// As we don't want to load the entire input document in memory
			// for XQuery processing, we handle each node separately
			// (event/production/location/group).
			if (metadata.get(Metadata.CONTENT_TYPE).equals(UITBASEV3_MIME_TYPE)) {
				Reader xquery = getFileReader(getClass(), "v3/event.xsparql");    			
				return new MatchingContentHandler(
					new XSPARQLQueryHandler(handler, metadata, /*context,*/ xquery), 
					getXPathMatcher("/nubxml/events/descendant::node()"));
				
			} else if (metadata.get(Metadata.CONTENT_TYPE).equals(UITBASEV4_MIME_TYPE)) {
				Reader xquery = getFileReader(getClass(), "v4.xsparql");
				if (xquery == null) {
					throw new IOException("XQuery definition file not found");
				}
				
				Map<String, StreamSource> includes = new HashMap<String, StreamSource>();
//				logger.info("PATH: "+getClass().getResource("venuetypes.xml").getPath());
				includes.put("venueTypesExternal", new StreamSource(getClass().getResourceAsStream("venuetypes.xml")));
				
				XSPARQLQueryHandler queryHandler = new XSPARQLQueryHandler(handler, metadata, /*context,*/ xquery, includes);
				Matcher xpathMatcher = getXPathMatcher("/search/descendant::node()");
				
				return new MatchingContentHandler(queryHandler, xpathMatcher);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
    }
}
