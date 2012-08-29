package org.waag.ah.spring.service;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.openrdf.model.Statement;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.waag.ah.RepositoryConnectionFactory;
import org.waag.ah.bigdata.BigdataConnectionService;
import org.waag.ah.exception.ConnectionException;
import org.waag.ah.model.PrivateObject;
import org.waag.ah.spring.model.PrivateObjectImpl;
import org.waag.ah.spring.util.ApiResult;
import org.waag.ah.spring.util.ApiResult.ApiResultType;

@Service("privateObjectService")
public class PrivateObjectService extends MongoService<PrivateObject>  {

	@EJB(mappedName="java:app/datastore/BigdataConnectionService")
	private RepositoryConnectionFactory cf;
	
	private RepositoryConnection conn;
	private ValueFactory vf;
	
	@Autowired
	private PropertiesConfiguration platformConfig;	
	
	private String platformUri;
	private String classUri;
	
	@PostConstruct
	@SuppressWarnings("unchecked")
	public void postConstruct() {
		super.postConstruct();
		platformUri = platformConfig.getString("platform.platformUri");
		classUri = platformConfig.getString("platform.classUri");
		//objectUri = platformConfig.getString("platform.objectUri");
		
		try {
			conn = cf.getConnection();
			vf = conn.getValueFactory();
		} catch (ConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// TODO: create MongoCRUDService (copy from snelkassa branch)
	public ApiResult create(PrivateObjectImpl privateObject) {
		
		if (conn != null && vf != null) {
		
		String subject = privateObject.getUri();
		String predicate = classUri + "privateObject";
		String object = platformUri + "/private/object/" + privateObject.getId();
		//http://localhost:8080/private/object/d17ff653-ebdb-473f-a5f9-15a0aea03eb7
		
		// TODO: check if subject exists
		// TODO: only add if insert in Mongo is successful
		
		Statement statement = vf.createStatement(vf.createURI(subject), vf.createURI(predicate), vf.createURI(object));
				
		try {
			conn.add(statement);
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		template.insert(privateObject);
		//TODO: return ApiResult
			return new ApiResult(ApiResultType.SUCCESS);
		} else {
			return new ApiResult(ApiResultType.FAILED);
		}		
	}

	public Collection<PrivateObjectImpl> findAll() {
		return template.findAll(PrivateObjectImpl.class);
	}

	public PrivateObject find(String id) {		
		return template.findOne(new Query(Criteria.where("id").is(id)),PrivateObjectImpl.class);
	}	
	
}
