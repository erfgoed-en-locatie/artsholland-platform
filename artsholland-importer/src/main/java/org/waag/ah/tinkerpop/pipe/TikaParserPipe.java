package org.waag.ah.tinkerpop.pipe;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.ToXMLContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class TikaParserPipe extends AbstractStreamingPipe<URL> {
//	private static final Logger logger = LoggerFactory.getLogger(TikaParserPipe.class);

	@Override
	protected void process(URL url, OutputStream out)
			throws IOException, SAXException, TikaException {
		URLConnection conn = url.openConnection();
		InputStream in = conn.getInputStream();		
		
		AutoDetectParser parser = new AutoDetectParser();
		ContentHandler handler = new ToXMLContentHandler(out, "UTF-8");

		Metadata metadata = new Metadata();
		metadata.add(Metadata.RESOURCE_NAME_KEY, url.toExternalForm());
		metadata.add(Metadata.CONTENT_ENCODING,
				new InputStreamReader(in).getEncoding());

		parser.parse(in, handler, metadata, new ParseContext());
	}
}
