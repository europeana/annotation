package eu.europeana.annotation.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;


public class AnnotationConfigurationImpl implements AnnotationConfiguration {

  private Properties annotationProperties;
  private Set<String> acceptedLicences;

  @Override
  public boolean isIndexingEnabled() {
    String value = getAnnotationProperties().getProperty(ANNOTATION_INDEXING_ENABLED);
    return Boolean.valueOf(value);
  }

  public Properties getAnnotationProperties() {
    return annotationProperties;
  }

  public void setAnnotationProperties(Properties annotationProperties) {
    this.annotationProperties = annotationProperties;
  }

 
  @Override
  public String getAnnotationBaseUrl() {
    return getAnnotationProperties().getProperty(ANNO_DATA_ENDPOINT);
  }

  public String getDefaultWhitelistResourcePath() {
    return getAnnotationProperties().getProperty(DEFAULT_WHITELIST_RESOURCE_PATH);
  }

  public int getMaxPageSize(String profile) {
    String key = PREFIX_MAX_PAGE_SIZE + profile;
    return Integer.parseInt(getAnnotationProperties().getProperty(key));
  }

  public String getJwtTokenSignatureKey() {
    return getAnnotationProperties().getProperty(KEY_APIKEY_JWTTOKEN_SIGNATUREKEY);
  }

  @Override
  public String getAuthorizationApiName() {
    return getAnnotationProperties().getProperty(AUTHORIZATION_API_NAME);
  }

  @Override
  public String getTranscriptionsLicenses() {
    return getAnnotationProperties().getProperty(TRANSCRIPTIONS_LICENSES);
  }

  @Override
  /*
   * (non-Javadoc)
   * 
   * @see eu.europeana.annotation.config.AnnotationConfiguration#getAcceptedLicenceses( )
   */
  public Set<String> getAcceptedLicenceses() {

    if (acceptedLicences == null) {
      String[] licences = StringUtils.split(getTranscriptionsLicenses(), ",");
      acceptedLicences = Stream.of(licences).collect(Collectors.toCollection(HashSet::new));
    }

    return acceptedLicences;
  }

  @Override
  public String getMetisBaseUrl() {
    return getAnnotationProperties().getProperty(METIS_BASE_URL);
  }

  @Override
  public int getMetisConnectionRetries() {
    String value = getAnnotationProperties().getProperty(KEY_METIS_CONNECTION_RETRIES);
    return toInt(value);
  }

  @Override
  public int getMetisConnectionTimeout() {
    String value = getAnnotationProperties().getProperty(KEY_METIS_CONNECTION_TIMEOUT);
    return toInt(value);
  }

  int toInt(String value) {
    try {
      return Integer.valueOf(value);
    } catch (NumberFormatException e) {
      return -1;
    }
  }

  @Override
  public String getAnnoApiEndpoint() {
    return getAnnotationProperties().getProperty(ANNO_API_ENDPOINT);
  }

  @Override
  public String getAnnoUserDataEndpoint() {
    return getAnnotationProperties().getProperty(ANNO_USER_DATA_ENDPOINT);
  }

  @Override
  public String getAnnoClientApiEndpoint() {
    return getAnnotationProperties().getProperty(ANNO_CLIENT_API_ENDPOINT);
  }

  @Override
  public String getAnnoItemDataEndpoint() {
    return getAnnotationProperties().getProperty(ANNO_ITEM_DATA_ENDPOINT);
  }

  @Override
  public String getMongoDatabaseName() {
    return getAnnotationProperties().getProperty(MONGO_DATABASE_NAME);
  }

  @Override
  public String getSolrUrls() {
    return getAnnotationProperties().getProperty(SOLR_URLS);
  }

  public boolean isAuthDisabled() {
    return Boolean.valueOf(
        getAnnotationProperties().getProperty(ANNOTATION_AUTH_DISABLED, Boolean.FALSE.toString()));
  }
  
  @Override
  public boolean isAuthEnabled() {
    return !isAuthDisabled();
  }
  
  /**
   * utility method to be used in unit tests
   * @throws IOException 
   */
  public void loadProperties() throws IOException{
    Properties props = new Properties();
    InputStream annoPropsStream = getClass().getResourceAsStream("/config/annotation.properties");
    props.load(annoPropsStream);
    InputStream userPropsStream = getClass().getResourceAsStream("/config/annotation.user.properties");
    if(userPropsStream != null) {
      props.load(userPropsStream); 
    }
    setAnnotationProperties(props);
  }
}
