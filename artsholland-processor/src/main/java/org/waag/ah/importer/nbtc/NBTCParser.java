package org.waag.ah.importer.nbtc;

import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.xpath.MatchingContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.importer.AbstractParser;
import org.waag.ah.tika.XSPARQLQueryHandler;
import org.xml.sax.ContentHandler;

/**
 * Parser for NBTC POI data.
 * 
 * @author Raoul Wissink <raoul@raoul.net>
 */
@SuppressWarnings("serial")
public class NBTCParser extends AbstractParser {
	private Logger logger = LoggerFactory.getLogger(NBTCParser.class);

	private static final Set<MediaType> SUPPORTED_TYPES = new HashSet<MediaType>() {{ 
		add(MediaType.application("x-waag-nbtc-pois+xml")); 
	}};
    
	@Override
	public Set<MediaType> getSupportedTypes(ParseContext context) {
		return SUPPORTED_TYPES;
	}

	@Override
	protected ContentHandler getContentHandler(ContentHandler handler, 
    		Metadata metadata, ParseContext context) {		
		try {
			Reader xquery = getFileReader(getClass(), "nbtc_pois.xsparql");
			if (xquery == null) {
				throw new IOException("XQuery definition file not found");
			}
			return new MatchingContentHandler(
					new XSPARQLQueryHandler(handler, metadata, /*context,*/ xquery),
					getXPathMatcher("/Pois/descendant::node()"));
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
    }
}
