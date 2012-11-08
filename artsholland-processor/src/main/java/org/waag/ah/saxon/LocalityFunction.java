package org.waag.ah.saxon;

import org.waag.ah.util.postalcode.PostalCodeLookup;

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
public class LocalityFunction extends ExtensionFunctionDefinition {

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
		return new StructuredQName("waag", "http://waag.org/saxon-extension", "locality");
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
				String locality = "";
				try {
					// First argument: postal code
					// Second (optional) argument: locality (ignored for now)
					
					String postalCode = ((StringValue) arguments[0].next()).getStringValue();
					locality = PostalCodeLookup.lookupPostalCode(postalCode);
				} catch (Exception e) {
					return Value.asIterator(EmptySequence.getInstance());
				}				
				return Value.asIterator(StringValue.makeStringValue(locality));
			}
		};
	}
}
