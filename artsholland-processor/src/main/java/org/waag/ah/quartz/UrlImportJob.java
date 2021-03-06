package org.waag.ah.quartz;

import java.net.URL;
import java.util.List;

import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jeromq.ZMQ;
import org.joda.time.DateTime;
import org.openrdf.repository.RepositoryException;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.exception.ConnectionException;
import org.waag.ah.importer.ImportConfig;
import org.waag.ah.importer.ImportJob;
import org.waag.ah.importer.ImportResult;
import org.waag.ah.importer.ImportStrategy;
import org.waag.ah.importer.UrlGenerator;
import org.waag.ah.service.MongoConnectionService;
import org.waag.ah.zeromq.ZMQContext;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

@DisallowConcurrentExecution
public class UrlImportJob extends ImportJob {
	final static Logger logger = LoggerFactory.getLogger(UrlImportJob.class);

	private DBCollection coll;
	private UrlGenerator urlGenerator;
	private ImportStrategy strategy = ImportStrategy.FULL;

//	private @EJB ZMQContext zmq;
	private static final ZMQ.Context zmq;
	static {
		zmq = ZMQ.context(1);
	}
	
	public UrlImportJob() throws NamingException, ConnectionException, RepositoryException {
		InitialContext ic = new InitialContext();
		MongoConnectionService mongo = (MongoConnectionService) ic
				.lookup("java:global/artsholland-platform/datastore/MongoConnectionServiceImpl");
		this.coll = mongo.getCollection(UrlImportJob.class.getName());
		coll.setObjectClass(ImportResult.class);
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			String jobKey = context.getJobDetail().getKey().toString(); //this.urlGenerator.getClass().getName();
			
			ImportResult lastResult = getLastResult(jobKey);
			if (lastResult != null && this.strategy == ImportStrategy.ONCE) {
				context.setResult("Import job "+jobKey+" with strategy ONCE already executed, skipping.");
				return;
			}

			ImportResult result = new ImportResult();
			result.put("jobKey", jobKey);
			result.put("jobId", context.getFireInstanceId());
			result.put("timestamp", context.getFireTime().getTime());
			result.put("strategy", this.strategy.toString());
			
			// TODO: Refactor ImportConfig to a UrlImporterPipeline with
			//       appropriate interface and abstract class to support
			//       all kinds of import inputs (URLs, files, etc).
			ImportConfig config = new ImportConfig();
			
			// TODO: Use context as id?
			config.setId(context.getFireInstanceId());
			config.setStrategy(this.strategy);
			if (lastResult != null) {
				config.setFromDateTime(new DateTime(lastResult.get("timestamp")));
			}
			config.setToDateTime(new DateTime(context.getFireTime().getTime()));

	        ZMQ.Socket sender = zmq.socket(ZMQ.PUSH);
	        sender.connect("tcp://localhost:5557");			
			
	        try {
	        	List<URL> urls = urlGenerator.getUrls(config);
	        	if (urls.size() > 0) {
		        	sender.send(new Gson().toJson(urls).getBytes(), 0);
			        context.setResult("Sent "+urls.size()+" URLs");
	        	}
				result.put("success", true);	        	
	        } catch (Exception e) {
	        	result.put("success", false);
				throw e;
	        } finally {
	        	coll.insert(result);
	        	sender.close();
	        }
		} catch (Exception e) {
			throw new JobExecutionException(e);
		}
	}
	
	public void setUrlGeneratorClass(String className) throws JobExecutionException {
		try {
			this.urlGenerator = (UrlGenerator) Class.forName(className).newInstance();
		} catch (Exception e) {
			throw new JobExecutionException(e.getMessage(), e);
		}
	}
	
	public void setStrategy(String strategy) {
		this.strategy = ImportStrategy.fromValue(strategy);
	}
	
	private ImportResult getLastResult(String jobKey) {
		BasicDBObject query = new BasicDBObject();
		query.put("jobKey", jobKey);
		query.put("success", true);

		DBCursor cur = coll.find(query)
				.sort(new BasicDBObject("timestamp", -1));

		if (cur.hasNext()) {
			return (ImportResult) cur.next();
		}
		
		return null;
	}
}
/*
RepositoryConnection conn = cf.getReadWriteConnection();
ValueFactory vf = conn.getValueFactory();
URI contextUri = vf.createURI(graphUri);

try {
	logger.info("Running import job "+jobKey+" with strategy="+config.getStrategy());

	// TODO: Change ImporterPipeline to receive ImportConfig(s) and 
	//       return ImportResult(s). Persistence must be handled by
	//       the pipeline, not here.
	Pipeline<URL, Statement> pipeline = new ImporterPipeline(config);
	pipeline.setStarts(urlGenerator.getUrls(config));

	long oldsize = conn.size(contextUri);
	
	if (this.strategy == ImportStrategy.FULL) {
		conn.clear(config.getContext());
	}
	
	URI predicate = vf.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
	
	while (pipeline.hasNext()) {
		Statement statement = pipeline.next();
		// TODO: Check if quering for the statement is less
		//       expensive. Maybe repositories should indicate
		//       whether they support delete on insert/update, or
		//       implement a custom RepositoryConnection to handle
		//       these cases.
//		conn.remove(statement, contextUri);
//		logger.info(statement.toString());
		if (statement.getPredicate().equals(predicate)) {
//			RepositoryResult<Statement> statements = conn.getStatements(statement.getSubject(), null, null, false, contextUri);
//			for (Statement stmt : statements.asList()) {
//				conn.remove(stmt);
//			}
			conn.remove(statement.getSubject(), null, null, contextUri);
		}
		conn.add(statement, contextUri);
	}
	
	logger.info("Import finished, committing statements...");
	conn.commit();
	
	context.setResult("Added "+(conn.size(contextUri)-oldsize)+" statements");
	result.put("success", true);

} catch (Exception e) {
//	logger.error("Exception while importing "+contextUri+" ("+e.getMessage()+")");
//	context.setResult("Exception while importing "+contextUri+" ("+e.getMessage()+"), rolling back transaction...");
	conn.rollback();
	result.put("success", false);
//	e.printStackTrace();
	throw e; //new ImportException(e.getCause().getMessage());
} finally {
	coll.insert(result);
	logger.info("Closing connection: "+conn);
	conn.close();
}

//			config.setContext(vf.createURI(graphUri));

//	public void setGraphUri(String uri) {
//		this.graphUri = uri;
//	}
*/