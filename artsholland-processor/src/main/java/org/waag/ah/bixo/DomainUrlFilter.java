package org.waag.ah.bixo;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import bixo.datum.UrlDatum;
import bixo.urls.BaseUrlFilter;

// Filter URLs that fall outside of the target domain
@SuppressWarnings("serial")
public class DomainUrlFilter extends BaseUrlFilter {
    private static final Logger LOGGER = Logger.getLogger(DomainUrlFilter.class);

    private String _domain;
    private Pattern _suffixExclusionPattern;
    private Pattern _protocolInclusionPattern;

    public DomainUrlFilter(String domain) {
        _domain = domain;
        _suffixExclusionPattern = Pattern.compile("(?i)\\.(pdf|zip|gzip|gz|sit|bz|bz2|tar|tgz|exe)$");
        _protocolInclusionPattern = Pattern.compile("(?i)^(http|https)://");
    }

    @Override
    public boolean isRemove(UrlDatum datum) {
        String urlAsString = datum.getUrl();
        
        // Skip URLs with protocols we don't want to try to process
        if (!_protocolInclusionPattern.matcher(urlAsString).find()) {
            return true;
            
        }

        if (_suffixExclusionPattern.matcher(urlAsString).find()) {
            return true;
        }
        
        try {
            URL url = new URL(urlAsString);
            String host = url.getHost();
            return (!host.endsWith(_domain));
        } catch (MalformedURLException e) {
            LOGGER.warn("Invalid URL: " + urlAsString);
            return true;
        }
    }
}