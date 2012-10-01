package org.waag.ah.importer.dosa;
//package org.waag.ah.importer.dosa;
//
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.quartz.DisallowConcurrentExecution;
//import org.quartz.JobExecutionContext;
//import org.quartz.JobExecutionException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.waag.ah.ImportMetadata;
//
//@DisallowConcurrentExecution
//public class DOSAImportJob extends AbstractImportJob {
//	private Logger logger = LoggerFactory.getLogger(DOSAImportJob.class);
////	private URL resource;
////	private String strategy;
//	
//	private static final String BASE_URI = "http://127.0.0.1/ah/dosa/";
//	
//	public static final String[] RESOURCES = { 
//		"2010_oov.xls",
//		"2011_cm.xls",
//		"2011_wie.xls"
//	};	
//
//	@Override
//	public void execute(JobExecutionContext context)
//			throws JobExecutionException {
//
//		try {
//			ImportMetadata metadata = new ImportMetadata();
//			metadata.setJobIdentifier(context.getFireInstanceId());
//
////			JobDataMap dataMap = context.getMergedJobDataMap();
////			logger.info("INCOMING DATAMAP: "+dataMap.getWrappedMap());
//
//			List<URL> urls = getURLs();
//					
//			logger.info(urls.toString());
//			
//			doImport(urls, metadata);
//		} catch (Exception e) {
//			throw new JobExecutionException(e);
//		}
//	}
//
//	private List<URL> getURLs() {
//		List<URL> urls = new ArrayList<URL>();
//		
//		try {
//			
//			for (String resource : RESOURCES) {			
//				urls.add(new URL(BASE_URI + resource));
//			}
//			
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}	
//		
//		return urls;			
//	}
//
////	public void setResource(String url) throws MalformedURLException {
////		this.resource = new URL(url);
////	}
//
////	public void setStrategy(String strategy) {
////		this.strategy = strategy;
////	}
//}
