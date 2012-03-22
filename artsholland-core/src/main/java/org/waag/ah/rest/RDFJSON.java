package org.waag.ah.rest;

import java.io.Writer;
import java.util.Iterator;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A utility class to help converting Sesame Graphs from and to RDF/JSON.
 *
 * @author Hannes Ebner <hebner@kth.se>
 * @author Joshua Shinavier 
 * @author Peter Ansell
 */
public class RDFJSON {

	private static final String STRING_GRAPHS = "graphs";
	private static final String STRING_URI = "uri";
	private static final String STRING_BNODE = "bnode";
	private static final String STRING_DATATYPE = "datatype";
	private static final String STRING_LITERAL = "literal";
	private static final String STRING_LANG = "lang";
	private static final String STRING_TYPE = "type";
	private static final String STRING_VALUE = "value";
	
	private static final Logger log = LoggerFactory.getLogger(RDFJSON.class);

    /**
     * Outputs an ordered set of Statements directly to JSON
     *
     * NOTE: The statements must be ordered by a comparator that orders null 
     * before other values, or the contexts may not be written correctly
     *
     * @param graph A Set of Statements that are preordered in the 
     * order subject>predicate>object>context so that it can be 
     * output directly without any further checks
     * @param writer The output writer to use
     * 
     * @return An RDF/JSON string if successful, otherwise null.
     */
    public static Writer graphToRdfJsonPreordered(Set<Statement> graph, Writer writer) 
    {
    	JSONObject result = new JSONObject();
        try 
        {
        	Resource lastSubject = null;
    		URI lastPredicate = null;
    		Value lastObject = null;
        	Resource lastContext = null;
        	
        	JSONObject predicateArray = new JSONObject();
        	JSONArray objectArray = new JSONArray();
        	JSONArray contextArray = new JSONArray();
        	
        	Iterator<Statement> iterator = graph.iterator();
        	
        	while(iterator.hasNext())
        	{
        	    Statement nextStatement = iterator.next();
        	    
				// Dump everything if the subject changes after the first iteration
				if(lastSubject != null && !nextStatement.getSubject().equals(lastSubject))
        		{
					//==================================================
					// Dump the last variables starting from the context
	    			// NOTE: This only works because StatementComparator orders nulls before all other values
	    			if(lastContext != null || contextArray.length() == 0)
	    			{
	    				contextArray.put(contextArray.length(), lastContext);
	    			}
    				addObjectToArray(lastObject, objectArray, contextArray);
                    predicateArray.put(lastPredicate.stringValue(), objectArray);
                    result.put(resourceToString(lastSubject), predicateArray);
					//==================================================
                    
					//==================================================
					// Recreate the relevant temporary objects, now that 
                    // they have been stored with the results
                    predicateArray = new JSONObject();
        			objectArray = new JSONArray();
        			contextArray = new JSONArray();
        			//==================================================

        			//==================================================
        			// Change all of the pointers for the last objects over
        			lastSubject = nextStatement.getSubject();
        			lastPredicate = nextStatement.getPredicate();
        			lastObject = nextStatement.getObject();
        			lastContext = nextStatement.getContext();
        			//==================================================
        		}
				else
				{
					lastSubject = nextStatement.getSubject();
					
	    			// Add the lastPredicate when it changes, as we know we have all of the objects and their related contexts for the last predicate now
					if(lastPredicate != null && !nextStatement.getPredicate().equals(lastPredicate))
	        		{
						//==================================================
						// Dump the last variables starting from the context
		    			// NOTE: This only works because StatementComparator orders nulls before all other values
		    			if(lastContext != null || contextArray.length() == 0)
		    			{
		    				contextArray.put(contextArray.length(), lastContext);
		    			}
	    				addObjectToArray(lastObject, objectArray, contextArray);
	                    predicateArray.put(lastPredicate.stringValue(), objectArray);
						//==================================================
	                    
						//==================================================
						// Recreate the relevant temporary objects, now that 
	                    // they have been stored with the last predicate
	                    objectArray = new JSONArray();
	                    contextArray = new JSONArray();
						//==================================================
	                    
						//==================================================
	        			// Change the relevant pointers for the last objects over
		    			lastPredicate = nextStatement.getPredicate();
		    			lastObject = nextStatement.getObject();
		    			lastContext = nextStatement.getContext();
						//==================================================
	        		}
					else
					{
		    			lastPredicate = nextStatement.getPredicate();
		    			
		    			// Add the lastObject to objectArray when it changes, as we know we have all of the contexts for the object then
		    			if(lastObject != null && !nextStatement.getObject().equals(lastObject))
		    			{
							//==================================================
							// Dump the last variables starting from the context
		        			// NOTE: This only works because StatementComparator orders nulls before all other values
			    			if(lastContext != null || contextArray.length() == 0)
			    			{
								// Add the lastContext to contextArray
			    				contextArray.put(contextArray.length(), lastContext);
			    			}
			    			addObjectToArray(lastObject, objectArray, contextArray);
							//==================================================
		
							//==================================================
							// Recreate the temporary context array, now that 
		                    // they have been stored with the last object
		                    contextArray = new JSONArray();
							//==================================================

							//==================================================
		        			// Change the relevant pointers for the last objects over
			    			lastObject = nextStatement.getObject();
			    			lastContext = nextStatement.getContext();
							//==================================================
		    			}
		    			else
		    			{
			    			lastObject = nextStatement.getObject();
			    			
			    			// add the next context for the current object
		    				contextArray.put(contextArray.length(), lastContext);
			    			
			    			lastContext = nextStatement.getContext();
		    			}
					}
				}
        	}

			// the last subject/predicate/object/context will never get pushed inside the loop above, so push it here if we went into the loop
    		if(graph.size() > 0)
    		{
    			// NOTE: This only works because StatementComparator orders nulls before all other values
    			if(lastContext != null || contextArray.length() == 0)
    			{
    				contextArray.put(contextArray.length(), lastContext);
    			}
    			addObjectToArray(lastObject, objectArray, contextArray);
                predicateArray.put(lastPredicate.stringValue(), objectArray);
        		result.put(resourceToString(lastSubject), predicateArray);
    		}
    		
            result.write(writer);
            
            return writer;
	    } 
        catch (JSONException e) 
        {
	        log.error(e.getMessage(), e);
	    }
	    return null;
	}
    
