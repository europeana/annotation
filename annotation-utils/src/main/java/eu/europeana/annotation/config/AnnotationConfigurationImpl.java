package eu.europeana.annotation.config;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import eu.europeana.annotation.definitions.model.search.SearchProfiles;


//@Configuration
//@ComponentScan("eu.europeana.annotation")
public class AnnotationConfigurationImpl implements AnnotationConfiguration {
    
    private Set<String> acceptedLicences;
    private boolean indexingEnabled;
    private String environment;
    private String annotationBaseUrl;
    private String defaultWhitelistResourcePath;
    private int maxpagesizeMinimal;
    private int maxpagesizeStandard;
    private String jwtTokenSignatureKey;
    private String authorizationApiName;
    private String transcriptionsLicenses;
    private int statsFacets;
    private String metisBaseUrl;
    private int metisConnectionRetries;
    private int metisConnectionTimeout;
    private String annotationApiVersion;
    private String annoApiEndpoint;
    private String annoUserDataEndpoint;
    private String annoClientApiEndpoint;
    private String annoItemDataEndpoint;
    private String mongoDatabaseName;
    private boolean annoRemoveAuthorization;
    private String solrUrls;    
    
    public String getComponentName() {
	return "annotation";
    }

    public boolean isIndexingEnabled() {
      return indexingEnabled;
    }

    public boolean isProductionEnvironment() {
	return VALUE_ENVIRONMENT_PRODUCTION.equals(getEnvironment());
    }

    public int getMaxPageSize(String profile) {
      switch(SearchProfiles.getByStr(profile)) {
        case MINIMAL:
          return maxpagesizeMinimal;
        case STANDARD:
          return maxpagesizeStandard;
        default:
          return maxpagesizeStandard;
      }
    }
    
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

    public String getEnvironment() {
      return environment;
    }

    public String getAnnotationBaseUrl() {
      return annotationBaseUrl;
    }

    public String getDefaultWhitelistResourcePath() {
      return defaultWhitelistResourcePath;
    }

    public int getMaxpagesizeMinimal() {
      return maxpagesizeMinimal;
    }

    public int getMaxpagesizeStandard() {
      return maxpagesizeStandard;
    }

    public String getJwtTokenSignatureKey() {
      return jwtTokenSignatureKey;
    }

    public String getAuthorizationApiName() {
      return authorizationApiName;
    }

    public String getTranscriptionsLicenses() {
      return transcriptionsLicenses;
    }

    public int getStatsFacets() {
      return statsFacets;
    }

    public String getMetisBaseUrl() {
      return metisBaseUrl;
    }

    public int getMetisConnectionRetries() {
      return metisConnectionRetries;
    }

    public int getMetisConnectionTimeout() {
      return metisConnectionTimeout;
    }

    public String getAnnotationApiVersion() {
      return annotationApiVersion;
    }

    public String getAnnoApiEndpoint() {
      return annoApiEndpoint;
    }

    public String getAnnoUserDataEndpoint() {
      return annoUserDataEndpoint;
    }

    public String getAnnoClientApiEndpoint() {
      return annoClientApiEndpoint;
    }

    public String getAnnoItemDataEndpoint() {
      return annoItemDataEndpoint;
    }

    public String getMongoDatabaseName() {
      return mongoDatabaseName;
    }

    public boolean getAnnoRemoveAuthorization() {
      return annoRemoveAuthorization;
    }

    public String getSolrUrls() {
      return solrUrls;
    }

    public void setEnvironment(String environment) {
      this.environment = environment;
    }

    public void setIndexingEnabled(boolean indexingEnabled) {
      this.indexingEnabled = indexingEnabled;
    }

    public void setAnnotationBaseUrl(String annotationBaseUrl) {
      this.annotationBaseUrl = annotationBaseUrl;
    }

    public void setDefaultWhitelistResourcePath(String defaultWhitelistResourcePath) {
      this.defaultWhitelistResourcePath = defaultWhitelistResourcePath;
    }

    public void setMaxpagesizeMinimal(int maxpagesizeMinimal) {
      this.maxpagesizeMinimal = maxpagesizeMinimal;
    }

    public void setMaxpagesizeStandard(int maxpagesizeStandard) {
      this.maxpagesizeStandard = maxpagesizeStandard;
    }

    public void setJwtTokenSignatureKey(String jwtTokenSignatureKey) {
      this.jwtTokenSignatureKey = jwtTokenSignatureKey;
    }

    public void setAuthorizationApiName(String authorizationApiName) {
      this.authorizationApiName = authorizationApiName;
    }

    public void setTranscriptionsLicenses(String transcriptionsLicenses) {
      this.transcriptionsLicenses = transcriptionsLicenses;
    }

    public void setStatsFacets(int statsFacets) {
      this.statsFacets = statsFacets;
    }

    public void setMetisBaseUrl(String metisBaseUrl) {
      this.metisBaseUrl = metisBaseUrl;
    }

    public void setMetisConnectionRetries(int metisConnectionRetries) {
      this.metisConnectionRetries = metisConnectionRetries;
    }

    public void setMetisConnectionTimeout(int metisConnectionTimeout) {
      this.metisConnectionTimeout = metisConnectionTimeout;
    }

    public void setAnnotationApiVersion(String annotationApiVersion) {
      this.annotationApiVersion = annotationApiVersion;
    }

    public void setAnnoApiEndpoint(String annoApiEndpoint) {
      this.annoApiEndpoint = annoApiEndpoint;
    }

    public void setAnnoUserDataEndpoint(String annoUserDataEndpoint) {
      this.annoUserDataEndpoint = annoUserDataEndpoint;
    }

    public void setAnnoClientApiEndpoint(String annoClientApiEndpoint) {
      this.annoClientApiEndpoint = annoClientApiEndpoint;
    }

    public void setAnnoItemDataEndpoint(String annoItemDataEndpoint) {
      this.annoItemDataEndpoint = annoItemDataEndpoint;
    }

    public void setMongoDatabaseName(String mongoDatabaseName) {
      this.mongoDatabaseName = mongoDatabaseName;
    }

    public void setAnnoRemoveAuthorization(boolean annoRemoveAuthorization) {
      this.annoRemoveAuthorization = annoRemoveAuthorization;
    }

    public void setSolrUrls(String solrUrls) {
      this.solrUrls = solrUrls;
    }

}
