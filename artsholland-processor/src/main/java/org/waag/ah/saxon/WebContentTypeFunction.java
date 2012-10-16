package org.waag.ah.saxon;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
public class WebContentTypeFunction extends ExtensionFunctionDefinition {

	private static final Map<String, String> TYPES;
  static {
      Map<String, String> types = new HashMap<String, String>();
      types.put("article", "Article");
      types.put("blog", "Blog");
      types.put("editorial", "Editorial");
      types.put("hotel", "Hotel");
      types.put("locatie", "Venue");
      types.put("press_release", "Press Release");
      types.put("productie", "Production");
      types.put("restaurant", "Restaurant");
      types.put("route", "Route");
      types.put("shop", "Shop");
      
      TYPES = Collections.unmodifiableMap(types);
  }	
	
	@Override
	public SequenceType[] getArgumentTypes() {
		return new SequenceType[] { SequenceType.OPTIONAL_STRING };
	}

	@Override
	public StructuredQName getFunctionQName() {
		return new StructuredQName("waag", "http://waag.org/saxon-extension",
				"web-content-type");
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
				String type = "";
				try {
					type = ((StringValue) arguments[0].next()).getStringValue();

					// Rewrite TAM export types					
					if (TYPES.containsKey(type)) {
						String newType = TYPES.get(type);
						return Value.asIterator(StringValue.makeStringValue(newType));
					} else {
						return Value.asIterator(EmptySequence.getInstance());
					}
				} catch (Exception e) {
					return Value.asIterator(EmptySequence.getInstance());
				}				
			}
		};
	}
}
