package org.waag.ah.saxon;

import java.net.MalformedURLException;
import java.net.URL;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.EmptySequence;
import net.sf.saxon.value.FloatValue;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.value.StringValue;
import net.sf.saxon.value.Value;

import org.apache.commons.configuration.ConfigurationException;
import org.waag.ah.PlatformConfig;
import org.waag.ah.PlatformConfigHelper;

@SuppressWarnings("serial")
public class WKTGeometryFunction extends ExtensionFunctionDefinition {
	private final PlatformConfig config;

	public WKTGeometryFunction() throws ConfigurationException {
		config = PlatformConfigHelper.getConfig();
	}

	@Override
	public SequenceType[] getArgumentTypes() {		
		return new SequenceType[] { SequenceType.OPTIONAL_STRING };
	}

	@Override
	public int getMaximumNumberOfArguments() {
		return 100;
	}

	@Override
	public StructuredQName getFunctionQName() {
		return new StructuredQName("waag", "http://waag.org/saxon-extension",	"wkt-geometry");
	}

	@Override
	public SequenceType getResultType(SequenceType[] suppliedArgumentTypes) {
		return SequenceType.OPTIONAL_STRING;
	}

	@Override
	public ExtensionFunctionCall makeCallExpression() {
		return new ExtensionFunctionCall() {
			public SequenceIterator call(SequenceIterator[] arguments,
					XPathContext context) throws XPathException {
				String point = "";
				try {						
					
					String lat = ((StringValue) arguments[0].next()).getStringValue();
					String lon = ((StringValue) arguments[1].next()).getStringValue();
					
					// WKT POINT specification: first lon, then lat.
					//if ( fn:empty($lat) or fn:empty($long) ) then () else concat( 'POINT(', $long, ' ', $lat, ')' )
					point = "POINT(" + lon + " " + lat + ")";
					
				} catch (NullPointerException e) {
				}				
				if (point.length() > 0) {
					return Value.asIterator(StringValue.makeStringValue(point));
				} else {
					return Value.asIterator(EmptySequence.getInstance());
				}
			}
		};
	}
}
