package org.waag.ah.importer;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
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
import org.waag.ah.sesame.StoringRDFParser;

@Stateful
public class ImportServiceBean implements ImportService {
	private static final Logger logger = LoggerFactory.getLogger(ImportServiceBean.class);
	private RepositoryConnection conn;

	@EJB(mappedName="java:app/datastore/BigdataConnectionService")
	private RepositoryConnectionFactory cf;
	
	@PostConstruct
	public void init() {
		Assert.isNull(conn, "Connection already initialized");
		try {
			conn = cf.getConnection();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	@PreDestroy
	public void destroy() {
		try {
			conn.close();
		} catch (RepositoryException e) {
			logger.error(e.getMessage());
		}
	}
	
	public void importResource(List<ImportResource> resources,
			ImportMetadata metadata) throws Exception {
		Assert.notNull(conn, "Connection not initialized");
		Assert.notEmpty(resources, "No resources provided for import");
		logger.debug("Processing importing job: "+metadata.getJobIdentifier());
		ImportResource curResource = null;
		StoringRDFParser parser = new StoringRDFParser(conn);
		try {
			int pos = 1;
			long oldsize = conn.size();
			Iterator<ImportResource> it = resources.iterator();
			while (it.hasNext()) {
				curResource = it.next();
				InputStream stream = null;
				try {
					logger.info("Importing "+pos+"/"+resources.size()+": "+curResource);
					stream = curResource.parse();
					parser.parse(stream, metadata);
					pos++;
				} finally {
					stream.close();
				}
			}
			logger.info("Committing import...");
			parser.commit();
			logger.info("Import comitted, "+(conn.size()-oldsize)+" added");
		} catch (Exception e) {
			logger.error("Error importimg url <"+curResource+">: "+e);
			parser.rollback();
			throw e;
		}
	}
	
	public void importResource(ImportResource resource, ImportMetadata metadata)
			throws Exception {
		importResource(Arrays.asList(resource), metadata);
	}
}
