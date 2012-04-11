package org.waag.ah.rest.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.waag.ah.rest.RestParameters;

public class RestRelation {

	// TODO: get from properties
	public static String OBJECT_NAMESPACE ="http://data.artsholland.com/";
	public static String CLASS_NAMESPACE = "http://purl.org/artsholland/1.0/";
	
	public static enum RelationQuantity {
		SINGLE, MULTIPLE
	};
  
	public static enum RelationType {
		SELF, FORWARD, BACKWARD, BACKWARDFORWARD
	};
	
	private String parameter;	
	private String classURI;
	private String objectURI;
	private RelationQuantity quantity;
	private RelationType type;  
	
	private boolean parameterized;

	// TODO: make map? parameter as key?
	List<RestRelation> relations = new ArrayList<RestRelation>();
	
	Map<String, SPARQLFilter> filters = new HashMap<String, SPARQLFilter>();
	
	private RestRelation parent = null;

	public RestRelation() {
		
	}
	
	public RestRelation(String parameter, String classURI, RelationQuantity quantity, RelationType type, boolean parameterized) {
		this.parameter = parameter;
		this.classURI = classURI;
		this.quantity = quantity;
		this.type = type;
		this.parameterized = parameterized;
	}
	
	public RestRelation(String parameter, String classURI, RelationQuantity quantity, RelationType type, boolean parameterized, RestRelation parent) {
		this.parameter = parameter;
		this.classURI = classURI;
		this.quantity = quantity;
		this.type = type;
		this.parent = parent;		
		this.parameterized = parameterized;
	}
	
	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	// TODO: include namespace parameter
	public String getClassURI() {
		return CLASS_NAMESPACE + classURI;
	}

	public void setClassURI(String classURI) {
		this.classURI = classURI;
	}
	
	public String getObjectURI() {
		return objectURI;
	}
	
	public String getObjectURI(LinkedList<String> parameters, int i) {
		if (parent != null) {
			if (parameterized) {
				return parent.getObjectURI(parameters, i - 1) + "/" + parameters.get(i);	
			} else {
				return parent.getObjectURI(parameters, i - 1) + "/" + parameter;
			}
		}
		if (OBJECT_NAMESPACE.endsWith("/")) {
			return OBJECT_NAMESPACE.substring(0, OBJECT_NAMESPACE.length() - 1);
		}
		return OBJECT_NAMESPACE;
	}

	public void setObjectURI(String objectURI) {
		this.objectURI = objectURI;
	}

	public RelationQuantity getQuantity() {
		return quantity;
	}

	public void setQuantity(RelationQuantity quantity) {
		this.quantity = quantity;
	}

	public RelationType getType() {
		return type;
	}

	public void setType(RelationType type) {
		this.type = type;
	}
	
	public RestRelation getParent() {
		return parent;
	}

	public void setParent(RestRelation parent) {
		this.parent = parent;
	}
	
	public boolean isParameterized() {
		return parameterized;
	}

	public void setParameterized(boolean parameterized) {
		this.parameterized = parameterized;
	}
	
	public void addRelation(RestRelation relation) {
		relation.setParent(this);
		relations.add(relation);
	}
	
	public void addRelations(RestRelation... relations) {
		for (RestRelation relation : relations) {
			relation.setParent(this);
			this.relations.add(relation);
		}
	}
	
	public RestRelation addRelation(String parameter, String classURI,
			RelationQuantity quantity, RelationType type, boolean parameterized) {
		RestRelation relation = new RestRelation(parameter, classURI, quantity,
				type, parameterized, this);
		relations.add(relation);
		return relation;
	}
	
	public List<RestRelation> getRelations() {
		return relations;
	}
	
	public RestRelation findRelation(LinkedList<String> parameters) {
		return findRelation(parent, relations, parameters, 0);
	}
	
	public void addFilter(SPARQLFilter filter) {
		filters.put(filter.getParameter(), filter);		
	}
	
	public SPARQLFilter findFilter(String parameter) {
		return filters.get(parameter);				
	}

	private RestRelation findRelation(RestRelation parent,
			List<RestRelation> relations, LinkedList<String> parameters, int i) {
		String parameter = null;
		
		if (i >= 0 && i < parameters.size()) {
			parameter = parameters.get(i);
		}
		
		if (parameter != null) {
			
			RestRelation relation = null;
			boolean found = false;
			
			Iterator<RestRelation> iter = relations.iterator();
			
			while (!found && iter.hasNext()) {
				relation = iter.next();
				if (parameter.equals(relation.getParameter()) && !relation.isParameterized()) {
					found = true;
				}				
			}
			
			iter = relations.iterator();
			while (!found && iter.hasNext()) {
				relation = iter.next();
				if (relation.isParameterized()) {	
					found = true;
				}				
			}
			
			if (found && relation != null) {
				return relation.findRelation(relation.parent, relation.getRelations(), parameters, i + 1);
			} else {
				return null;
			}
		}
		return this;
	}

	public ArrayList<String> getStatements(RestParameters params) {
		
		ArrayList<String> statements = new ArrayList<String>();
		
		for (Entry<String, String[]> uriParameter : params.getURIParameterMap().entrySet()) {
			String parameter = uriParameter.getKey();
			SPARQLFilter filter = findFilter(parameter);
			if (filter != null) {
				statements.add(filter.getStatement());
			}
		}
			
		return statements;
	}

	public ArrayList<String> getFilters(RestParameters params) {
		ArrayList<String> filters = new ArrayList<String>();
		
		for (Entry<String, String[]> uriParameter : params.getURIParameterMap().entrySet()) {
			String parameter = uriParameter.getKey();			
			SPARQLFilter filter = findFilter(parameter);
			if (filter != null) {
				String[] value = uriParameter.getValue();
				if (value.length > 0) {
					filters.add(filter.getFilter(value[0]));
				}				
			}
		}
			
		return filters;
	}

	
}
