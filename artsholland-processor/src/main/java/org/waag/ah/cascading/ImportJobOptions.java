package org.waag.ah.cascading;

import java.net.URL;
import java.util.List;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class ImportJobOptions {

    public static final int DEFAULT_MAX_THREADS = 10;

    private String _agentName;
    private List<URL> _domain;
    
    private boolean _debugLogging = false;
    private int _maxThreads = DEFAULT_MAX_THREADS;

    
    public void setResources(List<URL> resources) {
        _domain = resources;
    }

    public List<URL> getResources() {
        return _domain;
    }

    public void setAgentName(String agentName) {
        _agentName = agentName;
    }

    public String getAgentName() {
        return _agentName;
    }

    public void setMaxThreads(int maxThreads) {
        _maxThreads = maxThreads;
    }
    
    public int getMaxThreads() {
        return _maxThreads;
    }

    public boolean isDebugLogging() {
        return _debugLogging;
    }
    
    public void setDebugLogging(boolean debugLogging) {
    	_debugLogging = debugLogging;
    }
    
    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
