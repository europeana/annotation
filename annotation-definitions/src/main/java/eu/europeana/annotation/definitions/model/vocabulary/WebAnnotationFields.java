package eu.europeana.annotation.definitions.model.vocabulary;

import eu.europeana.annotation.definitions.model.vocabulary.fields.WebAnnotationModelFields;

/**
 * @author GrafR
 * 
 */
// TODO: move the field values to other interfaces/enumerations
public interface WebAnnotationFields extends WebAnnotationModelFields{

	
	public static final String SPLITTER = "#";
	
	//TODO: move to appropriate location verify if still used
	public static final String DISABLED = "disabled";
	public static final String START_ON = "startOn";
	public static final String LIMIT = "limit";
	public static final String INPUT_STRING = "inputString";
	public static final String DIMENSION_MAP = "dimensionMap";
	public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
	public static final String MULTILINGUAL = "multilingual";
	public static final String TOTAL_ITEMS = "totalItems";
	
	
	// skos fields
	/*
	 * TODO: //remove skos concepts management and replace it with the Entity API
	 */
	@Deprecated 
	public static final String PREF_LABEL = "prefLabel";
	public static final String ALT_LABEL = "altLabel";
	public static final String HIDDEN_LABEL = "hiddenLabel";
	public static final String IN_SCHEME = "inScheme";
	public static final String TOP_CONCEPT_OF = "topConceptOf";
	public static final String SCOPE_NOTE = "scopeNote";
	public static final String DEFINITION = "definition";
	public static final String EXAMPLE = "example";
	public static final String HISTORY_NOTE = "historyNote";
	public static final String EDITORIAL_NOTE = "editorialNote";
	public static final String CHANGE_NOTE = "changeNote";
	public static final String HAS_TOP_CONCEPT = "hasTopConcept";
	public static final String NOTE = "note";
	public static final String SEMANTIC_RELATION = "semanticRelation";
	public static final String BROADER_TRANSITIVE = "broaderTransitive";
	public static final String NARROWER_TRANSITIVE = "narrowerTransitive";
	public static final String MEMBER = "member";
	public static final String MEMBER_LIST = "memberList";
	public static final String MAPPING_RELATION = "mappingRelation";
	public static final String BROAD_MATCH = "broadMatch";
	public static final String NARROW_MATCH = "narrowMatch";
	public static final String CLOSE_MATCH = "closeMatch";
	public static final String RELATE_MATCH = "relateMatch";
	public static final String EXACT_MATCH = "exactMatch";
	public static final String VOCABULARY = "vocabulary";
	public static final String ANCESTORS = "ancestors";

	// public static final String DEFAULT_MEDIA_TYPE = "oa:SemanticTag";
	public static final String ANNOTATION_TYPE = "Annotation";
	public static final String OA = "oa";
	public static final String WA_CONTEXT = "https://www.w3.org/ns/anno.jsonld";
	public static final String SEPARATOR_SEMICOLON = ":";

	// TAGS
	public static final String LABEL = "label";
	public static final String ID = "id";
	public static final String LANGUAGE = "language";
	public static final String VALUE = "value";
	public static final String TAG_TYPE = "tagType";
	public static final String TAG = "tag";
	public static final String LINK = "link";
	public static final String SPECIFIC_RESOURCE = "SpecificResource";

	public static final String DEFAULT_LANGUAGE = "EN";
	public static final String UNDERSCORE = "_";

	/**
	 * Path Params
	 */
	public static final String PATH_PARAM_ANNO_TYPE = "annoType";
	public static final String PATH_PARAM_FORMAT = "format";
	public static final String PATH_PARAM_PROVIDER = "provider";
	public static final String PATH_PARAM_IDENTIFIER = "identifier";
	public static final String PATH_FIELD_REPORT = "report";
	public static final String PATH_FIELD_MODERATION_SUMMARY = "moderationsummary";
	//Not needed yet
	//public static final String WHITELIST = "whitelist";

	public static final String FORMAT_JSONLD = "jsonld";

	/**
	 * Query Params
	 */
	public static final String PARAM_WSKEY = "wskey";
	public static final String PARAM_QUERY = "query";
	public static final String PARAM_QF = "qf";
	public static final String PARAM_FACET = "facet";
	public static final String PARAM_PROFILE = "profile";
	public static final String PARAM_START = "start";
	public static final String PARAM_ROWS = "rows";
	public static final String PARAM_SCORE = "score";
	public static final String PARAM_SORT = "sort";
	public static final String PARAM_SORT_ORDER = "sortOrder";
	public static final String COLON = ":";
	public static final String PARAM_INCLUDE_ERROR_STACK = "includeErrorStack";
	public static final String REQ_PARAM_PROVIDER = PATH_PARAM_PROVIDER;
	public static final String REQ_PARAM_IDENTIFIER = PATH_PARAM_IDENTIFIER;
	
	
	/**
	 * Query Fields
	 */
	public static final String FIELD_MODIFIED = "modified";
	public static final String FIELD_UPDATED_TIMESTAMP = "updated_timestamp";

