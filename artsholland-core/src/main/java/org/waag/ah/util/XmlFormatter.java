package org.waag.ah.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.InputSource;

public class XmlFormatter {
	static public String format(String xml) {
		try {
			Transformer serializer = SAXTransformerFactory.newInstance()
					.newTransformer();
			serializer.setOutputProperty(OutputKeys.INDENT, "yes");
			// serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
			// "yes");
			serializer.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "2");
			// serializer.setOutputProperty("{http://xml.customer.org/xslt}indent-amount",
			// "2");
			Source xmlSource = new SAXSource(new InputSource(
					new ByteArrayInputStream(xml.getBytes())));
			StreamResult res = new StreamResult(new ByteArrayOutputStream());
			serializer.transform(xmlSource, res);
			return new String(
					((ByteArrayOutputStream) res.getOutputStream())
							.toByteArray());
		} catch (Exception e) {
			return xml;
		}
	}
}
