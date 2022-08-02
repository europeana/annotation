package eu.europeana.annotation.config;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import eu.europeana.annotation.definitions.model.search.SearchProfiles;

@PropertySources({@PropertySource("classpath:config/annotation.properties"), @PropertySource(
    value = "classpath:config/annotation.user.properties", ignoreResourceNotFound = true)})
public class AnnotationConfiguration {

  private static final Logger logger = LogManager.getLogger(AnnotationConfiguration.class);

  @Value("${annotation.indexing.enabled:true}")
  private boolean indexingEnabled;

  @Value("${annotation.environment}")
  private String environment;

  @Value("${annotation.data.endpoint}")
  private String annoBaseUrl;

  @Value("${annotation.whitelist.default}")
  private String defaultWhitelistResourcePath;
  
  @Value("${europeana.apikey.jwttoken.siganturekey}")
  private String jwtTokenSignatureKey;
  
  @Value("${authorization.api.name}")
  private String authorizationApiName;
  
  @Value("${annotation.licenses}")
  private String transcriptionsLicenses;
  
  @Value("${solr.stats.facets:10}")
  private int statsFacets;

  private Set<String> acceptedLicences;
  
  @Value("${metis.baseUrl}")
  private String metisBaseUrl;
  
  @Value("${metis.connection.retries:1}")
  private int metisConnectionRetries;
  
  @Value("${metis.connection.timeout:1000}")
  private int metisConnectionTimeout;

  @Value("${annotation.apiVersion:null}")
  private String annotationApiVersion;
  
  @Value("${annotation.api.endpoint}")
  private String annoApiEndpoint;
  
  @Value("${annotation.user.data.endpoint}")
  private String annoUserDataEndpoint;
  
  @Value("${annotation.client.api.endpoint}")
  private String annoClientApiEndpoint;
  
  @Value("${annotation.item.data.endpoint}")
  private String annoItemDataEndpoint;
  
  @Value("${mongodb.annotation.databasename:annotation}")
  private String mongoDatabaseName;
  
  @Value("${annotation.authorization.enabled:true}")
  private boolean authorizationEnabled;
  
  @Value("${solr.annotation.url}")
  private String solrUrls;
  
  
  @Value("${annotation.search.maxpagesize.minimal:1000}")
  private int pageSizeMinimal;
  
  @Value("${annotation.search.maxpagesize.standard:100}")
  private int pageSizeStandard;
  
  @Value("${annotation.search.maxpagesize.dereference:100}")
  private int pageSizeDeref;
  
  public String getComponentName() {
    return "annotation";
  }

  public boolean isIndexingEnabled() {
    return indexingEnabled;
  }

  public boolean isProductionEnvironment() {
    return AnnotationConstants.VALUE_ENVIRONMENT_PRODUCTION.equals(environment);
  }

  public String getEnvironment() {
    return environment;
  }

  public String getAnnotationBaseUrl() {
    return annoBaseUrl;
  }

  public String getDefaultWhitelistResourcePath() {
//    return getAnnotationProperties()
//        .getProperty(AnnotationConstants.DEFAULT_WHITELIST_RESOURCE_PATH);
    return defaultWhitelistResourcePath;
  }

  public int getMaxPageSize(String profile) {
    SearchProfiles searchProfile = SearchProfiles.getByStr(profile);
    int maxPageSize = 100;
    switch(searchProfile) {
      case MINIMAL :
        maxPageSize = pageSizeMinimal;
        break;
      case DEREFERENCE :
        maxPageSize = pageSizeStandard;
        break;
      case STANDARD :
        maxPageSize = pageSizeStandard;
        break;
      default:
        break;  
    }
    
    return maxPageSize;
  }

  public String getJwtTokenSignatureKey() {
//    return getAnnotationProperties()
//        .getProperty(AnnotationConstants.KEY_APIKEY_JWTTOKEN_SIGNATUREKEY);
    return jwtTokenSignatureKey;
  }

  public String getAuthorizationApiName() {
    return authorizationApiName;
  }

  public String getTranscriptionsLicenses() {
//    return getAnnotationProperties().getProperty(AnnotationConstants.TRANSCRIPTIONS_LICENSES);
    return transcriptionsLicenses;
  }

  public int getStatsFacets() {
//    return toInt(getAnnotationProperties().getProperty(AnnotationConstants.SOLR_STATS_FACETS));
    return statsFacets;
  }

  public Set<String> getAcceptedLicenceses() {

    if (acceptedLicences == null && getTranscriptionsLicenses()!= null) {
      String[] licences = StringUtils.split(getTranscriptionsLicenses(), ",");
      acceptedLicences = Stream.of(licences).collect(Collectors.toCollection(HashSet::new));
    }

    return acceptedLicences;
  }

  public String getMetisBaseUrl() {
//    return getAnnotationProperties().getProperty(AnnotationConstants.METIS_BASE_URL);
    return metisBaseUrl;
  }

  public int getMetisConnectionRetries() {
//    String value =
//        getAnnotationProperties().getProperty(AnnotationConstants.KEY_METIS_CONNECTION_RETRIES);
//    return toInt(value);
    return metisConnectionRetries;
  }

  public int getMetisConnectionTimeout() {
//    String value =
//        getAnnotationProperties().getProperty(AnnotationConstants.KEY_METIS_CONNECTION_TIMEOUT);
//    return toInt(value);
    return metisConnectionTimeout;
  }

  int toInt(String value) {
    try {
      return Integer.valueOf(value);
    } catch (NumberFormatException e) {
      return -1;
    }
  }

  public String getAnnotationApiVersion() {
//    return getAnnotationProperties().getProperty(AnnotationConstants.API_VERSION);
    return annotationApiVersion;
  }

  public String getAnnoApiEndpoint() {
//    return getAnnotationProperties().getProperty(AnnotationConstants.ANNO_API_ENDPOINT);
    return annoApiEndpoint;
  }


  public String getAnnoUserDataEndpoint() {
//    return getAnnotationProperties().getProperty(AnnotationConstants.ANNO_USER_DATA_ENDPOINT);
    return annoUserDataEndpoint;
  }

  public String getAnnoClientApiEndpoint() {
//    return getAnnotationProperties().getProperty(AnnotationConstants.ANNO_CLIENT_API_ENDPOINT);
    return annoClientApiEndpoint;
  }

  public String getAnnoItemDataEndpoint() {
//    return getAnnotationProperties().getProperty(AnnotationConstants.ANNO_ITEM_DATA_ENDPOINT);
    return annoItemDataEndpoint;
  }

  public String getMongoDatabaseName() {
//    return getAnnotationProperties().getProperty(AnnotationConstants.MONGO_DATABASE_NAME);
    return mongoDatabaseName;
  }

  public boolean isAuthorizationEnabled() {
//    String value =
//        getAnnotationProperties().getProperty(AnnotationConstants.ANNO_REMOVE_AUTHORIZATION);
//    return Boolean.valueOf(value);
    return authorizationEnabled;
  }

  public String getSolrUrls() {
//    return getAnnotationProperties().getProperty(AnnotationConstants.SOLR_URLS);
    return solrUrls;
  }

  public void setEnvironment(String environment) {
    this.environment = environment;
  }
}
