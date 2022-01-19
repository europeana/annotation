package eu.europeana.annotation.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;


//@Configuration
//@ComponentScan("eu.europeana.annotation")
public class AnnotationConfigurationImpl implements AnnotationConfiguration {

    private Properties annotationProperties;
    private Set<String> acceptedLicences;

    @Override
    public String getComponentName() {
	return "annotation";
    }

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
    public boolean isProductionEnvironment() {
	return VALUE_ENVIRONMENT_PRODUCTION.equals(getEnvironment());
    }

    @Override
    public String getEnvironment() {
	return getAnnotationProperties().getProperty(ANNOTATION_ENVIRONMENT);
    }

    @Override
    public String getAnnotationBaseUrl() {
	String key = ANNOTATION_ENVIRONMENT + "." + getEnvironment() + "." + SUFFIX_BASEURL;
	return getAnnotationProperties().getProperty(key);
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


}
