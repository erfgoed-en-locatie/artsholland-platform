package org.waag.ah.importer;

import java.net.URL;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.service.MongoConnectionService;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

public class UrlImportJob implements Job {
	final static Logger logger = LoggerFactory.getLogger(UrlImportJob.class);

	private DBCollection coll;

	private UrlGenerator urlGenerator;
	private ImportStrategy strategy;
	
	public UrlImportJob() throws NamingException {
		InitialContext ic = new InitialContext();
		MongoConnectionService mongo = (MongoConnectionService) ic
				.lookup("java:global/artsholland-platform/datastore/MongoConnectionServiceImpl");
		coll = mongo.getCollection(UrlImportJob.class.getName());
		coll.setObjectClass(ImportJobResult.class);
	}

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		String jobKey = urlGenerator.getClass().getName();

		ImportJobResult result = new ImportJobResult();
		result.put("jobKey", jobKey);
		result.put("timestamp", context.getFireTime().getTime());
		result.put("strategy", this.strategy.toString());
		
		try {
			ImporterConfig config = new ImporterConfig();
			config.setStrategy(this.strategy);
			config.setFromImportDate(getStartTime(jobKey));
			config.setUntilImportDate(new DateTime(context.getFireTime().getTime()));
			
			logger.info("Running import job with strategy: "+config.getStrategy());
			
			List<URL> urls = urlGenerator.getUrls(config);
			logger.info(urls.toString());
			
//			Pipeline<URL, Statement> pipeline = new ImporterPipeline();
//			pipeline.setStarts(urls);		
			
			result.put("success", true);
			
		} catch (Exception e) {
			logger.warn("JOB FAILURE: "+e.getMessage());
			result.put("success", false);
			throw new JobExecutionException(e.getMessage(), e);
		} finally {
			coll.insert(result);
		}
	}

	public void setUrlGeneratorClass(String className)
			throws JobExecutionException {
		try {		
			this.urlGenerator = (UrlGenerator) Class.forName(className).newInstance();
		} catch (Exception e) {
			throw new JobExecutionException(e.getMessage(), e);
		}
	}
	
	public void setStrategy(String strategy) {
		this.strategy = ImportStrategy.fromValue(strategy);
	}
	
	private DateTime getStartTime(String jobKey) throws NamingException {
    	BasicDBObject query = new BasicDBObject();
        query.put("jobKey", jobKey);
        query.put("success", true);
        
        DBCursor cur = coll.find(query).sort(new BasicDBObject("timestamp", -1));
        
        if (cur.hasNext()) {
        	ImportJobResult lastResult = (ImportJobResult) cur.next();
        	return new DateTime(lastResult.get("timestamp"));
        }
        
        return null;
	}
}
