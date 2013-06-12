package org.waag.ah.importer.atcb;

import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.xpath.MatchingContentHandler;
import org.waag.ah.importer.AbstractParser;
import org.waag.ah.tika.XSPARQLQueryHandler;
import org.xml.sax.ContentHandler;

public class ATCBParser extends AbstractParser {
//	private Logger logger = LoggerFactory.getLogger(TAMParser.class);
	private static final long serialVersionUID = 116987633314164925L;

	@SuppressWarnings("serial")
	private static final Set<MediaType> SUPPORTED_TYPES = new HashSet<MediaType>() {
		{
			add(MediaType.application("x-waag-atcb+xml"));
		}
	};

	public static final String ATCB_MIME_TYPE = "application/x-waag-atcb+xml";

	@Override
	public Set<MediaType> getSupportedTypes(ParseContext context) {
		return SUPPORTED_TYPES;
	}

	@Override
	protected ContentHandler getContentHandler(ContentHandler handler,
			Metadata metadata, ParseContext context) {
		try {
			if (metadata.get(Metadata.CONTENT_TYPE).equals(ATCB_MIME_TYPE)) {
				Reader xquery = getFileReader(getClass(), "atcb.xsparql");
				if (xquery == null) {
					throw new IOException("XQuery definition file not found");
				}				
				
				XSPARQLQueryHandler queryHandler = new XSPARQLQueryHandler(handler,	metadata, xquery);				
				return new MatchingContentHandler(queryHandler,	getXPathMatcher("/items/descendant::node()"));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return handler;
	}
}
