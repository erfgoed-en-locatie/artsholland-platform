package org.waag.ah.importer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.ToXMLContentHandler;
import org.waag.ah.ImportResource;
import org.xml.sax.ContentHandler;

import com.gc.iotools.stream.is.InputStreamFromOutputStream;

public class URLImportResource implements ImportResource {
	private final URL url;
	
	public URLImportResource(URL url) {
		this.url = url;
	}

	@Override
	public InputStream parse() throws IOException {
		final InputStreamFromOutputStream<String> isos = new InputStreamFromOutputStream<String>() {
//			final URL url
			@Override
			public String produce(final OutputStream outStream) throws Exception {
				URLConnection conn = url.openConnection();
				InputStream in = conn.getInputStream();
				
				AutoDetectParser parser = new AutoDetectParser();
				ContentHandler handler = new ToXMLContentHandler(outStream, "UTF-8"); 

				Metadata metadata = new Metadata();
				metadata.add(Metadata.RESOURCE_NAME_KEY, url.toExternalForm());				
				metadata.add(Metadata.CONTENT_ENCODING,
						new InputStreamReader(in).getEncoding());		

				parser.parse(in, handler, metadata, new ParseContext());
				return "OK";
			}
		};
		return isos;
	}
	
	@Override
	public String toString() {
		return "URLImportResource [url=" + url + "]";
	}
}
