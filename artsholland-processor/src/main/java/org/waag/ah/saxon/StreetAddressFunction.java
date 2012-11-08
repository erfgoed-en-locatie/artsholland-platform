package org.waag.ah.saxon;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.EmptySequence;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.value.StringValue;
import net.sf.saxon.value.Value;

@SuppressWarnings("serial")
public class StreetAddressFunction extends ExtensionFunctionDefinition {

	@Override
	public SequenceType[] getArgumentTypes() {
		return new SequenceType[] { SequenceType.OPTIONAL_STRING };
	}

	@Override
	public StructuredQName getFunctionQName() {
		return new StructuredQName("waag", "http://waag.org/saxon-extension",
				"street-address");
	}
	
	@Override
	public int getMaximumNumberOfArguments() {
		return 2;
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
				// concat twee strings (naam en nummer). Alleen naam mag, alleen nummer niet.
				String streetName = "";
				String streetNumber = "";
				try {
					streetName = ((StringValue) arguments[0].next()).getStringValue();
					if (arguments.length > 1) {
						 streetNumber = arguments[1].next().getStringValue();
					}
				} catch (Exception e) {
					return Value.asIterator(EmptySequence.getInstance());
				}		
				String streetAddress = streetName;
				if (streetNumber.length() > 0) {
					streetAddress += " " + streetNumber;
				}
				return Value.asIterator(StringValue.makeStringValue(streetAddress));
			}
		};
	}
}
