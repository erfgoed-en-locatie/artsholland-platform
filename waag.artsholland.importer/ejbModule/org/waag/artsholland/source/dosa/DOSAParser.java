package org.waag.artsholland.source.dosa;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Set;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AbstractParser;
import org.apache.tika.parser.ParseContext;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class DOSAParser extends AbstractParser { //CompositeParser
	private static final long serialVersionUID = -3133605121375062159L;
	
	private static final Set<MediaType> SUPPORTED_TYPES = Collections.singleton(
    		MediaType.application("vnd.waag.dosa-excel"));
    public static final String UITBASEV3_MIME_TYPE = "application/vnd.waag.dosa-excel";

	@Override
	public Set<MediaType> getSupportedTypes(ParseContext context) {
		return SUPPORTED_TYPES;
	}

	@Override
	public void parse(InputStream stream, ContentHandler handler,
			Metadata metadata, ParseContext context) throws IOException,
			SAXException, TikaException {
	}
}
