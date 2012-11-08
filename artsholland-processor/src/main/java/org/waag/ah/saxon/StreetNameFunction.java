package org.waag.ah.saxon;

import java.util.Arrays;
import java.util.List;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.EmptySequence;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.value.StringValue;
import net.sf.saxon.value.Value;

@SuppressWarnings("serial")
public class StreetNameFunction extends ExtensionFunctionDefinition {

	@Override
	public SequenceType[] getArgumentTypes() {
		return new SequenceType[] { SequenceType.OPTIONAL_STRING };
	}

	@Override
	public StructuredQName getFunctionQName() {
		return new StructuredQName("waag", "http://waag.org/saxon-extension",
				"street-name");
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
				// grijpt nummer uit string en plakt eventuele toevoeging eraan vast
				String streetName = "";
				try {
					streetName = ((StringValue) arguments[0].next()).getStringValue();
					streetName = sanitizeStreetName(streetName);
				} catch (Exception e) {
					return Value.asIterator(EmptySequence.getInstance());
				}
				if (streetName.length() == 0) {
					return Value.asIterator(EmptySequence.getInstance());
				}
				return Value.asIterator(StringValue.makeStringValue(streetName));
			}
		};
	}

	public String sanitizeStreetName(String streetName) {
		streetName = streetName
				.trim()
				.replaceAll("\\s+"," ")
				//.replaceAll(/(\d+)\s+\1+/,'\1')
				//.replaceAll(/(\d+)[-\s]*([A-Za-z]+)\s*$/) {|c| $1+$2.downcase}
				.replaceAll("(?i) VAN ", " van ")
				.replaceAll("(?i) V. ", " van ")
				.replaceAll("(?i) V ", " van ")
				.replaceAll("(\\d+)\\s*-\\s*(\\d+)", "$1-$2")
				//.replaceAll("str.", "straat")
				//.replaceAll("str(\\W)", "straat$1")
				//.replaceAll("ln(\\W)", "laan$1")
				.replaceAll("(?i)burg\\\\.?\\s", "Burgemeester ")
				.replaceAll("(?i)ds\\\\.?\\s", "Dominee ")
				.replaceAll("\\s+(plein|kade|laan)", "$1");
				
		return streetName;
	}
}
