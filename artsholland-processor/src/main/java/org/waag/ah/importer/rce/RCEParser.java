package org.waag.ah.importer.rce;

import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.ContentHandlerDecorator;
import org.apache.tika.sax.xpath.MatchingContentHandler;
import org.waag.ah.importer.AbstractParser;
import org.waag.ah.tika.XSPARQLQueryHandler;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class RCEParser extends AbstractParser {
	private static final long serialVersionUID = 7817046137123630153L;
	
	public class RCEContentHandler extends ContentHandlerDecorator {

		/*
		 * XSPARQL apparently does not accept <str> tags. Unfortunately, 
		 * these tags are used by the Rijksmonumenten API:
		 * http://api.rijksmonumenten.info/select/?q=rce_categorie:%22Openbare%20gebouwen%22&rows=1
		 * Solution: replace <str> tags by <rce_str>
		 */
		
		@Override
		public void startElement(String uri, String localName, String name,
				Attributes atts) throws SAXException {
			if (name == "str" || localName == "str") {
			super.startElement(uri, "rce_" + localName, "rce_" + name, atts);
			} else {
				super.startElement(uri, localName, name, atts);
			}
		}

		@Override
		public void endElement(String uri, String localName, String name)
				throws SAXException {
			if (name == "str" || localName == "str") {			
				super.endElement(uri, "rce_" + localName, "rce_" + name);
			} else {
				super.endElement(uri, localName, name);
			}
		}

		public RCEContentHandler(ContentHandler handler) {
			super(handler);
		}


	}

	
	
	
	

	@SuppressWarnings("serial")
	private static final Set<MediaType> SUPPORTED_TYPES = new HashSet<MediaType>() {
		{
			add(MediaType.application("x-waag-rce+xml"));
		}
	};

	public static final String RCE_MIME_TYPE = "application/x-waag-rce+xml";

	@Override
	public Set<MediaType> getSupportedTypes(ParseContext context) {
		return SUPPORTED_TYPES;
	}

	@Override
	protected ContentHandler getContentHandler(ContentHandler handler,
			Metadata metadata, ParseContext context) {
		try {
			if (metadata.get(Metadata.CONTENT_TYPE).equals(RCE_MIME_TYPE)) {
				Reader xquery = getFileReader(getClass(), "rce.xsparql");
				if (xquery == null) {
					throw new IOException("XQuery definition file not found");
				}
								
				XSPARQLQueryHandler queryHandler = new XSPARQLQueryHandler(handler,	metadata, context, xquery);	
				
				RCEContentHandler blaat = new RCEContentHandler(queryHandler);
				
				return new MatchingContentHandler(blaat,	getXPathMatcher("/response/result/descendant::node()"));				
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return handler;
	}
}
