package eu.europeana.annotation.client.config;

import java.io.InputStream;
import java.util.Properties;

import eu.europeana.annotation.client.exception.TechnicalRuntimeException;

public class ClientConfiguration {

    protected static final String ANNOTATION_CLIENT_PROPERTIES_FILE = "/annotation-client.properties";
    protected static final String PROP_ANNOTATION_API_KEY = "annotation.api.key";
    protected static final String PROP_ANNOTATION_SERVICE_BASE_URI = "annotation.service.uri";
    protected static final String PROP_ANNOTATION_ID_BASE_URI = "annotation.id.baseUrl";
    protected static final String PROP_ANNOTATION_ITEM_DATA_ENDPOINT = "annotation.item.data.endpoint";
    protected static final String PROP_ANNOTATION_CLIENT_API_ENDPOINT = "annotation.client.api.endpoint";
   
    protected static final String PROP_AUTHORIZATION_HEADER_NAME = "annotation.header.name";
    protected static final String PROP_REGULAR_AUTHORIZATION_HEADER_VALUE = "annotation.regular.authorization.value";
    protected static final String PROP_ADMIN_ANNOTATION_HEADER_VALUE = "annotation.admin.authorization.value";

    protected static final String PROP_OAUTH_SERVICE_URI = "oauth.service.uri";
    protected static final String PROP_OAUTH_REQUEST_PARAMS_PREFIX = "oauth.token.request.params.";
    
    private static Properties properties = null;

    private static ClientConfiguration singleton;

    /**
     * Hide the default constructor
     */
    private ClientConfiguration() {
    };

    /**
     * Accessor method for the singleton
     * 
     * @return
     */
    public static synchronized ClientConfiguration getInstance() {
      if(singleton == null) {
        singleton = new ClientConfiguration();
        singleton.loadProperties();
      }
	return singleton;
    }

    /**
     * Laizy loading of configuration properties
     */
    public synchronized void loadProperties() {
	try {
	    properties = new Properties();
	    InputStream resourceAsStream = getClass().getResourceAsStream(ANNOTATION_CLIENT_PROPERTIES_FILE);
	    if (resourceAsStream != null)
		getProperties().load(resourceAsStream);
	    else
		throw new TechnicalRuntimeException(
			"No properties file found in classpath! " + ANNOTATION_CLIENT_PROPERTIES_FILE);

	} catch (Exception e) {
	    throw new TechnicalRuntimeException("Cannot read configuration file: " + ANNOTATION_CLIENT_PROPERTIES_FILE,
		    e);
	}

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
	return ANNOTATION_CLIENT_PROPERTIES_FILE;
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

    /**
     * This method provides access to the search uri value defined in the
     * configuration file
     * 
     * @see PROP_EUROPEANA_SEARCH_URI
     * 
     * @return
     */
    public String getServiceUri() {
	return getProperties().getProperty(PROP_ANNOTATION_SERVICE_BASE_URI).trim();
    }
    
    /**
     * This method provides access to the annotation id base uri value defined in the
     * configuration file
     * 
     * @see PROP_EUROPEANA_SEARCH_URI
     * 
     * @return
     */
    public String getAnnotationIdBaseUri() {
    return getProperties().getProperty(PROP_ANNOTATION_ID_BASE_URI).trim();
    }
    
   
    
    /**
     * This method provides access to the header name defined in the configuration
     * file
     * 
     * @see PROP_EUROPEANA_SEARCH_URI
     * 
     * @return
     */
    @Deprecated
    public String getHeaderName() {
	return getProperties().getProperty(PROP_AUTHORIZATION_HEADER_NAME).trim();
    }

    /**
     * This method provides access to the header value defined in the configuration
     * file
     * 
     * @return
     */
    public String getAuthorizationHeaderValue() {
	return getProperties().getProperty(PROP_REGULAR_AUTHORIZATION_HEADER_VALUE).trim();
    }

    /**
     * This method provides access to the header value defined in the configuration
     * file
     * 
     * @return
     */
    public String getAuthorizationHeaderValueForAdmin() {
	return getProperties().getProperty(PROP_ADMIN_ANNOTATION_HEADER_VALUE).trim();
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
