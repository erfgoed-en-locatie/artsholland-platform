package org.waag.ah.saxon;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.SequenceType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class DebugValue extends ExtensionFunctionDefinition {
	private Logger logger = LoggerFactory.getLogger(DebugValue.class);
	
	@Override
	public SequenceType[] getArgumentTypes() {
		return new SequenceType[] {SequenceType.ANY_SEQUENCE};
	}

	@Override
	public StructuredQName getFunctionQName() {
		return new StructuredQName("waag", "http://waag.org/saxon-extension", "debug");
	}

	@Override
	public SequenceType getResultType(SequenceType[] suppliedArgumentTypes) {
		return SequenceType.EMPTY_SEQUENCE;
	}

	@Override
	public ExtensionFunctionCall makeCallExpression() {
        return new ExtensionFunctionCall() {
            public SequenceIterator call(SequenceIterator[] arguments, XPathContext context) throws XPathException {
            	logger.info(arguments[0].next().getStringValue());
            	return null;
            }
        };
	}
}
