package org.waag.ah.spring.service;

import javax.ejb.EJB;

import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sail.SailRepositoryConnection;
import org.openrdf.sail.SailException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.waag.ah.bigdata.OpenSaharaConnectionService;

import com.useekm.indexing.IndexingSailConnection;
import com.useekm.indexing.exception.IndexException;

@Service("geoService")
public class GeoService implements InitializingBean {
	
	@EJB(mappedName="java:app/datastore/OpenSaharaConnectionService")
	private OpenSaharaConnectionService openSahara;
	
	private IndexingSailConnection conn;

	
	@Override
	public void afterPropertiesSet() throws Exception {
			conn = openSahara.getConnection();
	}
	
	
	public String getVis() {
		// TODO Auto-generated method stub
		
		return "Hondje";	
		
	}

	public void reindex() {
		try {
			openSahara.reindex();
		} catch (IndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SailException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	
}