    /**
     * Returns the correct syntax for a Resource, 
     * depending on whether it is a URI or a Blank Node (ie, BNode)
     * 
     * @param uriOrBnode The resource to serialise to a string
     * @return The string value of the sesame resource
     */
    private static String resourceToString(Resource uriOrBnode)
    {
    	if(uriOrBnode instanceof URI)
    	{
    		return uriOrBnode.stringValue();
    	}
    	else
    	{
    		return "_:" + ((BNode)uriOrBnode).getID();
    	}
    }
    
    /**
     * Helper method to reduce complexity of the JSON serialisation algorithm
     * 
     * Any null contexts will only be serialised to JSON if there are also non-null contexts in the contexts array
     * 
     * @param object The RDF value to serialise
     * @param valueArray The JSON Array to serialise the object to
     * @param contexts The set of contexts that are relevant to this object, including null contexts as they are found.
     * @throws JSONException
     */
	private static void addObjectToArray(Value object, JSONArray valueArray, JSONArray contexts) throws JSONException
	{
		JSONObject valueObj = new JSONObject();
		
		if (object instanceof Literal) 
		{
			valueObj.put(RDFJSON.STRING_VALUE, object.stringValue());
			
		    valueObj.put(RDFJSON.STRING_TYPE, RDFJSON.STRING_LITERAL);
		    Literal l = (Literal) object;
		    
		    if (l.getLanguage() != null) 
		    {
		        valueObj.put(RDFJSON.STRING_LANG, l.getLanguage());
		    } 
		    else if (l.getDatatype() != null) 
		    {
		        valueObj.put(RDFJSON.STRING_DATATYPE, l.getDatatype().stringValue());
		    }
		} 
		else if (object instanceof BNode) 
		{
			valueObj.put(RDFJSON.STRING_VALUE, resourceToString((BNode)object));
			
		    valueObj.put(RDFJSON.STRING_TYPE, RDFJSON.STRING_BNODE);
		} 
		else if (object instanceof URI) 
		{
			valueObj.put(RDFJSON.STRING_VALUE, resourceToString((URI)object));

			valueObj.put(RDFJSON.STRING_TYPE, RDFJSON.STRING_URI);
		}

		// net.sf.json line
		//		if (contexts.size() > 0 && !(contexts.size() == 1 && contexts.contains(null)))
		// org.json line
		//		if(contexts.length() > 0 && !(contexts.length() == 1 && contexts.isNull(0)))
		
		// if there is a context, and null is not the only context, 
		// then, output the contexts for this object
		if(contexts.length() > 0 && !(contexts.length() == 1 && contexts.isNull(0)))
		{
			valueObj.put(RDFJSON.STRING_GRAPHS, contexts);
		}
		valueArray.put(valueObj);
    }
}