	/**
	 * Search API response
	 */
	public static final String SEARCH_RESP_FACETS = "facets";
	public static final String SEARCH_RESP_FACETS_FIELD = "field";
	public static final String SEARCH_RESP_FACETS_VALUES = "values";
	public static final String SEARCH_RESP_FACETS_LABEL = "label";
	public static final String SEARCH_RESP_FACETS_COUNT = "count";
	
	
	/**
	 * AnnotationId
	 */
	public static final String ANNOTATION_ID_PREFIX = "http://data.europeana.eu/annotation";
	public static final String SLASH = "/";
	public static final int MIN_ANNOTATION_ID_COMPONENT_COUNT = 4;
	public static final int MIN_HISTORY_PIN_COMPONENT_COUNT = 3;
	public static final String PROVIDER_WEBANNO = "webanno";
	public static final String PROVIDER_EUROPEANA_DEV = "eanadev";
	public static final String PROVIDER_HISTORY_PIN = "historypin";
	public static final String PROVIDER_PUNDIT = "pundit";
	public static final String PROVIDER_WITH = "with";
	public static final String PROVIDER_BASE = "base";
	public static final String PROVIDER_COLLECTIONS = "collections";
	public static final String PROVIDER_WRONG = "wrong";
	public static final String TEST_HISTORYPIN_URL = "http://historypin.com/annotation/1234";
	public static final String HTTP = "http://";

	/**
	 * Markup
	 */
	public static final String MARKUP_ITEM = "/item";
	public static final String MARKUP_RECORD = "/record";
	
	
	
	/**
	 * 
	 */
	// TODO: move to constant to authentication interfaces/classes
	public static final String USER_ANONYMOUNS = "anonymous";
	public static final String USER_ADMIN = "admin";

	/**
	 * SKOS concept
	 */
	public static final String CONCEPT = "concept";
	public static final String NOTATION = "notation";
	public static final String CONTAINER = "container";
	public static final String NARROWER = "narrower";
	public static final String BROADER = "broader";
	public static final String RELATED = "related";
	public static final String SKOS = "skos";

	/**
	 * AnnotationLd
	 */
	public static final String AND = "&";
	public static final String EQUALS = "=";
	public static final String INDEXING = "indexing";
	public static final String INDEX_ON_CREATE = "indexOnCreate";
	public static final String PROVIDER = "provider";
	public static final String USER = "user";
	public static final String USER_TOKEN = "userToken";
	public static final String IDENTIFIER = "identifier";
	public static final String RESOURCE_ID = "resourceId";
	public static final String JSON_REST = ".json";
	public static final String JSON_LD_REST = ".jsonld";
	public static final String ANNOTATION_JSON_LD_REST = "annotation.jsonld";
	public static final String ANNOTATION_LD_JSON_LD_REST = "annotationld.jsonld";
	public static final String CONCEPT_JSON_REST = "uri.json";
	public static final String SEARCH_JSON_LD_REST = "search.jsonld";
	public static final String PAR_CHAR = "?";
	public static final String COLLECTION = "collection";
	public static final String OBJECT = "object";

	/**
	 * Error messages
	 */
	public static final String SUCCESS_TRUE = "\"success\":true";
	public static final String SUCCESS_FALSE = "\"success\":false";
	public static final String INVALID_PROVIDER = "Invalid provider!";
	public static final String UNNECESSARY_ANNOTATION_NR = "AnnotationNr must not be set for provider!";

	/**
	 * SKOS parsing
	 */
	public static final String ADD = "add";
	public static final String GET = "get";
	public static final String IN_MAPPING = "InMapping";

	/**
	 * Mongo
	 */
	public static final String MONGO_ID = "_id";

	/**
	 * Default values for the Rest API services
	 */
	public static final String DEFAULT_PROVIDER = "webanno";
	
	public static final String REST_COLLECTION = "15502";
	public static final String REST_OBJECT = "GG_8285";
	public static final String REST_RESOURCE_ID = SLASH + REST_COLLECTION + SLASH + REST_OBJECT;
	public static final String REST_ANNOTATION_NR = "1";
	public static final String REST_ANNOTATION_ID = DEFAULT_PROVIDER + SLASH + REST_ANNOTATION_NR;
	public static final String REST_TAG_ID = "5506c37343247ba48753d1e5";
	public static final String REST_LANGUAGE = "en";
	public static final String REST_START_ON = "0";
	public static final String REST_LIMIT = "10";
	public static final String REST_DEFAULT_PROVIDER_ID_GENERATION_TYPE = "provided";
	public static final String ITEMS_COUNT = "itemsCount";
	public static final String URI = "uri";
	public static final String WHITELIST = "whitelist";

	/**
	 * These namespace prefixes are employed for evaluation of the internal type
	 * of the objects in Annotation.
	 */
	public enum TypeNamespaces {
		oa, foaf, prov;
	}
}
