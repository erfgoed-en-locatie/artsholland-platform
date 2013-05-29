package org.waag.ah.tinkerpop;

import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.importer.ImportDocument;
import org.waag.ah.tika.DocumentParser;

import com.tinkerpop.pipes.AbstractPipe;

public class TikaParserPipe extends AbstractPipe<ImportDocument, String> {
	private Logger logger = LoggerFactory.getLogger(TikaParserPipe.class);
	private DocumentParser parser = new DocumentParser();
	
	@Override
	protected String processNextStart() throws NoSuchElementException {
		ImportDocument doc = starts.next();
		try {
			return parser.parse(doc);
		} catch(Exception e) {
			logger.error("Error parsing URL ("+e.getMessage()+"): "+doc.getUrl());
			throw new RuntimeException(e.getMessage());
		}

	}
}
