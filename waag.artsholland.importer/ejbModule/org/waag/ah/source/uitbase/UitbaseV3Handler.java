package org.waag.ah.source.uitbase;
//package org.waag.ah.source.uitbase;
//
//import java.io.InputStream;
//
//import org.apache.tika.exception.TikaException;
//import org.apache.tika.metadata.Metadata;
//import org.apache.tika.parser.ParseContext;
//import org.apache.tika.sax.ContentHandlerDecorator;
//import org.apache.tika.sax.xpath.Matcher;
//import org.apache.tika.sax.xpath.MatchingContentHandler;
//import org.apache.tika.sax.xpath.XPathParser;
//import org.xml.sax.ContentHandler;
//import org.waag.ah.parser.sax.XSPARQLQueryHandler;
//
//
//public class UitbaseV3Handler extends ContentHandlerDecorator {
//    private static final XPathParser PARSER = new XPathParser(null, "");
//    private static final Matcher MATCHER = PARSER.parse("/nubxml/events/descendant::node()");
//    private static final InputStream QUERY = UitbaseV3Handler.class.getResourceAsStream("META-INF/uitbase_v3.txt");
//    
//    public UitbaseV3Handler(ContentHandler handler, Metadata metadata, 
//    		ParseContext context) throws TikaException {
//        super(new MatchingContentHandler(new XSPARQLQueryHandler(handler, QUERY), MATCHER));
//    }    	
//}
