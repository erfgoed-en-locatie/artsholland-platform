package org.waag.ah.importer;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateful;

import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.waag.ah.ImportMetadata;
import org.waag.ah.ImportResource;
import org.waag.ah.ImportService;
import org.waag.ah.RepositoryConnectionFactory;
import org.waag.ah.exception.ImportException;
import org.waag.ah.sesame.StoringRDFParser;

@Stateful
public class ImportServiceBean implements ImportService {
	private static final Logger logger = LoggerFactory.getLogger(ImportServiceBean.class);
	private RepositoryConnection conn;

	private final int RETRY_MAX_COUNT = 3;
	private final long RETRY_TIMEOUT = 5000;

	@EJB(mappedName="java:app/datastore/BigdataConnectionService")
	private RepositoryConnectionFactory cf;
	
//	@PostConstruct
//	public void init() {
//		Assert.isNull(conn, "Connection already initialized");
//		connect();
//	}
	
	private void connect() {
		try {
			if (conn == null || !conn.isOpen()) {
				conn = cf.getConnection();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	private void disconnect() {
		try {
			if (conn != null && conn.isOpen()) {
				conn.close();
			}
		} catch (RepositoryException e) {
			logger.warn("Error closing connection: "+e.getMessage(), e);
		}
	}
	
//	@PreDestroy
//	public void destroy() {
//		try {
//			conn.close();
//		} catch (RepositoryException e) {
//			logger.error(e.getMessage());
//		}
//	}
	
	public void importResource(List<ImportResource> resources,
			ImportMetadata metadata) throws Exception {
		Assert.notEmpty(resources, "No resources provided for import");
		
		if (logger.isDebugEnabled()) {
			logger.debug("Processing importing job: "+metadata.getJobIdentifier());
		}
		
		StoringRDFParser parser = new StoringRDFParser();		
		ImportResource curResource = null;
		try {
			connect();
			int pos = 1;
			long oldsize = conn.size();
			int retryCount = 0;
			
			Iterator<ImportResource> it = resources.iterator();
			while (it.hasNext()) {
				if (retryCount == 0) {
					curResource = it.next();
				}
				InputStream stream = null;
				try {
					logger.info("Importing "+pos+"/"+resources.size()+": "+curResource);
					stream = curResource.parse();
					parser.parse(conn, stream, metadata);
					pos++;
					retryCount = 0;
				} catch (Exception e) {
					retryCount++;
					if (retryCount > RETRY_MAX_COUNT) {
						throw new ImportException("Maximum number of retries reached, aborting...");
					} else {
						logger.warn("Error during import: "+e.getMessage()+" [retrying in "+(RETRY_TIMEOUT/1000)+" seconds...]");
						Thread.sleep(RETRY_TIMEOUT);
					}
				} finally {
					stream.close();
				}
			}
			logger.info("Committing import...");
			conn.commit();
			logger.info("Import comitted, "+(conn.size()-oldsize)+" added");
		} catch (Exception e) {
			logger.error("Error importimg url <"+curResource+">: "+e);
			conn.rollback();
			throw e;
		} finally {
			disconnect();
		}
	}
	
	public void importResource(ImportResource resource, ImportMetadata metadata)
			throws Exception {
		importResource(Arrays.asList(resource), metadata);
	}
}
