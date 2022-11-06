package eu.europeana.annotation.config;

import java.util.Set;

public interface AnnotationConfiguration {

    public static final String ANNOTATION_INDEXING_ENABLED = "annotation.indexing.enabled";
    public static final String ANNOTATION_AUTH_DISABLED = "annotation.auth.disabled";
    
    public static final String AUTHORIZATION_API_NAME = "authorization.api.name";
    public static final String KEY_APIKEY_JWTTOKEN_SIGNATUREKEY = "europeana.apikey.jwttoken.siganturekey";
    public static final String DEFAULT_WHITELIST_RESOURCE_PATH = "annotation.whitelist.default";
    public static final String METIS_BASE_URL = "metis.baseUrl";
    public static final String KEY_METIS_CONNECTION_RETRIES = "metis.connection.retries";
    public static final String KEY_METIS_CONNECTION_TIMEOUT = "metis.connection.timeout";
    public static final String TRANSCRIPTIONS_LICENSES = "annotation.licenses";
    public static final String PREFIX_MAX_PAGE_SIZE = "annotation.search.maxpagesize.";
    public static final String SOLR_STATS_FACETS = "solr.stats.facets";
    public static final String ANNO_API_ENDPOINT = "annotation.api.endpoint";
    public static final String ANNO_DATA_ENDPOINT = "annotation.data.endpoint";
    public static final String ANNO_USER_DATA_ENDPOINT = "annotation.user.data.endpoint";
    public static final String ANNO_CLIENT_DATA_ENDPOINT = "annotation.client.data.endpoint";
    public static final String ANNO_ITEM_DATA_ENDPOINT = "annotation.item.data.endpoint";
    public static final String MONGO_DATABASE_NAME = "mongodb.annotation.databasename";
    public static final String MONGO_COLLECTION_NAME = "annotation";
    public static final String ANNO_REMOVE_AUTHORIZATION = "annotation.remove.authorization";
    public static final String SOLR_URLS = "solr.annotation.url";
    public static final String BEAN_ANNO_SOLR_CLIENT = "annoSolrClient";
    public static final String BEAN_SOLR_ANNO_SERVICE = "solrAnnotationService";
    public static final String BEAN_ANNO_MONGO_STORE = "annotation_db_morphia_datastore_annotation";
    public static final String BEAN_METIS_DEREFERENCE_CLIENT = "metisDereferenceClient";
    public static final String BEAN_ANNO_SERVICE = "annotationService";
    public static final String VALIDATION_API = "api";
    public static final String VALIDATION_ADMIN_API_KEY = "adminapikey";
    public static final String VALIDATION_ADMIN_SECRET_KEY = "adminsecretkey";
    public static final String API_KEY_CACHING_TIME = "annotation.apikey.caching.time";
    
    
    /**
     * uses annotation.indexing.enabled property
     */
    public boolean isIndexingEnabled();


    public String getAnnotationBaseUrl();

    /**
     * uses annotation.whitelist.default property
     */
    public String getDefaultWhitelistResourcePath();

    public int getMaxPageSize(String profile);

    public String getJwtTokenSignatureKey();

    public String getAuthorizationApiName();

    public String getTranscriptionsLicenses();

 
    /**
     * This method retrieves a set of supported transcriptions licenses.
     * 
     * @return a set of transcriptions licenses
     */
    Set<String> getAcceptedLicenceses();

    int getMetisConnectionTimeout();

    int getMetisConnectionRetries();

    String getAnnoApiEndpoint();
    String getAnnoUserDataEndpoint();
    String getAnnoClientApiEndpoint();
    String getAnnoItemDataEndpoint();
    
    String getMongoDatabaseName();
    
    String getSolrUrls();

    boolean isAuthEnabled();
}
