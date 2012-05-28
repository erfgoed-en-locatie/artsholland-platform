package org.waag.ah.importer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.waag.ah.ImportResource;

public class URLImportResource implements ImportResource {
	private final URL url;
	
	public URLImportResource(URL url) {
		this.url = url;
	}

	@Override
	public InputStream parse() throws IOException {
		URLConnection conn = url.openConnection();
		return conn.getInputStream();		
//		final InputStreamFromOutputStream<String> isos = new InputStreamFromOutputStream<String>() {
////			final URL url
//			@Override
//			public String produce(final OutputStream outStream) throws Exception {
//				URLConnection conn = url.openConnection();
//				InputStream in = conn.getInputStream();
//				
//				AutoDetectParser parser = new AutoDetectParser();
//				ContentHandler handler = new ToXMLContentHandler(outStream, "UTF-8"); 
//
//				Metadata metadata = new Metadata();
//				metadata.add(Metadata.RESOURCE_NAME_KEY, url.toExternalForm());				
//				metadata.add(Metadata.CONTENT_ENCODING,
//						new InputStreamReader(in).getEncoding());		
//
//				parser.parse(in, handler, metadata, new ParseContext());
//				return "OK";
//			}
//		};
//		return isos;
	}
	
	@Override
	public String toString() {
		return "URLImportResource [url=" + url + "]";
	}
}
