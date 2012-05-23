package org.waag.ah.tinkerpop.pipes;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.ToXMLContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class TikaParserPipe extends AbstractStreamingPipe {

	@Override
	protected void processStream(InputStream in, OutputStream out)
			throws IOException, SAXException, TikaException {
		AutoDetectParser parser = new AutoDetectParser();
		ContentHandler handler = new ToXMLContentHandler(out, "UTF-8");

		Metadata metadata = new Metadata();
		// metadata.add(Metadata.RESOURCE_NAME_KEY, url.toExternalForm());
		metadata.add(Metadata.CONTENT_ENCODING,
				new InputStreamReader(in).getEncoding());

		parser.parse(in, handler, metadata, new ParseContext());
	}
}
