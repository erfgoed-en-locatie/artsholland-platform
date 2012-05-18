package org.waag.ah.cascading;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;

import bixo.config.ParserPolicy;
import bixo.datum.FetchedDatum;
import bixo.datum.ParsedDatum;
import bixo.parser.BaseParser;
import bixo.utils.IoUtils;

@SuppressWarnings("serial")
public class ResourceParser extends BaseParser {
	private Parser _parser;
    
	public ResourceParser() {
        this(new ParserPolicy());
    }

	public ResourceParser(ParserPolicy parserPolicy) {
		super(parserPolicy);
	}

	protected synchronized void init() {
		if (_parser == null) {
			_parser = getTikaParser();
		}
	}

    public Parser getTikaParser() {
        return new AutoDetectParser();
    }
    
	@Override
	public ParsedDatum parse(FetchedDatum fetchedDatum) throws Exception {
		init();
        
        // Provide clues to the parser about the format of the content.
        Metadata metadata = new Metadata();
        metadata.add(Metadata.RESOURCE_NAME_KEY, fetchedDatum.getUrl());
        metadata.add(Metadata.CONTENT_TYPE, fetchedDatum.getContentType());
        String charset = getCharset(fetchedDatum);
        metadata.add(Metadata.CONTENT_LANGUAGE, getLanguage(fetchedDatum, charset));
        
        InputStream is = new ByteArrayInputStream(fetchedDatum.getContentBytes(), 0, fetchedDatum.getContentLength());

        try {
        	URL baseUrl = getContentLocation(fetchedDatum);
        	metadata.add(Metadata.CONTENT_LOCATION, baseUrl.toExternalForm());

            Callable<ParsedDatum> c = new TikaCallable(_parser, is, metadata, new ParseContext());
            FutureTask<ParsedDatum> task = new FutureTask<ParsedDatum>(c);
            Thread t = new Thread(task);
            t.start();
            
            ParsedDatum result;
            try {
                result = task.get(getParserPolicy().getMaxParseDuration(), TimeUnit.MILLISECONDS);
            } catch (TimeoutException e) {
                task.cancel(true);
                t.interrupt();
                throw e;
            } finally {
                t = null;
            }
            
            // TODO KKr Should there be a BaseParser to take care of copying
            // these two fields?
            result.setHostAddress(fetchedDatum.getHostAddress());
            result.setPayload(fetchedDatum.getPayload());
            return result;
        } finally {
            IoUtils.safeClose(is);
        }
	}
}
