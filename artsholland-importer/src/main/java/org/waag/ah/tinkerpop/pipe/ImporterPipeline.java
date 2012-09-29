package org.waag.ah.tinkerpop.pipe;

import java.net.URL;
import java.util.List;

import org.openrdf.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.importer.ImportConfig;

import com.tinkerpop.pipes.transform.IdentityPipe;
import com.tinkerpop.pipes.util.Pipeline;

public class ImporterPipeline extends Pipeline<URL, Statement> {
	final static Logger logger = LoggerFactory.getLogger(ImporterPipeline.class);
	
	private long count = 0;
	private long pos = 0;

	public ImporterPipeline(ImportConfig config) {
		super();
		this.addPipe(new ProgressPipe());
		this.addPipe(new ParserPipeline());
		this.addPipe(new ProcessorPipeline());
	}

	public void setStarts(Iterable<URL> starts) {
		this.count = ((List<URL>) starts).size();
		super.setStarts(starts);
	}
	
	private class ProgressPipe extends IdentityPipe<URL> {
		@Override
		protected URL processNextStart() {
			URL url = super.processNextStart();
			pos++;
			logger.info("Importing "+pos+"/"+count+": "+url);
			return url;
		}
	}
}
