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
public class StreetNumberFunction extends ExtensionFunctionDefinition {

	@Override
	public SequenceType[] getArgumentTypes() {
		return new SequenceType[] { SequenceType.OPTIONAL_STRING };
	}
	
	@Override
	public int getMaximumNumberOfArguments() {
		return 2;
	}

	@Override
	public StructuredQName getFunctionQName() {
		return new StructuredQName("waag", "http://waag.org/saxon-extension",
				"street-number");
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
				String number = "";
				String addition = "";
				try {
					number = ((StringValue) arguments[0].next()).getStringValue();
					if (arguments.length > 1) {
						 addition = arguments[1].next().getStringValue();
					}
				} catch (Exception e) {
					return Value.asIterator(EmptySequence.getInstance());
				}		
				String streetNumber = number;
				if (addition.length() > 0) {
					streetNumber += addition;
				}
				if (streetNumber.length() == 0) {
					return Value.asIterator(EmptySequence.getInstance());
				}
				return Value.asIterator(StringValue.makeStringValue(sanitizeStreetNumber(streetNumber)));
			}
		};
	}
	
	public String sanitizeStreetNumber(String streetNumber) {
		streetNumber = streetNumber
				.trim()
				.replaceAll("\\s+"," ")
				//.replaceAll(".0", "")
				.replaceAll("(\\d+)\\s+\\1+", "\\1")
				//.replaceAll(/(\d+)[-\s]*([A-Za-z]+)\s*$/) {|c| $1+$2.downcase}
				.replaceAll("(\\d+)\\s*-\\s*(\\d+)", "$1-$2");
		
		if (streetNumber.endsWith(".0")) {
			streetNumber = streetNumber.substring(0, streetNumber.length() - 2);
		}
		
		return streetNumber;
	}
}