package org.waag.rdf.sesame;

import java.io.IOException;
import java.util.Map;
import java.util.Stack;

import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.waag.rdf.AHRDFNamespaces;

import com.google.gson.stream.JsonWriter;

/**
 * A utility class to help converting Sesame Graphs from and to Arts Holland
 * JSON.
 * 
 * @author Hannes Ebner <hebner@kth.se>
 * @author Joshua Shinavier
 * @author Peter Ansell
 * @author Bert Spaan
 */
public class RDFGSON {
	
	// TODO: read namespaces from properties file.
	private static final Map<String, String> NAMESPACES = AHRDFNamespaces.getNamespaces();

//	private static final String STRING_GRAPHS = "graphs";
	private static final String STRING_URI = "uri";
//	private static final String STRING_BNODE = "bnode";
//	private static final String STRING_DATATYPE = "datatype";
//	private static final String STRING_LITERAL = "literal";
//	private static final String STRING_LANG = "lang";
//	private static final String STRING_TYPE = "type";
//	private static final String STRING_VALUE = "value";

	// Instance: e.g. ah:Venue, RDF subject. 
	private Resource lastInstance = null;

	// Name: e.g. ah:cidn, RDF predicate.
	private URI lastName = null;
	
	// Value: e.g. literal, RDF object.
	private Value lastValue = null;
	
	enum StackItem {
    VALUE_ARRAY,
    INSTANCE
  };
  
	private final Stack<StackItem> stack = new Stack<StackItem>();

	private JsonWriter jsonWriter;

	public RDFGSON(JsonWriter jsonWriter) {
		this.jsonWriter = jsonWriter;
	}
	
	/**
	 * Returns the correct syntax for a Resource, depending on whether it is a URI
	 * or a Blank Node (ie, BNode)
	 * 
	 * @param uriOrBnode
	 *          The resource to serialise to a string
	 * @return The string value of the sesame resource
	 */
	private static String resourceToString(Resource uriOrBnode) {
		if (uriOrBnode instanceof URI) {
			return uriOrBnode.stringValue();
		} else {
			return "_:" + ((BNode) uriOrBnode).getID();
		}
	}
	
	private void writeValue(Value value) throws IOException {
		if (value instanceof Literal) {	
				
	    //Literal l = (Literal) value;	    
	    //if (l.getLanguage() != null) 
	    //{
	    //		if (l.getLanguage().equals(languageTag)) {
	    //			jsonWriter.value(value.stringValue());
	    //		}
	    //} else {
	    	jsonWriter.value(value.stringValue());
	    //}
		} else if (value instanceof BNode) {			
			jsonWriter.value(resourceToString((BNode) value));			
		} else if (value instanceof URI) {			
			jsonWriter.value(resourceToString((URI) value));
		}		
	}
	
	private void writeName(URI name) throws IOException {
		String uri = resourceToString(name);
		for (Map.Entry<String, String> namespace : NAMESPACES.entrySet()) {			
			if (uri.startsWith(namespace.getValue())) {
				uri = /*namespace.getKey() + ":" + */uri.substring(namespace.getValue().length());
				break;
			}
		}
		jsonWriter.name(uri);		
	}
	
	private void beginInstance(Resource instance) throws IOException {
		stack.push(StackItem.INSTANCE);
		jsonWriter.beginObject();
		jsonWriter.name(STRING_URI);
		jsonWriter.value(resourceToString(instance));
	}
	
	public void end() throws IOException  {
		if (lastInstance != null && stack.isEmpty()) {
			// Stack is empty, but last triple is not written.
			// Write new subject.			
			
			beginInstance(lastInstance);			
			writeName(lastName);			
		}
		
		if (lastValue != null) {
			writeValue(lastValue);	
		}
		
		clearStack();		
	}	
	
	public void writeStatement(Statement statement) throws IOException {

		if (lastInstance != null && !statement.getSubject().equals(lastInstance)) {			
			// Subject differs. Close last JSON object.
			writeValue(lastValue);
			clearStack();

			lastInstance = statement.getSubject();
			lastName = statement.getPredicate();
			lastValue = statement.getObject();
			
			beginInstance(lastInstance);			
			writeName(lastName);

		} else {
			lastInstance = statement.getSubject();

			if (lastName != null	&& !statement.getPredicate().equals(lastName)) {
				// Predicate and object differ
				
				writeValue(lastValue);
				if (stack.peek().equals(StackItem.VALUE_ARRAY)) {					
					jsonWriter.endArray();
					stack.pop();					
				}
				
				lastName = statement.getPredicate();
				lastValue = statement.getObject();
				
				writeName(lastName);
				
			} else {
				lastName = statement.getPredicate();

				if (lastValue != null && !statement.getObject().equals(lastValue)) {
					// Only object differs, but predicate is the same
					if (!stack.peek().equals(StackItem.VALUE_ARRAY)) {
						stack.push(StackItem.VALUE_ARRAY);
						jsonWriter.beginArray();
					}
					writeValue(lastValue);					
	
					lastValue = statement.getObject();	
				} else {
					// First call only.
					lastValue = statement.getObject();
					
					beginInstance(lastInstance);			
					writeName(lastName);				
				}
			}
		}
	}

	private void clearStack() throws IOException {
		while (!stack.isEmpty()) {
			StackItem stackItem = stack.pop();
			if (stackItem.equals(StackItem.VALUE_ARRAY)) {
				jsonWriter.endArray();
			} else if (stackItem.equals(StackItem.INSTANCE)) {
				jsonWriter.endObject();
			}			
		}		
	}

}
