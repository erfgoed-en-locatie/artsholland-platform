package org.waag.ah.api.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.security.oauth.common.signature.SharedConsumerSecret;
import org.springframework.security.oauth.common.signature.SignatureSecret;
import org.springframework.security.oauth.consumer.BaseProtectedResourceDetails;
import org.springframework.security.oauth.consumer.OAuthConsumerSupport;
import org.springframework.security.oauth.consumer.OAuthConsumerToken;
import org.springframework.security.oauth.consumer.ProtectedResourceDetails;
import org.springframework.security.oauth.consumer.ProtectedResourceDetailsService;
import org.springframework.security.oauth.consumer.client.CoreOAuthConsumerSupport;
import org.springframework.security.oauth.consumer.net.DefaultOAuthURLStreamHandlerFactory;

public class TestOAuthClient {
	 
    protected static Logger log = Logger.getLogger(TestOAuthClient.class);
 
    private static final String SERVER_URL = "http://localhost:8080/api";
    private static final String SERVER_URL_OAUTH_REQUEST = SERVER_URL + "/oauth/request_token";
    private static final String SERVER_URL_OAUTH_CONFIRM = SERVER_URL + "/oauth/confirm_access";
    private static final String SERVER_URL_OAUTH_ACCESS = SERVER_URL + "/oauth/access_token";
 
    private static final String RESOURCE_URL = SERVER_URL + "/rest/profile/1";
    private static final String RESOURCE_ID = "photos";
 
    private static final String CONSUMER_KEY = "tonr-consumer-key";
    private static final String CONSUMER_SECRET = "SHHHHH!!!!!!!!!!";
    private static final String SIGNATURE_METHOD = "HMAC-SHA1";
    private static final String DEFAULT_ENCODING = "ISO-8859-1";
 
    // -------------------------------------------------------------------------------------------------
 
    public static void main(String[] args) throws IOException {
        testClassicMode();
        test2LeggedMode();
    }
 
    // -------------------------------------------------------------------------------------------------
 
    private static void testClassicMode() throws IOException {
        OAuthConsumerSupport consumerSupport = createConsumerSupport();
        // step 1 - get the request token
        OAuthConsumerToken requestToken = getRequestToken(consumerSupport);
        // step 2 - wait for the user to grant access on the protected resource
        String verifier = authorizeRequestToken(requestToken);
        // step 3 - get the access token
        OAuthConsumerToken accessToken = getAccessToken(consumerSupport, requestToken, verifier);
        // step 4 - get the protected resource
        getProtectedResource(consumerSupport, accessToken);
    }
 
    private static void test2LeggedMode() throws IOException {
        OAuthConsumerSupport consumerSupport = createConsumerSupport();
        getProtectedResource(consumerSupport, new OAuthConsumerToken());
    }
 
    // -------------------------------------------------------------------------------------------------
 
    private static OAuthConsumerSupport createConsumerSupport() {
        CoreOAuthConsumerSupport consumerSupport = new CoreOAuthConsumerSupport();
        consumerSupport.setStreamHandlerFactory(new DefaultOAuthURLStreamHandlerFactory());
        consumerSupport.setProtectedResourceDetailsService(new ProtectedResourceDetailsService() {
            public ProtectedResourceDetails loadProtectedResourceDetailsById(String id) throws IllegalArgumentException {
                SignatureSecret secret = new SharedConsumerSecret(CONSUMER_SECRET);
 
                BaseProtectedResourceDetails result = new BaseProtectedResourceDetails();
                result.setConsumerKey(CONSUMER_KEY);
                result.setSharedSecret(secret);
                result.setSignatureMethod(SIGNATURE_METHOD);
                result.setUse10a(true); // set this to false to not require the verifier on the second step
                result.setRequestTokenURL(SERVER_URL_OAUTH_REQUEST);
                result.setAccessTokenURL(SERVER_URL_OAUTH_ACCESS);
                return result;
            }
        });
        return consumerSupport;
    }
 
    private static OAuthConsumerToken getRequestToken(OAuthConsumerSupport consumerSupport) {
        log.info("OAUTH: Request token: getting...");
        OAuthConsumerToken requestToken = consumerSupport.getUnauthorizedRequestToken(RESOURCE_ID, null);
        log.info("OAUTH: Request token: " + requestToken.getValue());
        log.info("OAUTH: Request token secret: " + requestToken.getSecret());
        return requestToken;
    }
 
    private static String authorizeRequestToken(OAuthConsumerToken requestToken) {
        log.info("OAUTH: Waiting for token authorization... ");
        System.out.println("\t1. Go to: " + SERVER_URL_OAUTH_CONFIRM + "?oauth_token=" + requestToken.getValue());
        System.out.println("\t2. Authorize the request for the protected resource (with an eventual login first)");
        System.out.println("\t3. After authorization, copy the verifier value from the URL");
        System.out.print("\t4. Enter the verifier here: ");
        return new Scanner(System.in, DEFAULT_ENCODING).next();
    }
 
    private static OAuthConsumerToken getAccessToken(OAuthConsumerSupport consumerSupport, OAuthConsumerToken requestToken, String verifier) {
        log.info("OAUTH: Access token: getting...");
        OAuthConsumerToken accessToken = consumerSupport.getAccessToken(requestToken, verifier);
        log.info("OAUTH: Access token: " + accessToken.getValue());
        log.info("OAUTH: Access token secret: " + accessToken.getSecret());
        return accessToken;
    }
 
    private static void getProtectedResource(OAuthConsumerSupport consumerSupport, OAuthConsumerToken accessToken) throws IOException {
        log.info("OAUTH: Getting protected resource");
        InputStream is = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            is = consumerSupport.readProtectedResource(new URL(RESOURCE_URL), accessToken, "GET");
            IOUtils.copy(is, baos);
            log.info("OAUTH: Resource : " + new String(baos.toByteArray(), DEFAULT_ENCODING));
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(baos);
        }
    }
}