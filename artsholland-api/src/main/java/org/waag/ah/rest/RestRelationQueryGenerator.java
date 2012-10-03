package org.waag.ah.rest;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryLanguage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.rdf.RdfQueryDefinition;
import org.waag.ah.rest.model.RestRelation;
import org.waag.ah.rest.model.RestRelation.RelationQuantity;
import org.waag.ah.rest.model.RestRelation.RelationType;
import org.waag.ah.rest.model.SPARQLQuery;

public class RestRelationQueryGenerator {
	final static Logger logger = LoggerFactory.getLogger(RestRelationQueryGenerator.class);
	/*
	 * Check with http://localhost:8080/rest/venues/bf23f0c6-5c54-4d18-a0e5-35d1dc140508
	 * (Paradiso Amsterdam)
	 */	

	private RestRelation rootRelation;	
	
	private static final SPARQLQuery querySingleSelf = new SPARQLQuery(true, "?object ?p ?o.", "?object a ?class.");
	private static final SPARQLQuery queryMultipleSelf = new SPARQLQuery("?object a ?class.");		
	private static final SPARQLQuery queryMultipleForward = new SPARQLQuery("?this ?p2 ?object.", "?this a ?class.", "?object a ?linkedClass.");		
	private static final SPARQLQuery queryMultipleBackward = new SPARQLQuery("?object ?p2 ?this.", "?this a ?class.", "?object a ?linkedClass.");			 
	
	private static final SPARQLQuery queryMultipleBackwardForward = new SPARQLQuery("?i ?p2 ?this.", "?i ?p3 ?object.", "?this a ?class.", "?object a ?linkedClass.");
//private static final SPARQLQuery queryMultipleBackwardForward = new SPARQLQuery("?i ?p2 ?object.", "?i ?p3 ?s.", "?object a ?class.", "?s a ?linkedClass.");
	
	public RestRelationQueryGenerator(RestRelation rootRelation) {
		this.rootRelation = rootRelation;
	}	
	
	public RdfQueryDefinition generate(RestParameters params)
			throws MalformedQueryException {

		LinkedList<String> uriPathParts = params.getURIPathParts();
		RestRelation relation = rootRelation.findRelation(uriPathParts);
		
		if (relation == null) {
			throw new MalformedQueryException();
		}
		
		RelationType type = relation.getType();
		RelationQuantity quantity = relation.getQuantity();
		
		boolean ordered = params.isOrdered();
		boolean calculateCount = /*(params.getPage() == 1) &&*/ params.isCountTotals() && (quantity == RelationQuantity.MULTIPLE);
	
		SPARQLQuery query = null;
		
		Map<String, String> bindings = new HashMap<String, String>();
				
		if (type == RelationType.SELF) {
			
			if (quantity == RelationQuantity.SINGLE) {
				query = querySingleSelf;
				bindings.put("object", relation.getObjectURI(uriPathParts, uriPathParts.size() - 1));
				bindings.put("class", relation.getClassURI());
			} else if (relation.getQuantity() == RelationQuantity.MULTIPLE) {
				query = queryMultipleSelf;
				bindings.put("class", relation.getClassURI());			
			}
			
		} else { 
			
			if (relation.getParent() != null) {
			
				String objectURI = relation.getParent().getObjectURI(uriPathParts, uriPathParts.size() - 2);
				String classURI = relation.getParent().getClassURI();
				String linkedClassURI = relation.getClassURI();
				
				if (type == RelationType.FORWARD) {
					query = queryMultipleForward;
					/*
					if (quantity == RelationQuantity.SINGLE) {					
					} else if (quantity == RelationQuantity.MULTIPLE) {										
					}*/
				} else if (type == RelationType.BACKWARD) {					
					query = queryMultipleBackward;				
				} else if (type == RelationType.BACKWARDFORWARD) {					
					query = queryMultipleBackwardForward;				
				}
				
				bindings.put("this", objectURI);
				bindings.put("class", classURI);
				bindings.put("linkedClass", linkedClassURI);
			}
		}
		
		if (query == null) {
			throw new MalformedQueryException();
		}
					
		String construct = query.generateContruct(relation, params, bindings, true, ordered);
		String count = calculateCount ? query.generateCount(relation, params, bindings, true) : null;
		
		logger.info(construct);
		
		return new RdfQueryDefinition(
				QueryLanguage.SPARQL,	construct, count, quantity == RelationQuantity.SINGLE);
	}
	
}
