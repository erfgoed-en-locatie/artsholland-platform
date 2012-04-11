package org.waag.ah.importer;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateful;

import org.openrdf.repository.RepositoryConnection;
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

	@EJB(mappedName="java:app/datastore/BigdataConnectionService")
	private RepositoryConnectionFactory cf;
	
	private @EJB StoringRDFParser parser;
	private RepositoryConnection conn;
	
	@PostConstruct
	public void init() {
		Assert.isNull(conn, "Connection already initialized");
		try {
			conn = cf.getConnection();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	public void importResource(List<ImportResource> resources,
			ImportMetadata metadata) {
		Assert.notNull(conn, "Connection not initialized");
		logger.debug("Processing importing job: "+metadata.getJobIdentifier());
		ImportResource curResource = null;
		try {
			long oldsize = conn.size();
			Iterator<ImportResource> it = resources.iterator();
			while (it.hasNext()) {
				curResource = it.next();
				InputStream stream = null;
				try {
					logger.info("Importing: "+curResource);
					stream = curResource.parse();
					parser.parse(conn, stream, metadata);
				} finally {
					stream.close();
				}
			}
			logger.info("Committing import...");
			parser.commit();
			logger.info("Import comitted, "+(conn.size()-oldsize)+" added");
		} catch (Exception e) {
			logger.error("Error importimg url <"+curResource+">: "+e, e);
			parser.rollback();
		}
	}
	
	public void importResource(ImportResource resource, ImportMetadata metadata)
			throws Exception {
		importResource(Arrays.asList(resource), metadata);
	}
}
