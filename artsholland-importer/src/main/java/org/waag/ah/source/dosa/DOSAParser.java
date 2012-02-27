package org.waag.ah.source.dosa;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MediaTypeRegistry;
import org.apache.tika.parser.CompositeParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.microsoft.OfficeParser;
import org.waag.ah.source.uitbase.UitbaseParser;
import org.waag.ah.tika.parser.sax.XQueryContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class DOSAParser extends CompositeParser {
	private static final long serialVersionUID = -3133605121375062159L;
	
	private static final Set<MediaType> SUPPORTED_TYPES = Collections.singleton(
    		MediaType.application("x-waag-dosa-excel"));
	
    public static final String DOSAXLS_MIME_TYPE = "application/x-waag-dosa-excel";

	@Override
	public Set<MediaType> getSupportedTypes(ParseContext context) {
		return SUPPORTED_TYPES;
	}
	
    public DOSAParser() {
        super(new MediaTypeRegistry(), new OfficeParser());
    }

	@Override
	public void parse(InputStream stream, ContentHandler handler,
			Metadata metadata, ParseContext context) throws IOException,
			SAXException, TikaException {
		
		Map<String, String> vars = new HashMap<String, String>();
		vars.put("config", UitbaseParser.getFileContents(getClass(), "META-INF/tables.xml"));
		
		Parser parser = getParser(MediaType.application("vnd.ms-excel"));
		parser.parse(stream, new XQueryContentHandler(handler, 
				UitbaseParser.getFileContents(getClass(), "META-INF/dosa_xls.xquery"),
				metadata, context), 
				metadata, context);
	}
	
	private Parser getParser(MediaType mediaType) {
		Metadata metadata = new Metadata();
		metadata.set(Metadata.CONTENT_TYPE, mediaType.toString());
		return super.getParser(metadata);
	}
}