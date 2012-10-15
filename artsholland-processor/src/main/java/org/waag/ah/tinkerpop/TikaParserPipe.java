package org.waag.ah.tinkerpop;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.waag.ah.tika.ToRDFContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class TikaParserPipe extends AbstractStreamingPipe<URL> {
//	private static final Logger logger = LoggerFactory.getLogger(TikaParserPipe.class);

	class PasswordAuthenticator extends Authenticator {
	  
		private String username;
		private String password;
		
	  PasswordAuthenticator(String username, String password) {
	  	super();
	  	this.username = username;
	  	this.password = password;
	  }
		
		protected PasswordAuthentication getPasswordAuthentication() {	    
	    return new PasswordAuthentication(username, password.toCharArray());
	  }
	}	
	
	@Override
	protected void process(URL url, OutputStream out)
			throws IOException, SAXException, TikaException {
		
		boolean authenticated = false;
		String username = "";
		String password = "";
		
		String userInfo = url.getUserInfo();
		if (userInfo != null) {
			String[] usernamePassword = userInfo.split(":");
			if (usernamePassword.length == 2) {
				username = usernamePassword[0];
				password = usernamePassword[1];
				authenticated = true;
			}
		}		
		
		if (authenticated) {
			Authenticator.setDefault(new PasswordAuthenticator(username, password));
		} else {
			Authenticator.setDefault(null);
		}
		
		URLConnection conn = url.openConnection();
				
		InputStream in = conn.getInputStream();	
		try {
			AutoDetectParser parser = new AutoDetectParser();
			ContentHandler handler = new ToRDFContentHandler(out, "UTF-8");
	
			Metadata metadata = new Metadata();
			metadata.add(Metadata.RESOURCE_NAME_KEY, url.toExternalForm());
			metadata.add(Metadata.CONTENT_ENCODING,
					new InputStreamReader(in).getEncoding());
	
			parser.parse(in, handler, metadata, new ParseContext());
		} finally {
			in.close();
		}
//		return in.getResult();
	}
}
