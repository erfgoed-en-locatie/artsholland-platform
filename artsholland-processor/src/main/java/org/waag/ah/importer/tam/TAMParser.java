package org.waag.ah.importer.tam;

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
import org.apache.tika.sax.xpath.MatchingContentHandler;
import org.waag.ah.importer.AbstractParser;
import org.waag.ah.tika.XSPARQLQueryHandler;
import org.xml.sax.ContentHandler;

public class TAMParser extends AbstractParser {
//	private Logger logger = LoggerFactory.getLogger(TAMParser.class);
	private static final long serialVersionUID = 116987633414164925L;

	@SuppressWarnings("serial")
	private static final Set<MediaType> SUPPORTED_TYPES = new HashSet<MediaType>() {
		{
			add(MediaType.application("x-waag-tam+xml"));
		}
	};

	public static final String TAM_MIME_TYPE = "application/x-waag-tam+xml";

	@Override
	public Set<MediaType> getSupportedTypes(ParseContext context) {
		return SUPPORTED_TYPES;
	}

	@Override
	protected ContentHandler getContentHandler(ContentHandler handler,
			Metadata metadata, ParseContext context) {
		try {
			if (metadata.get(Metadata.CONTENT_TYPE).equals(TAM_MIME_TYPE)) {
				Reader xquery = getFileReader(getClass(), "tam.xsparql");
				if (xquery == null) {
					throw new IOException("XQuery definition file not found");
				}
				
				Map<String, StreamSource> includes = new HashMap<String, StreamSource>();
				includes.put("taxonomy", new StreamSource(getClass().getResourceAsStream("taxonomy.xml")));
				
				XSPARQLQueryHandler queryHandler = new XSPARQLQueryHandler(handler,	metadata, /*context,*/ xquery, includes);
				
				return new MatchingContentHandler(queryHandler,	getXPathMatcher("/node_export/descendant::node()"));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return handler;
	}
}
