package org.waag.ah.rest;

import java.io.IOException;
import java.util.Stack;

import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
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

//	private static final String STRING_GRAPHS = "graphs";
	private static final String STRING_URI = "uri";
//	private static final String STRING_BNODE = "bnode";
//	private static final String STRING_DATATYPE = "datatype";
//	private static final String STRING_LITERAL = "literal";
//	private static final String STRING_LANG = "lang";
//	private static final String STRING_TYPE = "type";
//	private static final String STRING_VALUE = "value";

	private Resource lastSubject = null;
	private URI lastPredicate = null;
	private Value lastObject = null;
	
	enum StackItem {
    ARRAY,
    OBJECT
  };
	
	private final Stack<StackItem> stack = new Stack<StackItem>();

	private JsonWriter jsonWriter;

	public RDFGSON(JsonWriter jsonWriter) {
		this.jsonWriter = jsonWriter;
		this.jsonWriter.setIndent("  ");
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
	
	private void writeNameValue(URI predicate, Value object) throws IOException {			
		writeName(predicate);
		writeValue(object);
	}
	
	private void writeValue(Value object) throws IOException {
		if (object instanceof Literal) {			
			jsonWriter.value(object.stringValue());
		} else if (object instanceof BNode) {			
			jsonWriter.value(resourceToString((BNode) object));			
		} else if (object instanceof URI) {			
			jsonWriter.value(resourceToString((URI) object));
		}		
	}
	
	private void writeName(URI predicate) throws IOException {
		jsonWriter.name(resourceToString(predicate));		
	}

	
	public void end() throws IOException  {
		if (stack.isEmpty()) {
			/// Stack is empty, but last triple is not written.
			// Write new subject.			
			
			jsonWriter.beginObject();			
			jsonWriter.name(STRING_URI);
			jsonWriter.value(resourceToString(lastSubject));
			writeNameValue(lastPredicate, lastObject);
			
		} else if (stack.peek().equals(StackItem.ARRAY)) {
			
			writeValue(lastObject);			
			
		} else if (stack.peek().equals(StackItem.OBJECT)) {
			
			writeNameValue(lastPredicate, lastObject);
			
		}
		
		clearStack();
		
	}
	
	//jsonWriter.name(STRING_URI);
	//jsonWriter.value(resourceToString(lastSubject));

	//jsonWriter.name(STRING_URI);
	//jsonWriter.value(resourceToString(lastSubject));
	
	public void writeStatement(Statement statement) throws IOException {

		if (lastSubject != null && !statement.getSubject().equals(lastSubject)) {			
			// Subject differs. Close last JSON object.
			
			clearStack();

			lastSubject = statement.getSubject();
			lastPredicate = statement.getPredicate();
			lastObject = statement.getObject();
			
			jsonWriter.beginObject();
			stack.push(StackItem.OBJECT);			

		} else {
			lastSubject = statement.getSubject();

			if (lastPredicate != null	&& !statement.getPredicate().equals(lastPredicate)) {
				// Predicate and object differ
				writeName(lastPredicate);
				if (stack.peek().equals(StackItem.ARRAY)) {
					jsonWriter.endArray();
					stack.pop();
				}
				writeValue(lastObject);
				
				
				lastPredicate = statement.getPredicate();
				lastObject = statement.getObject();
				//writeNameValue(lastPredicate, lastObject);
				
			} else {
				lastPredicate = statement.getPredicate();

				if (lastObject != null && !statement.getObject().equals(lastObject)) {
					// Only object differs, but predicate is the same
					if (!stack.peek().equals(StackItem.ARRAY)) {
						stack.push(StackItem.ARRAY);
						jsonWriter.beginArray();
					}
					writeValue(lastObject);
					
	
					lastObject = statement.getObject();			
					//writeNameValue(lastPredicate, lastObject);
				} else {
					// First call only.
					lastObject = statement.getObject();
					
					stack.push(StackItem.OBJECT);
					jsonWriter.beginObject();
					
				}
			}
		}
	}

	private void clearStack() throws IOException {
		while (!stack.isEmpty()) {
			StackItem stackItem = stack.pop();
			if (stackItem.equals(StackItem.ARRAY)) {
				jsonWriter.endArray();
			} else if (stackItem.equals(StackItem.OBJECT)) {
				jsonWriter.endObject();
			}			
		}		
	}
}
