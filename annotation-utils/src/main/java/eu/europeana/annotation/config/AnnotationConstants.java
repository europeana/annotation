package eu.europeana.annotation.config;

public interface AnnotationConstants {

    public static final String ANNOTATION_INDEXING_ENABLED = "annotation.indexing.enabled";
    public static final String ANNOTATION_ENVIRONMENT = "annotation.environment";
    public static final String ANNOTATION_SUBTITLES_FORMATS = "annotation.subtitles.formats";
    public static final String ANNOTATION_SUBTITLES_FORMATS_XML = "annotation.subtitles.formats.xml";
    public static final String BEAN_SUBTITLES_FORMATS = "subtitlesFormats";

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
    
    public static final String SOLR_STATS_FACETS = "solr.stats.facets";
    
    public static final String ANNO_API_ENDPOINT = "annotation.api.endpoint";
    public static final String ANNO_DATA_ENDPOINT = "annotation.data.endpoint";
    public static final String ANNO_USER_DATA_ENDPOINT = "annotation.user.data.endpoint";
    public static final String ANNO_CLIENT_API_ENDPOINT = "annotation.client.api.endpoint";
    public static final String ANNO_ITEM_DATA_ENDPOINT = "annotation.item.data.endpoint";
    public static final String MONGO_DATABASE_NAME = "mongodb.annotation.databasename";
    public static final String MONGO_COLLECTION_NAME = "annotation";
    public static final String ANNO_REMOVE_AUTHORIZATION = "annotation.remove.authorization";
    public static final String SOLR_URLS = "solr.annotation.url";
    
    public static final String VALIDATION_API = "api";
    public static final String VALIDATION_ADMIN_API_KEY = "adminapikey";
    public static final String VALIDATION_ADMIN_SECRET_KEY = "adminsecretkey";

    public static final String API_KEY_CACHING_TIME = "annotation.apikey.caching.time";

    public static final String ETAG_FORMAT = "application/json";

}