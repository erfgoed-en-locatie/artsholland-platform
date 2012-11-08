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
public class AddressUriFunction extends ExtensionFunctionDefinition {

	@Override
	public SequenceType[] getArgumentTypes() {
		return new SequenceType[] { SequenceType.OPTIONAL_STRING };
	}
	
	@Override
	public int getMaximumNumberOfArguments() {
		return 3;
	}

	@Override
	public StructuredQName getFunctionQName() {
		return new StructuredQName("waag", "http://waag.org/saxon-extension", "address-uri");
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
				String postalCode = "";
				String number = "";
				String addition = "";				
				try {					
					postalCode = ((StringValue) arguments[0].next()).getStringValue();
					number = ((StringValue) arguments[1].next()).getStringValue();
					if (arguments.length == 3) {
						addition = ((StringValue) arguments[2].next()).getStringValue();	
					}		
				} catch (Exception e) {
					return Value.asIterator(EmptySequence.getInstance());
				}				
				if (postalCode.length() > 0 && number.length() > 0) {
					//addition may be empty sequence					
					String address = postalCode + number + addition;
					address = address.replaceAll("\\s","");						
					return Value.asIterator(StringValue.makeStringValue(address));
				} else {
					return Value.asIterator(EmptySequence.getInstance());
				}
			}
		};
	}
}
