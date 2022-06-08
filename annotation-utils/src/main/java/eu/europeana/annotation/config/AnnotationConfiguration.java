package eu.europeana.annotation.config;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@PropertySources({ @PropertySource("classpath:config/annotation.properties"),
    @PropertySource(value = "classpath:config/annotation.user.properties", ignoreResourceNotFound = true) })
public class AnnotationConfiguration {
    
    private static final Logger logger = LogManager.getLogger(AnnotationConfiguration.class);

    @Value("${annotation.indexing.enabled}")
    private boolean indexingEnabled;

    @Value("${annotation.environment}")
    private String environment;

    @Value("${annotation.data.endpoint}")
    private String annoBaseUrl;
    
    
    
    

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
    public int getStatsFacets() {
      return toInt(getAnnotationProperties().getProperty(SOLR_STATS_FACETS));
    }
    
    
    @Override
    /*
     * (non-Javadoc)
     * 
     * @see
     * eu.europeana.annotation.config.AnnotationConfiguration#getAcceptedLicenceses(
     * )
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
    public String getAnnotationApiVersion() {
	return getAnnotationProperties().getProperty(API_VERSION);
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
    public boolean getAnnoRemoveAuthorization() {
      String value = getAnnotationProperties().getProperty(ANNO_REMOVE_AUTHORIZATION);
      return Boolean.valueOf(value);
    }

    @Override
    public String getSolrUrls() {
      return getAnnotationProperties().getProperty(SOLR_URLS);
    }
}
