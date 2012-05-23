package org.waag.ah.cascading;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobConf;
import org.waag.ah.cascading.bixo.CrawlConfig;
import org.waag.ah.cascading.bixo.CrawlDbDatum;

import bixo.config.FetcherPolicy;
import bixo.config.ParserPolicy;
import bixo.config.UserAgent;
import bixo.datum.FetchedDatum;
import bixo.datum.ParsedDatum;
import bixo.datum.UrlDatum;
import bixo.datum.UrlStatus;
import bixo.fetcher.BaseFetcher;
import bixo.fetcher.SimpleHttpFetcher;
import bixo.operations.BaseScoreGenerator;
import bixo.operations.FixedScoreGenerator;
import bixo.pipes.FetchPipe;
import bixo.pipes.ParsePipe;
import cascading.flow.Flow;
import cascading.flow.FlowConnector;
import cascading.flow.FlowProcess;
import cascading.operation.BaseOperation;
import cascading.operation.Function;
import cascading.operation.FunctionCall;
import cascading.pipe.Each;
import cascading.pipe.Pipe;
import cascading.scheme.SequenceFile;
import cascading.scheme.TextLine;
import cascading.tap.Hfs;
import cascading.tap.Tap;
import cascading.tuple.Fields;
import cascading.tuple.TupleEntryCollector;

import com.bixolabs.cascading.HadoopUtils;
import com.bixolabs.cascading.NullContext;

public class ImportJobWorkflow {
//	private static final Logger LOGGER = Logger.getLogger(ImportJobWorkflow.class);
    
    @SuppressWarnings("serial")
	private static class WrapUrlDatum extends BaseOperation<NullContext> implements Function<NullContext> {
        
        public WrapUrlDatum() {
            super(UrlDatum.FIELDS);
        }
        
		@Override
		public void operate(FlowProcess flowProcess,
				FunctionCall<NullContext> functionCall) {
			CrawlDbDatum datum = new CrawlDbDatum(functionCall.getArguments());
            UrlDatum urlDatum = new UrlDatum(datum.getUrl());
            urlDatum.setPayloadValue(CrawlDbDatum.LAST_FETCHED_FIELD, datum.getLastFetched());
            urlDatum.setPayloadValue(CrawlDbDatum.LAST_UPDATED_FIELD, datum.getLastUpdated());
            urlDatum.setPayloadValue(CrawlDbDatum.LAST_STATUS_FIELD, datum.getLastStatus().name());
            urlDatum.setPayloadValue(CrawlDbDatum.CRAWL_DEPTH, datum.getCrawlDepth());	
            functionCall.getOutputCollector().add(urlDatum.getTuple());
		}
    }
    
	private static void initImportResources(List<URL> resources, Tap urlSink,
			JobConf conf) throws IOException {

		TupleEntryCollector writer;
		try {
			writer = urlSink.openForWrite(conf);
			// SimpleUrlNormalizer normalizer = new SimpleUrlNormalizer();
			for (URL url : resources) {
				CrawlDbDatum datum = new CrawlDbDatum(url.toExternalForm(), 0,
						0, UrlStatus.UNFETCHED, 0);
				writer.add(datum.getTuple());
			}
			writer.close();
		} catch (IOException e) {
			throw e;
		}
	}
    
	// Path inputDir, Path curLoopDirPath, UserAgent userAgent, FetcherPolicy fetcherPolicy,
    // BaseUrlFilter urlFilter, int maxThreads, boolean debug, String persistentDbLocation
	public static Flow createWorkflow(Path workPath, UserAgent userAgent,
			FetcherPolicy fetcherPolicy, List<URL> resources)
			throws IOException, InterruptedException {
		String dbLocation = null;
		int maxThreads = 10;
		boolean debug = true;
		
		JobConf conf = HadoopUtils.getDefaultJobConf(CrawlConfig.CRAWL_STACKSIZE_KB);
	    int numReducers = HadoopUtils.getNumReducers(conf);
	    conf.setNumReduceTasks(numReducers);
	    
	    Tap urlTap = JDBCTapFactory.createUrlsSinkJDBCTap(dbLocation);
	    initImportResources(resources, urlTap, conf);
        
		FileSystem fs = workPath.getFileSystem(conf);
		if (fs.exists(workPath)) {
            System.out.println("Warning: Previous cycle output dirs exist in : " + workPath);
            System.out.println("Warning: Delete the output dir before running");
            fs.delete(workPath, true);
        }
 		
        Tap inputSource = JDBCTapFactory.createUrlsSourceJDBCTap(dbLocation); 
        
        Path statusDirPath = new Path(workPath, CrawlConfig.STATUS_SUBDIR_NAME);
        Tap statusSink = new Hfs(new TextLine(), statusDirPath.toString());
        
        Pipe importPipe = new Pipe("url importer");
        importPipe = new Each(importPipe, new WrapUrlDatum(), Fields.RESULTS);
        
        Path contentPath = new Path(workPath, CrawlConfig.CRAWLDB_SUBDIR_NAME);
        Tap contentSink = new Hfs(new SequenceFile(FetchedDatum.FIELDS), contentPath.toString());

        Path parsePath = new Path(workPath, CrawlConfig.PARSE_SUBDIR_NAME);
        Tap parseSink = new Hfs(new SequenceFile(ParsedDatum.FIELDS), parsePath.toString());
        
//        Tap tripleSink = ImportJobTapFactory.createImportResourceSink(conn);
        
        BaseFetcher fetcher = new SimpleHttpFetcher(maxThreads, fetcherPolicy, userAgent);
        BaseScoreGenerator scorer = new FixedScoreGenerator();
        FetchPipe fetchPipe = new FetchPipe(importPipe, scorer, fetcher, numReducers);
        
        Pipe statusPipe = new Pipe("status pipe", fetchPipe.getStatusTailPipe());
        
        ResourceParser parser = new ResourceParser(new ParserPolicy());
        ParsePipe parsePipe = new ParsePipe(fetchPipe.getContentTailPipe(), parser);
        
//        Pipe analyzerPipe = new Pipe("analyzer pipe");
//        analyzerPipe = new Each(parsePipe.getTailPipe(), new AnalyzeHtml());        
        
//        Pipe urlFromOutlinksPipe = new Pipe("url from outlinks", parsePipe.getTailPipe());
//        urlFromOutlinksPipe = new Each(urlFromOutlinksPipe, new CreateUrlDatumFromOutlinksFunction());
//        urlFromOutlinksPipe = new Each(urlFromOutlinksPipe, new UrlFilter(urlFilter));
//        urlFromOutlinksPipe = new Each(urlFromOutlinksPipe, new NormalizeUrlFunction(new SimpleUrlNormalizer()));        

//        Pipe outputPipe = new Pipe("output pipe");
//      outputPipe = new Each(urlPipe, new CreateCrawlDbDatumFromUrlFunction());
		
        // Create the output map that connects each tail pipe to the appropriate sink.
        Map<String, Tap> sinkMap = new HashMap<String, Tap>();
        sinkMap.put(statusPipe.getName(), statusSink);
        sinkMap.put(FetchPipe.CONTENT_PIPE_NAME, contentSink);
        sinkMap.put(ParsePipe.PARSE_PIPE_NAME, parseSink);
//        sinkMap.put(outputPipe.getName(), tripleSink);

        FlowConnector flowConnector = new FlowConnector(HadoopUtils.getDefaultProperties(ImportJobWorkflow.class, debug, conf));
        return flowConnector.connect(inputSource, sinkMap, statusPipe, parsePipe.getTailPipe(), fetchPipe.getContentTailPipe());
	}
}
