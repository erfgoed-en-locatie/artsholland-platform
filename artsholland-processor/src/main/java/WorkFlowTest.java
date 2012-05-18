import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobConf;
import org.waag.ah.ImportMetadata;
import org.waag.ah.bixo.CrawlConfig;
import org.waag.ah.cascading.ImportJobWorkflow;

import bixo.config.FetcherPolicy;
import bixo.config.FetcherPolicy.FetcherMode;
import bixo.config.UserAgent;
import cascading.flow.Flow;

import com.bixolabs.cascading.HadoopUtils;

public class WorkFlowTest {

	/**
	 * @param args
	 *
	 * @author	Raoul Wissink <raoul@raoul.net>
	 * @throws InterruptedException 
	 * @throws IOException 
	 * @since	May 1, 2012
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
        
		List<URL> resources = new ArrayList<URL>();
		resources.add(new URL("http://ps4.uitburo.nl/api/search?key=505642b12881b9a60688411a333bc78b&resolve=true&rows=500&start=0&createdto=2012-04-29T13:44:00.007Z"));
//		resources.add(new URL("http://ps4.uitburo.nl/api/search?key=505642b12881b9a60688411a333bc78b&resolve=true&rows=500&start=500&createdto=2012-04-29T13:44:00.007Z"));
		
		ImportMetadata metadata = new ImportMetadata();
		metadata.setJobIdentifier("1234");
		
        JobConf conf = HadoopUtils.getDefaultJobConf(CrawlConfig.CRAWL_STACKSIZE_KB);
        conf.set("jobId", metadata.getJobIdentifier());
        int numReducers = HadoopUtils.getNumReducers(conf);
        conf.setNumReduceTasks(numReducers);		

        Path workPath = new Path("/tmp/cascading/quartz");
		FileSystem fs = workPath.getFileSystem(conf);
        if (!fs.exists(workPath)) {
            fs.mkdirs(workPath);
        }

        UserAgent userAgent = new UserAgent("TestAgent", "test@waxworks.nl", "http://waxworks.nl");

        FetcherPolicy fetcherPolicy = new FetcherPolicy();
        fetcherPolicy.setCrawlDelay(CrawlConfig.DEFAULT_CRAWL_DELAY);
//        fetcherPolicy.setMaxContentSize(CrawlConfig.MAX_CONTENT_SIZE);
        fetcherPolicy.setFetcherMode(FetcherMode.EFFICIENT);  
        
		Flow flow = ImportJobWorkflow.createWorkflow(workPath, userAgent, fetcherPolicy, resources);
		flow.complete();
	}
}
