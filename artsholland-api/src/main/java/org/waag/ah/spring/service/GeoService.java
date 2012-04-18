package org.waag.ah.spring.service;

import javax.ejb.EJB;

import org.openrdf.repository.sail.SailRepositoryConnection;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.waag.ah.bigdata.OpenSaharaTest;

@Service("geoService")
public class GeoService implements InitializingBean {


	
	@EJB(mappedName="java:app/datastore/OpenSaharaTest")
	private OpenSaharaTest koek;
	
	private SailRepositoryConnection conn;

	
	@Override
	public void afterPropertiesSet() throws Exception {
			conn = koek.getConnection();
			conn.close();
	}

	public String getVis() {
		// TODO Auto-generated method stub
		return "Hondje";	
		
	}
}
