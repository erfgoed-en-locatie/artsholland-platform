package org.waag.ah.cascading.bixo;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.kohsuke.args4j.Option;

public class SimpleCrawlToolOptions {

    public static final int NO_CRAWL_DURATION = 0;
    public static final int DEFAULT_MAX_THREADS = 10;
    private static final int DEFAULT_NUM_LOOPS = 1;

    private String _loggingAppender = null;
    private boolean _debugLogging = false;

    private String _outputDir;
    private String _agentName;
    private String _domain;
    private String _urlsFile;
    
    private int _crawlDuration = NO_CRAWL_DURATION;
    private int _maxThreads = DEFAULT_MAX_THREADS;
    private int _numLoops = DEFAULT_NUM_LOOPS;

    
    @Option(name = "-domain", usage = "domain to crawl (e.g. cnn.com)", required = false)
    public void setDomain(String domain) {
        _domain = domain;
    }

    @Option(name = "-urls", usage = "text file containing list of urls (either -domain or -urls needs to be set)", required = false)
    public void setUrlsFile(String urlsFile) {
        _urlsFile = urlsFile;
    }

    @Option(name = "-d", usage = "debug logging", required = false)
    public void setDebugLogging(boolean debugLogging) {
        _debugLogging = debugLogging;
    }

    @Option(name = "-logger", usage = "set logging appender (console, DRFA)", required = false)
    public void setLoggingAppender(String loggingAppender) {
        _loggingAppender = loggingAppender;
    }
    
    @Option(name = "-outputdir", usage = "output directory", required = true)
    public void setOutputDir(String outputDir) {
        _outputDir = outputDir;
    }

    @Option(name = "-agentname", usage = "user agent name", required = true)
    public void setAgentName(String agentName) {
        _agentName = agentName;
    }

    @Option(name = "-maxthreads", usage = "maximum number of fetcher threads to use", required = false)
    public void setMaxThreads(int maxThreads) {
        _maxThreads = maxThreads;
    }

    @Option(name = "-numloops", usage = "number of fetch/update loops", required = false)
    public void setNumLoops(int numLoops) {
        _numLoops = numLoops;
    }

    @Option(name = "-duration", usage = "target crawl duration in minutes", required = false)
    public void setCrawlDuration(int crawlDuration) {
        _crawlDuration = crawlDuration;
    }

    public String getOutputDir() {
        return _outputDir;
    }

    public String getDomain() {
        return _domain;
    }

    public String getUrlsFile() {
        return _urlsFile;
    }

    public String getAgentName() {
        return _agentName;
    }
    
    public int getMaxThreads() {
        return _maxThreads;
    }

    public int getNumLoops() {
        return _numLoops;
    }

    public int getCrawlDuration() {
        return _crawlDuration;
    }

    public boolean isDebugLogging() {
        return _debugLogging;
    }
    
    public String getLoggingAppender() {
        return _loggingAppender;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
