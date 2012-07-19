//package org.waag.ah.tika.parser.sax;
//
//import java.io.ByteArrayInputStream;
//import java.io.File;
//import java.io.InputStream;
//import java.io.StringReader;
//import java.nio.charset.Charset;
//
//import javax.xml.transform.stream.StreamSource;
//
//import net.sf.saxon.Configuration;
//import net.sf.saxon.s9api.DocumentBuilder;
//import net.sf.saxon.s9api.Processor;
//import net.sf.saxon.s9api.QName;
//import net.sf.saxon.s9api.XQueryCompiler;
//import net.sf.saxon.s9api.XQueryEvaluator;
//import net.sf.saxon.s9api.XQueryExecutable;
//import net.sf.saxon.s9api.XdmItem;
//import net.sf.saxon.s9api.XdmNode;
//
//import org.apache.tika.exception.TikaException;
//import org.apache.tika.metadata.Metadata;
//import org.apache.tika.parser.ParseContext;
//import org.apache.tika.sax.ContentHandlerDecorator;
//import org.apache.tika.sax.ToXMLContentHandler;
//import org.deri.xsparql.XSPARQLProcessor;
//import org.waag.ah.tika.parser.rdf.TurtleParser;
//import org.xml.sax.Attributes;
//import org.xml.sax.ContentHandler;
//import org.xml.sax.SAXException;
//
//public class XQueryContentHandler extends ContentHandlerDecorator {
//
//	private ContentHandler handler;
//	private XQueryEvaluator evaluator;
//	private ToXMLContentHandler xmlCollector;
//	private InputStream query;
//	private TurtleParser turtleParser;
//	private ParseContext context;
//	private Metadata metadata;
//
//	public XQueryContentHandler(ContentHandler handler, InputStream query, 
//			Metadata metadata, ParseContext context)
//			throws TikaException {
//		super(handler);
//		
//		this.handler = handler; 
//		this.query = query; 
//		this.metadata = metadata; 
//		this.context = context; 
//		this.xmlCollector = new ToXMLContentHandler();
//		this.turtleParser = new TurtleParser();
//	}
//	
//	@Override
//	public void startPrefixMapping(String prefix, String uri)
//			throws SAXException {
//		xmlCollector.startPrefixMapping(prefix, uri);
//	}
//
//	@Override
//	public void startDocument() throws SAXException {
//		xmlCollector.startDocument();
//	}
//
//	@Override
//	public void endDocument() throws SAXException {
//		xmlCollector.endDocument();
//		System.out.println(xmlCollector.toString());
//
//		try {
//			XSPARQLProcessor xp = new XSPARQLProcessor();
//			String q = xp.process(query);//new StringReader(query));
//			Configuration config = new Configuration();
//			Processor processor = new Processor(config);
//			XQueryCompiler compiler = processor.newXQueryCompiler();
//			XQueryExecutable executable = compiler.compile(q);
//			evaluator = executable.load();	
//			
//			DocumentBuilder docBuilder = processor.newDocumentBuilder();
//			XdmNode document = docBuilder.build(new File("ejbModule/org/waag/ah/source/dosa/META-INF/tables.xml"));
//			evaluator.setExternalVariable(new QName("config"), document);
//			
//			evaluator.setSource(new StreamSource(new StringReader(xmlCollector.toString())));
//			
//			StringBuilder combined = new StringBuilder();
//			for (XdmItem item : evaluator) {
//				combined.append(item);
//			}
//			
//			Metadata metadata = new Metadata();
//			metadata.set(Metadata.CONTENT_TYPE, "text/turtle");
//        
//			System.out.println(combined.toString());
//			
//			Charset charset = Charset.forName(this.metadata.get(Metadata.CONTENT_ENCODING));
//			turtleParser.parse(
//					new ByteArrayInputStream(combined.toString().getBytes(charset)), 
//					handler, metadata, context);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}			
//		
//	}
//
//	@Override
//	public void startElement(String uri, String localName, String name,
//			Attributes atts) throws SAXException {
//		xmlCollector.startElement(uri, localName, name, atts);
//	}
//
//	@Override
//	public void endElement(String uri, String localName, String name)
//			throws SAXException {
//		xmlCollector.endElement(uri, localName, name);
//	}
//
//	@Override
//	public void characters(char[] ch, int start, int length)
//			throws SAXException {
//		xmlCollector.characters(ch, start, length);
//	}
//}
