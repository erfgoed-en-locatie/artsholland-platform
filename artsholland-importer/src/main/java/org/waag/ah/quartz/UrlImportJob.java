package org.waag.ah.quartz;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.joda.time.DateTime;
import org.openrdf.model.Statement;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.binary.BinaryRDFWriter;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.RepositoryConnectionFactory;
import org.waag.ah.exception.ConnectionException;
import org.waag.ah.importer.ImportConfig;
import org.waag.ah.importer.ImportResult;
import org.waag.ah.importer.ImportStrategy;
import org.waag.ah.importer.UrlGenerator;
import org.waag.ah.service.MongoConnectionService;
import org.waag.ah.tinkerpop.ImporterPipeline;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.tinkerpop.pipes.util.Pipeline;

@DisallowConcurrentExecution
public class UrlImportJob implements Job {
	final static Logger logger = LoggerFactory.getLogger(UrlImportJob.class);

	private RepositoryConnectionFactory cf;
	private DBCollection coll;

	private UrlGenerator urlGenerator;
	private String graphUri;
	private ImportStrategy strategy = ImportStrategy.FULL;

	public UrlImportJob() throws NamingException, ConnectionException, RepositoryException {
		InitialContext ic = new InitialContext();
		
		MongoConnectionService mongo = (MongoConnectionService) ic
				.lookup("java:global/artsholland-platform/datastore/MongoConnectionServiceImpl");
		this.coll = mongo.getCollection(UrlImportJob.class.getName());
		coll.setObjectClass(ImportResult.class);
		
		cf = (RepositoryConnectionFactory) ic
				.lookup("java:global/artsholland-platform/core/ConnectionService");
	}

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		ImportResult result = new ImportResult();
		result.put("jobKey", context.getJobDetail().getKey().toString());
		result.put("jobId", context.getFireInstanceId());
		result.put("timestamp", context.getFireTime().getTime());
		result.put("strategy", this.strategy.toString());
		
		try {
			RepositoryConnection conn = cf.getConnection();
			
			File tempFile = File.createTempFile(result.get("jobKey").toString(),".tmp");
            FileOutputStream fout = new FileOutputStream(tempFile);			
            BinaryRDFWriter writer = new BinaryRDFWriter(fout);
			
			try {
				// TODO: Refactor ImportConfig to a UrlImporterPipeline with
				//       appropriate interface and abstract class to support
				//       all kinds of import inputs (URLs, files, etc).
				ImportConfig config = new ImportConfig();
				
				// TODO: Use context as id?
				config.setId(context.getFireInstanceId());
				config.setStrategy(this.strategy);
				config.setFromDateTime(getStartTime(context.getJobDetail().getKey().toString()));
				config.setToDateTime(new DateTime(context.getFireTime().getTime()));
				config.setContext(conn.getValueFactory().createURI(graphUri));
	
				logger.info("Running import job: strategy="+config.getStrategy()+", tempfile="+tempFile.getAbsolutePath());
	
				long oldsize = conn.size();
	
				// TODO: Change ImporterPipeline to receive ImportConfig(s) and 
				//       return ImportResult(s). All writing will be done by the
				//       pipeline, and not here.
				Pipeline<URL, Statement> pipeline = new ImporterPipeline(config);
				pipeline.setStarts(urlGenerator.getUrls(config));
	
				writer.startRDF();
				
				while (pipeline.hasNext()) {
					writer.handleStatement(pipeline.next());
				}
				
				writer.endRDF();
				fout.close();
				
//				if (this.strategy == ImportStrategy.FULL) {
//					conn.clear(config.getContext());
//				}
				
				conn.add(tempFile, null, RDFFormat.BINARY, config.getContext());
				conn.commit();
	
				logger.info("Import comitted, added " + (conn.size() - oldsize) + " statements");
				result.put("success", true);
	
			} catch (Exception e) {
				try {
					logger.info("Rolling back transaction");
					conn.rollback();
				} catch (RepositoryException e1) {
					logger.warn("Error rolling back transaction");
				}
				result.put("success", false);
				throw new JobExecutionException(e.getCause().getMessage());
			} finally {
				tempFile.delete();
				coll.insert(result);
				conn.close();
			}
		} catch (RepositoryException e) {
			throw new JobExecutionException(e);
		} catch (IOException e) {
			throw new JobExecutionException(e);
		}
	}

	public void setUrlGeneratorClass(String className)
			throws JobExecutionException {
		try {
			this.urlGenerator = (UrlGenerator) Class.forName(className)
					.newInstance();
		} catch (Exception e) {
			throw new JobExecutionException(e.getMessage(), e);
		}
	}

	public void setGraphUri(String uri) {
		this.graphUri = uri;
	}
	
	public void setStrategy(String strategy) {
		this.strategy = ImportStrategy.fromValue(strategy);
	}

	private DateTime getStartTime(String jobKey) throws NamingException {
		BasicDBObject query = new BasicDBObject();
		query.put("jobKey", jobKey);
		query.put("success", true);

		DBCursor cur = coll.find(query)
				.sort(new BasicDBObject("timestamp", -1));

		if (cur.hasNext()) {
			ImportResult lastResult = (ImportResult) cur.next();
			return new DateTime(lastResult.get("timestamp"));
		}

		return null;
	}
}
