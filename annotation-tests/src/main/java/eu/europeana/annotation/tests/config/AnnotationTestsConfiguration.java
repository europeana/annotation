package eu.europeana.annotation.tests.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnnotationTestsConfiguration {

  private static final String DEFAULT_DATA_EUROPEANA_ITEM_URI      = "http://data.europeana.eu/item";
  protected static final String ANNOTATION_TESTS_PROPERTIES_FILE   = "/annotation-tests.user.properties";
  protected static final String PROP_ANNOTATION_API_KEY            = "annotation.api.key";
  protected static final String PROP_ANNOTATION_ITEM_DATA_ENDPOINT = "annotation.item.data.endpoint";

  protected static final String PROP_OAUTH_SERVICE_URI             = "keycloak.token.endpoint";
  protected static final String PROP_OAUTH_REQUEST_PARAMS          = "keycloak.token.grant.params";

  public static final String BASE_SERVICE_URL                      = "/annotation/";
  public static final String BASE_SERVICE_URL_WITH_S               = "/annotations/";
  public static final String BASE_SERVICE_URL_ADMIN                = "/admin/annotation/";
  public static final String BASE_SERVICE_URL_WHITELIST            = "/whitelist/";

  private static Properties properties                             = new Properties();

  private static AnnotationTestsConfiguration singleton;

  Logger logger = LogManager.getLogger(getClass());

  /**
   * Hide the default constructor
   */
  private AnnotationTestsConfiguration() {};

  /**
   * Accessor method for the singleton
   * 
   * @return
   * @throws IOException
   */
  public static synchronized AnnotationTestsConfiguration getInstance() throws IOException {
    if (singleton == null) {
      singleton = new AnnotationTestsConfiguration();
      singleton.loadProperties();
    }
    return singleton;
  }

  /**
   * Laizy loading of configuration properties
   * 
   * @throws IOException
   */
  protected synchronized void loadProperties() throws IOException {
    InputStream resourceAsStream = getClass().getResourceAsStream(ANNOTATION_TESTS_PROPERTIES_FILE);
    if (resourceAsStream != null) {
      getProperties().load(resourceAsStream);
    } else {
      // no properties file to load
      logger.debug("No test properties file available in the classpath");
    }

  }

  /**
   * provides access to the configuration properties. It is not recommended to use the properties
   * directly, but the
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
   * Retrieves the API key from the configuration properties.
   * If the property is not available, a default value of "TEST-API-KEY" is returned.
   *
   * @return The API key as a trimmed string.
   */
  public String getApiKey() {
    return getProperties().getProperty(PROP_ANNOTATION_API_KEY, "TEST-API-KEY").trim();
  }

  public String getOauthServiceUri() {
    return getProperties().getProperty(PROP_OAUTH_SERVICE_URI, "").trim();
  }

  public String getOauthRequestParams(String user) {
    return getProperties().getProperty(PROP_OAUTH_REQUEST_PARAMS + user, "").trim();
  }

  public String getPropAnnotationItemDataEndpoint() {
    return getProperties()
        .getProperty(PROP_ANNOTATION_ITEM_DATA_ENDPOINT, DEFAULT_DATA_EUROPEANA_ITEM_URI).trim();
  }

}
