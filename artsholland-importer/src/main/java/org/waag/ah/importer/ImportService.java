package org.waag.ah.importer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.ToXMLContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.ImportMetadata;
import org.waag.ah.sesame.StoringRDFParser;
import org.xml.sax.ContentHandler;

import com.gc.iotools.stream.is.InputStreamFromOutputStream;

@Stateless
public class ImportService /*implements Service*/ {
	private static final Logger logger = LoggerFactory.getLogger(ImportService.class);
	
	private @EJB StoringRDFParser parser;
	
	public void importURL(List<URL> urls, ImportMetadata metadata) {
		logger.debug("Processing importing job: "+metadata.getJobIdentifier());
		try {
			for (URL url : urls) {
				InputStream stream = null;
				try {
					stream = parse(url);
					parser.parse(stream, metadata);
				} finally {
					stream.close();
				}
			}
			parser.commit();
		} catch (Exception e) {
			parser.rollback();
		}
		logger.debug("Finished importing");	
	}
	
	public void importURL(URL url, ImportMetadata metadata) throws Exception {
		importURL(Arrays.asList(url), metadata);
	}
	
	private InputStream parse(final URL url) throws IOException {
		final InputStreamFromOutputStream<String> isos = new InputStreamFromOutputStream<String>() {
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
}
