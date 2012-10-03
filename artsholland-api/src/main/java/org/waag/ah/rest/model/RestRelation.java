package org.waag.ah.rest.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.waag.ah.PlatformConfigHelper;
import org.waag.ah.rest.RestParameters;

public class RestRelation {
		
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

	List<RestRelation> relations = new ArrayList<RestRelation>();
	
	Map<String, SPARQLFilter> filters = new HashMap<String, SPARQLFilter>();
	
	private RestRelation parent = null;
	
//	private String classUri;
	private String objectUri;
	
	public RestRelation() {
		this.parameter = null;
		this.classURI = null;
		this.quantity = null;
		this.type = null;
		this.parameterized = false;
		

		PropertiesConfiguration config;
		try {
			config = PlatformConfigHelper.getConfig();
//			this.classUri = config.getString("platform.classUri");
			this.objectUri = config.getString("platform.objectUri");
		} catch (ConfigurationException e) {
			// TODO: add catch code
		} 
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
		return /*CLASS_NAMESPACE +*/ classURI;
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
		if (objectUri.endsWith("/")) {
			return objectUri.substring(0, objectUri.length() - 1);
		}
		return objectUri;
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
	
	public void addFilter(SPARQLFilter filter) {
		filters.put(filter.getParameter(), filter);		
	}
	
	public SPARQLFilter findFilter(String parameter) {
		return filters.get(parameter);				
	}

	
	public void addRelation(RestRelation relation) {
		relation.setParent(this);
		relations.add(relation);
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
		
		for (Map.Entry<String, String[]> uriParameter : params.getURIParameterMap().entrySet()) {
			String parameter = uriParameter.getKey();
			SPARQLFilter filter = findFilter(parameter);
			if (filter != null) {					
				String[] value = uriParameter.getValue();
				String statementString = null;
				
				if (value.length > 0) {
					statementString = filter.getStatement(value[0], params.getURIParameterMap());
				} else {
					statementString = filter.getStatement();
				}
				
				if (statementString != null & statementString.length() > 0) {
					statements.add(statementString);
				}
			}
		}
			
		return statements;
	}


	public ArrayList<String> getFilters(RestParameters params) {
		ArrayList<String> filters = new ArrayList<String>();
		
		for (Map.Entry<String, String[]> uriParameter : params.getURIParameterMap().entrySet()) {
			String parameter = uriParameter.getKey();			
			SPARQLFilter filter = findFilter(parameter);
			if (filter != null) {
				String[] value = uriParameter.getValue();
				String filterString = null;
				
				if (value.length > 0) {
					filterString = filter.getFilter(value[0], params.getURIParameterMap());					
				}	else {
					filterString = filter.getFilter();
				}
				
				if (filterString != null & filterString.length() > 0) {
					filters.add(filterString);
				}				
			}
		}
			
		return filters;
	}
	
}
