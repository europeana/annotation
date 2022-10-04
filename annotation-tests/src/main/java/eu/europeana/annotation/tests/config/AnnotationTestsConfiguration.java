package eu.europeana.annotation.tests.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AnnotationTestsConfiguration {

    protected static final String ANNOTATION_TESTS_PROPERTIES_FILE = "/annotation-tests.properties";
    protected static final String PROP_ANNOTATION_API_KEY = "annotation.api.key";
    protected static final String PROP_ANNOTATION_ITEM_DATA_ENDPOINT = "annotation.item.data.endpoint";
    protected static final String PROP_ANNOTATION_CLIENT_API_ENDPOINT = "annotation.client.api.endpoint";

    protected static final String PROP_OAUTH_SERVICE_URI = "oauth.service.uri";
    protected static final String PROP_OAUTH_REQUEST_PARAMS_PREFIX = "oauth.token.request.params.";
    
    public static final String BASE_SERVICE_URL = "/annotation/";
    public static final String BASE_SERVICE_URL_WITH_S = "/annotations/";
    public static final String BASE_SERVICE_URL_ADMIN = "/admin/annotation/";
    public static final String BASE_SERVICE_URL_WHITELIST = "/whitelist/";
    
    private static Properties properties = null;

    private static AnnotationTestsConfiguration singleton;

    /**
     * Hide the default constructor
     */
    private AnnotationTestsConfiguration() {
    };

    /**
     * Accessor method for the singleton
     * 
     * @return
     * @throws IOException 
     */
    public static synchronized AnnotationTestsConfiguration getInstance() throws IOException {
      if(singleton==null) {
    	singleton = new AnnotationTestsConfiguration();
    	singleton.loadProperties();
      }
      return singleton;
    }

    /**
     * Laizy loading of configuration properties
     * @throws IOException 
     */
    public synchronized void loadProperties() throws IOException {
	    properties = new Properties();
	    InputStream resourceAsStream = getClass().getResourceAsStream(ANNOTATION_TESTS_PROPERTIES_FILE);
		getProperties().load(resourceAsStream);
    }

    /**
     * provides access to the configuration properties. It is not recommended to use
     * the properties directly, but the
     * 
     * @return
     */
    Properties getProperties() {
	return properties;
    }

    /**
     * 
     * @return the name of the file storing the client configuration
     */
    String getConfigurationFile() {
	return ANNOTATION_TESTS_PROPERTIES_FILE;
    }

    /**
     * This method provides access to the API key defined in the configuration file
     * 
     * @see PROP_EUROPEANA_API_KEY
     * 
     * @return
     */
    public String getApiKey() {
	return getProperties().getProperty(PROP_ANNOTATION_API_KEY).trim();
    }    
 
    public String getOauthServiceUri() {
	return getProperties().getProperty(PROP_OAUTH_SERVICE_URI).trim();	
    }
    
    public String getOauthRequestParams(String user) {
	return getProperties().getProperty(PROP_OAUTH_REQUEST_PARAMS_PREFIX + user).trim();	
    }

    public String getPropAnnotationItemDataEndpoint() {
      return getProperties().getProperty(PROP_ANNOTATION_ITEM_DATA_ENDPOINT).trim();
    }

    public String getPropAnnotationClientApiEndpoint() {
      return getProperties().getProperty(PROP_ANNOTATION_CLIENT_API_ENDPOINT).trim();
    }
    
}
