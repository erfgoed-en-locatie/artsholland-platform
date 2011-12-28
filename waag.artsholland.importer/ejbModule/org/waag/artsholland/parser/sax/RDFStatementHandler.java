package org.waag.artsholland.parser.sax;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.sax.XHTMLContentHandler;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;


public class RDFStatementHandler extends XHTMLContentHandler 
		implements RDFHandler {

	public RDFStatementHandler(ContentHandler handler, Metadata metadata) {
		super(handler, metadata);
	}

	@Override
	public void startRDF() throws RDFHandlerException {
		try {
			super.startDocument();
			super.startElement("ul");
		} catch (SAXException e) {
			e.printStackTrace();
			throw new RDFHandlerException(e.getMessage(), e);
		}
	}

	@Override
	public void endRDF() throws RDFHandlerException {
		try {
			super.endElement("ul");
			super.endDocument();
		} catch (SAXException e) {
			e.printStackTrace();
			throw new RDFHandlerException(e.getMessage(), e);
		}
	}

	@Override
	public void handleNamespace(String prefix, String uri)
			throws RDFHandlerException {
		try {
			super.startPrefixMapping(prefix, uri);
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void handleStatement(Statement st) throws RDFHandlerException {
//		<meta about="p:TonyBlair" property="con:fullName">Tony Blair</meta>
//		http://www.amsterdamsuitburo.nl/event/2011-A-001-0415580-nl_NL, http://www.w3.org/1999/02/22-rdf-syntax-ns#type, http://purl.org/NET/c4dm/event.owl#Event
//		System.out.println(st.getObject().getClass());
		try {
			AttributesImpl atts = new AttributesImpl();
			atts.addAttribute("", "about", "", "", st.getSubject().stringValue());
			atts.addAttribute("", "property", "", "", st.getPredicate().stringValue());
			if (st.getObject() instanceof LiteralImpl) {
				processAttributes(atts, (LiteralImpl)st.getObject());
			}
			super.startElement("li", atts);
			super.characters(st.getObject().stringValue());
			super.endElement("li");
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}
	
	private void processAttributes(AttributesImpl atts, LiteralImpl object) {
//		System.out.println("EXT: "+object.getDatatype()+", VALUE: "+object.toString());
		if (object.getDatatype() != null) {
			atts.addAttribute("", "datatype", "", "", object.getDatatype().stringValue());
		}
	}

	@Override
	public void handleComment(String comment) throws RDFHandlerException {}
}