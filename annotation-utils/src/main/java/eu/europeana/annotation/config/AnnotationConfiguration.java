package eu.europeana.annotation.config;

import java.util.Set;

public interface AnnotationConfiguration {

    String ANNOTATION_INDEXING_ENABLED = "annotation.indexing.enabled";
    String ANNOTATION_AUTH_DISABLED = "annotation.auth.disabled";
    
    String AUTHORIZATION_API_NAME = "authorization.api.name";
    String KEY_APIKEY_JWTTOKEN_SIGNATUREKEY = "europeana.apikey.jwttoken.siganturekey";
    String DEFAULT_WHITELIST_RESOURCE_PATH = "annotation.whitelist.default";
    String METIS_BASE_URL = "metis.baseUrl";
    String KEY_METIS_CONNECTION_RETRIES = "metis.connection.retries";
    String KEY_METIS_CONNECTION_TIMEOUT = "metis.connection.timeout";
    String TRANSCRIPTIONS_LICENSES = "annotation.licenses";
    String PREFIX_MAX_PAGE_SIZE = "annotation.search.maxpagesize.";
    String SOLR_STATS_FACETS = "solr.stats.facets";
    String ANNO_API_ENDPOINT = "annotation.api.endpoint";
    String ANNO_DATA_ENDPOINT = "annotation.data.endpoint";
    String ANNO_USER_DATA_ENDPOINT = "annotation.user.data.endpoint";
    String ANNO_CLIENT_API_ENDPOINT = "annotation.client.api.endpoint";
    String ANNO_ITEM_DATA_ENDPOINT = "annotation.item.data.endpoint";
    String MONGO_DATABASE_NAME = "mongodb.annotation.databasename";
    String MONGO_COLLECTION_NAME = "annotation";
    String ANNO_REMOVE_AUTHORIZATION = "annotation.remove.authorization";
    String SOLR_URLS = "solr.annotation.url";
    String BEAN_ANNO_SOLR_CLIENT = "annoSolrClient";
    String BEAN_SOLR_ANNO_SERVICE = "solrAnnotationService";
    String BEAN_ANNO_MONGO_STORE = "annotation_db_morphia_datastore_annotation";
    String BEAN_METIS_DEREFERENCE_CLIENT = "metisDereferenceClient";
    String BEAN_ANNO_SERVICE = "annotationService";
    String VALIDATION_API = "api";
    String VALIDATION_ADMIN_API_KEY = "adminapikey";
    String VALIDATION_ADMIN_SECRET_KEY = "adminsecretkey";
    String API_KEY_CACHING_TIME = "annotation.apikey.caching.time";
    String ANNO_MEDIA_FORMATS = "annotation.media.formats";
    String SEARCH_API_BASE_URL = "searchApi.baseUrl";
    
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
    String getSearchApiBaseUrl();
}
