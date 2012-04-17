package org.waag.ah.spring.service;

import javax.ejb.EJB;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.repository.sail.SailRepositoryConnection;
import org.openrdf.rio.RDFFormat;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waag.ah.QueryService;
import org.waag.ah.bigdata.OpenSaharaTest;
import org.waag.ah.rdf.RDFJSONFormat;
import org.waag.ah.rdf.RDFWriterConfig;
import org.waag.ah.rdf.RdfQueryDefinition;
import org.waag.ah.rest.RESTParameters;
import org.waag.ah.rest.model.RestRelation;
import org.waag.ah.rest.model.RestRelation.RelationQuantity;
import org.waag.ah.rest.model.RestRelation.RelationType;
import org.waag.ah.rest.util.RestRelationQueryTaskGenerator;

@Service("geoService")
public class GeoService implements InitializingBean {


	
	@EJB(mappedName="java:app/datastore/OpenSaharaTest")
	private OpenSaharaTest koek;
	
	private SailRepositoryConnection conn;

	
	@Override
	public void afterPropertiesSet() throws Exception {
			conn = koek.getConnection();
	}

	public String getVis() {
		// TODO Auto-generated method stub
		return "Hondje";	
		
	}
}
