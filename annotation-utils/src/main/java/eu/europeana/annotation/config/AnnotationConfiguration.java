package eu.europeana.annotation.config;

import java.util.Set;

public interface AnnotationConfiguration {

    public static final String ANNOTATION_INDEXING_ENABLED = "annotation.indexing.enabled";
    public static final String ANNOTATION_ENVIRONMENT = "annotation.environment";
    public static final String ANNOTATION_SUBTITLES_FORMATS = "annotation.subtitles.formats";
    public static final String ANNOTATION_SUBTITLES_FORMATS_XML = "annotation.subtitles.formats.xml";
    public static final String BEAN_SUBTITLES_FORMATS = "subtitlesFormats";

    public static final String SUFFIX_BASEURL = "baseUrl";
    public static final String VALUE_ENVIRONMENT_PRODUCTION = "production";
    public static final String VALUE_ENVIRONMENT_TEST = "test";
    public static final String VALUE_ENVIRONMENT_DEVELOPMENT = "development";

    public static final String AUTHORIZATION_API_NAME = "authorization.api.name";
    public static final String KEY_APIKEY_JWTTOKEN_SIGNATUREKEY = "europeana.apikey.jwttoken.siganturekey";
    public static final String DEFAULT_WHITELIST_RESOURCE_PATH = "annotation.whitelist.default";

    public static final String METIS_BASE_URL = "metis.baseUrl";
    public static final String KEY_METIS_CONNECTION_RETRIES = "metis.connection.retries";
    public static final String KEY_METIS_CONNECTION_TIMEOUT = "metis.connection.timeout";

    public static final String TRANSCRIPTIONS_LICENSES = "annotation.licenses";

    public static final String PREFIX_MAX_PAGE_SIZE = "annotation.search.maxpagesize.";

    public static final String API_VERSION = "annotation.apiVersion";
    
    public static final String ANNOTATION_SCENARIO_TRANSCRIPTIONS = "transcriptions";
    public static final String ANNOTATION_SCENARIO_SUBTITLES = "subtitles";
    public static final String ANNOTATION_SCENARIO_SEMANTIC_TAGS = "semantic tags";
    public static final String ANNOTATION_SCENARIO_SIMPLE_TAGS = "simple tags";
    public static final String ANNOTATION_SCENARIO_GEO_TAGS = "geo tags";
    public static final String ANNOTATION_SCENARIO_OBJECT_LINKS = "object links";

    public String getComponentName();

    /**
     * uses annotation.indexing.enabled property
     */
    public boolean isIndexingEnabled();

    /**
     * checks annotation.environment=production property
     */
    public boolean isProductionEnvironment();

    /**
     * uses annotation.environment property
     */
    public String getEnvironment();

    /**
     * uses annotation.environment.{$environment}.baseUrl property
     */
    public String getAnnotationBaseUrl();

    /**
     * uses annotation.whitelist.default property
     */
    public String getDefaultWhitelistResourcePath();

    public int getMaxPageSize(String profile);

    public String getJwtTokenSignatureKey();

    public String getAuthorizationApiName();

    /**
     * uses metis.baseUrl property
     */
    public String getMetisBaseUrl();

    public String getTranscriptionsLicenses();

    public static final String VALIDATION_API = "api";
    public static final String VALIDATION_ADMIN_API_KEY = "adminapikey";
    public static final String VALIDATION_ADMIN_SECRET_KEY = "adminsecretkey";

    public static final String API_KEY_CACHING_TIME = "annotation.apikey.caching.time";

    public static final String ETAG_FORMAT = "application/json";

    /**
     * This method retrieves a set of supported transcriptions licenses.
     * 
     * @return a set of transcriptions licenses
     */
    public Set<String> getAcceptedLicenceses();

    /**
     * uses annotation.apiVersion property
     */
    public String getAnnotationApiVersion();

    int getMetisConnectionTimeout();

    int getMetisConnectionRetries();

}
