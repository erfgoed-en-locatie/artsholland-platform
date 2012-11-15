//package org.waag.ah.importer.dosa;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Set;
//
//import org.apache.tika.exception.TikaException;
//import org.apache.tika.metadata.Metadata;
//import org.apache.tika.mime.MediaType;
//import org.apache.tika.mime.MediaTypeRegistry;
//import org.apache.tika.parser.ParseContext;
//import org.apache.tika.parser.Parser;
//import org.apache.tika.parser.microsoft.OfficeParser;
//import org.apache.tika.sax.TaggedContentHandler;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.waag.ah.importer.AbstractParser;
//import org.waag.ah.tika.XSPARQLQueryHandler;
//import org.xml.sax.ContentHandler;
//import org.xml.sax.SAXException;
//
//public class DOSAParser extends AbstractParser {
//	private Logger logger = LoggerFactory.getLogger(DOSAParser.class);
//	private static final long serialVersionUID = -3133605121375062159L;
//	
//	private static final Set<MediaType> SUPPORTED_TYPES = Collections.singleton(
//    		MediaType.application("x-waag-dosa-excel"));
//	
//    public static final String DOSAXLS_MIME_TYPE = "application/x-waag-dosa-excel";
//
//	@Override
//	public Set<MediaType> getSupportedTypes(ParseContext context) {
//		return SUPPORTED_TYPES;
//	}
//	
//    public DOSAParser() {
//        super(new MediaTypeRegistry(), new OfficeParser());
//    }
//
//	@Override
//	public void parse(InputStream stream, ContentHandler handler,
//			Metadata metadata, ParseContext context) throws IOException,
//			SAXException, TikaException {
//		
//		TaggedContentHandler tagged = new TaggedContentHandler(handler);
//		ContentHandler wrappedHandler = getContentHandler(tagged, metadata, context);
//		if (wrappedHandler == null) {
//			throw new TikaException("Parsing aborted, unable to init Tika handler");
//		}
//		
////		Map<String, String> vars = new HashMap<String, String>();
////		vars.put("config", UitbaseParser.getFileContents(getClass(), "META-INF/tables.xml"));
////		vars.put("config", NBTCParser.getFileContents(getClass(), "META-INF/tables.xml"));
////		
//		Parser parser = getParser(MediaType.application("vnd.ms-excel"));
////		parser.parse(stream, new XQueryContentHandler(handler, getFileContents(getClass(), "dosa_xls.xsparql"),
////				metadata, context), 
////				metadata, context);
//  
////	      try {
////	          context.getSAXParser().parse(
////	                  new CloseShieldInputStream(stream),
////	                  new OfflineContentHandler(wrappedHandler));
////	      } catch (SAXException e) {
////	          tagged.throwIfCauseOf(e);
////	          throw new TikaException("XML parse error", e);
////	      }
//		Map<String, URI> includes = new HashMap<String, URI>();
//		includes.put("config", DOSAParser.getFileURI(DOSAParser.class, "tables.xml"));	      
//		try {
//			parser.parse(stream, new XSPARQLQueryHandler(handler, metadata,
//					context, getFileContents(getClass(), "dosa_xls.xsparql"), includes),
//					metadata, context);
//	      } catch (Exception e) {
//	    	  tagged.throwIfCauseOf(e);
//	      }
//	
///*
//		try {
//			context.getSAXParser().parse(new CloseShieldInputStream(stream),
//					new OfflineContentHandler(wrappedHandler));
//		} catch (SAXException e) {
//			tagged.throwIfCauseOf(e);
//			throw new TikaException("XML parse error", e);
//		}*/
//		
//		
//		
//	}
//	
//	protected ContentHandler getContentHandler(ContentHandler handler,
//			Metadata metadata, ParseContext context) {
//		try {
//
////			if (metadata.get(Metadata.CONTENT_TYPE).equals(DOSAXLS_MIME_TYPE)) {
//				
//				Map<String, URI> includes = new HashMap<String, URI>();
//				includes.put("config", DOSAParser.getFileURI(DOSAParser.class, "tables.xml"));
//				return new XSPARQLQueryHandler(handler, metadata,
//						context, getFileContents(getClass(), "dosa_xls.xsparql"), includes);  
//				
////				InputStream xquery = getFileContents(getClass(), "dosa_xls.xsparql");
////				if (xquery == null) {
////					throw new IOException("XQuery definition file not found");
////				}				
//				
//				//Map<String, String> vars = new HashMap<String, String>();
//				//vars.put("config", getFileContents(getClass(), "tables.xml"));
//				
//				
//				//
//				// Parser parser = getParser(MediaType.application("vnd.ms-excel"));
//				// parser.parse(stream, new XQueryContentHandler(handler,
//				// UitbaseParser.getFileContents(getClass(), "META-INF/dosa_xls.xquery"),
//				// NBTCParser.getFileContents(getClass(), "META-INF/dosa_xls.xquery"),
//				// metadata, context),
//				// metadata, context);				
//				
//				
////				return new MatchingContentHandler(new XSPARQLQueryHandler(handler,
////						metadata, context, xquery),
////						getXPathMatcher("/"));
//				
////			}
//		} catch (Exception e) {
//			logger.error(e.getMessage());
//			return null;
//		}
////		return handler;
//	}
//	
//	private Parser getParser(MediaType mediaType) {
//		Metadata metadata = new Metadata();
//		metadata.set(Metadata.CONTENT_TYPE, mediaType.toString());
//		return super.getParser(metadata);
//	}
//	
////	private Matcher getXPathMatcher(String selector) {
////		return new XPathParser(null, "").parse(selector);
////	}
//	
//	public static URI getFileURI(Class<?> clazz, String fileName)
//			throws IOException {
//		URI uri = null;
//		try {
//			uri = new URI(clazz.getResource(fileName).toString());
//		} catch (URISyntaxException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return uri;
//	}
//	
//	public static InputStream getFileContents(Class<?> clazz, String fileName)
//			throws IOException {
//		InputStream input = clazz.getResourceAsStream(fileName);
//		return input;
//	}
//
//}
