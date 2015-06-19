package eu.europeana.annotation.definitions.model;


public interface WebAnnotationFields {

	/**
	 * Helping constants for Annotation mapping to the JSON-LD format 
	 */
//	public static final String EU_TYPE              = "euType";
	public static final String INTERNAL_TYPE        = "oa";
	public static final String CONTEXT              = "@context";
	public static final String SPLITTER             = "#";
	public static final String ANNOTATED_BY         = "annotatedBy";
	public static final String ANNOTATED_AT         = "annotatedAt";
	public static final String NAME                 = "name";
	public static final String SERIALIZED_AT        = "serializedAt";
	public static final String SERIALIZED_BY        = "serializedBy";
	public static final String FOAF_HOMEPAGE        = "foaf:homepage";
	public static final String MOTIVATION           = "motivation";
	public static final String STYLED_BY            = "styledBy";
	public static final String SAME_AS              = "sameAs";
	public static final String EQUIVALENT_TO        = "equivalentTo";
	public static final String CONTENT_TYPE         = "contentType";
	public static final String HTTP_URI             = "httpUri";
	public static final String SOURCE               = "source";
	public static final String SELECTOR             = "selector";
	public static final String STYLE_CLASS          = "styleClass";
	public static final String AT_TYPE              = "@type";
	public static final String INPUT_STRING         = "inputString";
	public static final String BODY                 = "body";
	public static final String BODY_TYPE            = "bodyType";
//	public static final String TARGET_TYPE          = "targetType";
	public static final String DIMENSION_MAP        = "dimensionMap";
	public static final String CHARS                = "chars";
	public static final String DC_LANGUAGE          = "language";
	public static final String FORMAT               = "format";
	public static final String MEDIA_TYPE           = "mediaType";
	public static final String FOAF_PAGE            = "foaf:page";
	public static final String FOAF                 = "foaf";
	public static final String TARGET               = "target";
	public static final String AT_ID               = "@id";
	public static final String TYPE                 = "type";
	public static final String ANNOTATION_ID        = "annotationId";
	public static final String DATE_FORMAT          = "yyyy-MM-dd'T'HH:mm:ss";
	public static final String LAST_INDEXED_TIMESTAMP = "lastIndexedTimestamp";
	public static final String MULTILINGUAL         = "multilingual";
	public static final String PREF_LABEL           = "prefLabel";
	public static final String ALT_LABEL            = "altLabel";
	public static final String HIDDEN_LABEL         = "hiddenLabel";
	public static final String IN_SCHEME            = "inScheme";
	public static final String TOP_CONCEPT_OF       = "topConceptOf";
	public static final String DEFAULT_MEDIA_TYPE   = "[oa:SemanticTag]";
	public static final String DEFAULT_ANNOTATION_TYPE = "[oa:annotation,euType:OBJECT_TAG]";
	public static final String OA                   = "oa";
	public static final String OA_CONTEXT            = "http://www.w3.org/ns/oa-context-20130208.json";
	public static final String SEPARATOR_SEMICOLON  = ":";
	
	public static final String LABEL                = "label";
	public static final String ID                   = "id";
	public static final String LANGUAGE             = "language";
	public static final String VALUE                = "value";
	public static final String TAG_TYPE             = "tagType";
	
	public static final String DEFAULT_LANGUAGE = "EN";
	public static final String UNDERSCORE = "_";

	/**
	 * AnnotationId
	 */
	public static final String ANNOTATION_ID_PREFIX = "http://data.europeana.eu/annotation"; 
	public static final String SLASH = "/"; 
	public static final int MIN_ANNOTATION_ID_COMPONENT_COUNT = 4; 
	public static final int MIN_HISTORY_PIN_COMPONENT_COUNT = 3; 
	public static final String PROVIDER_HISTORY_PIN = "historypin"; 
	public static final String PROVIDER_WRONG = "wrong"; 
	public static final String TEST_HISTORYPIN_URL = "http://historypin.com/annotation/1234";
	public static final String HTTP = "http://"; 
	
	/**
	 * SKOS concept
	 */
	public static final String CONCEPT   = "concept";
	public static final String NOTATION  = "notation";
	public static final String CONTAINER = "container";
	public static final String NARROWER  = "narrower";
	public static final String BROADER   = "broader";
	public static final String RELATED   = "related";
	public static final String SKOS      = "skos";
	
	/**
	 * Notes
	 */
	public static final String SAMPLES_JSON_LINK = "Please find JSON samples for annotation in <a href=\"templates.jsp\" target=\"_blank\">templates</a>";
	public static final String SAMPLES_JSONLD_LINK = "Please find JSON-LD samples for annotation in <a href=\"templates.jsp\" target=\"_blank\">templates</a>. " +
			"Provider parameter is optional but checked if provided. For now historypin is the only accepted provider. AnnotationNr is optional, but if provided a valid provider must be submitted";
	public static final String SEARCH_FIELDS_LINK = "Valid fields are 'all', 'label', 'body_value', 'tag_id', 'multilingual'.";

	/**
	 * Solr query
	 */
	public static final String ALL_SOLR_ENTRIES = "*:*";
	public static final String DELIMETER = ":";
	
	public static final String PROVIDER_WEBANNO = "webanno";	
	
	/**
	 * AnnotationLd
	 */
	public static final String AND              = "&"; 
	public static final String EQUALS           = "="; 
	public static final String WSKEY            = "wskey"; 
	public static final String INDEXING         = "indexing"; 
	public static final String PROVIDER         = "provider"; 
	public static final String ANNOTATION_NR    = "annotationNr"; 
	public static final String RESOURCE_ID      = "resourceId"; 
	public static final String JSON_REST        = ".json";
	public static final String ANNOTATION_JSON_LD_REST = "annotation.jsonld";
	public static final String ANNOTATION_LD_JSON_LD_REST = "annotationld.jsonld";
	public static final String CONCEPT_JSON_REST = "uri.json";
	public static final String SEARCH_JSON_LD_REST = "search.jsonld";
	public static final String PAR_CHAR         = "?";
	public static final String COLLECTION       = "collection";
	public static final String OBJECT           = "object";
	
	/**
	 * Error messages
	 */
	public static final String SUCCESS_TRUE = "\"success\":true";
	public static final String SUCCESS_FALSE = "\"success\":false";
	public static final String INVALID_PROVIDER = "Invalid provider!";
	public static final String INVALID_ANNOTATION_NR = "Invalid annotationNr for provider!";
	public static final String UNNECESSARY_ANNOTATION_NR = "AnnotationNr must not be set for provider!";
	
	/**
	 * Default values for the Rest API services
	 */
	public static final String REST_COLLECTION      = "15502";
	public static final String REST_OBJECT          = "GG_8285";
	public static final String REST_PROVIDER        = "webanno";
	public static final String REST_RESOURCE_ID     = SLASH + REST_COLLECTION + SLASH + REST_OBJECT;
	public static final String REST_ANNOTATION_NR   = "1";
	public static final String REST_TAG_ID          = "5506c37343247ba48753d1e5";
	public static final String REST_LANGUAGE        = "en";
	public static final String REST_START_ON        = "0";
	public static final String REST_LIMIT           = "10";
	public static final String REST_DEFAULT_PROVIDER_ID_GENERATION_TYPE = "provided";
	public static final String ITEMS_COUNT          = "itemsCount";
	public static final String URI                  = "uri";
	public static final String REST_ANNOTATION_JSON = 
		"{\"annotatedAt\": 1403852113248,"
		+ "\"type\": \"OBJECT_TAG\","
		+ "\"annotatedBy\": {"
		+ "\"agentType\": \"[foaf:Person, euType:PERSON]\","
		+ "\"name\": \"annonymous web user\","
		+ "\"homepage\": null,"
		+ "\"mbox\": null,"
		+ "\"openId\": null},"
		+ "\"body\": {"
		+ "\"contentType\": \"Link\","
		+ "\"mediaType\": null,"
		+ "\"httpUri\": \"https://www.freebase.com/m/035br4\","
		+ "\"language\": \"ro\","
		+ "\"value\": \"Vlad Tepes\","
		+ "\"multilingual\": \"[ro:Vlad Tepes,en:Vlad the Impaler]\","
		+ "\"bodyType\": \"[oa:Tag,euType:SEMANTIC_TAG]\"},"
		+ "\"target\": {"
		+ "\"contentType\": \"text-html\","
		+ "\"mediaType\": \"image\","
		+ "\"language\": \"en\","
		+ "\"value\": \"Vlad IV. Tzepesch, der Pfähler, Woywode der Walachei 1456-1462 (gestorben 1477)\","
		+ "\"httpUri\": \"http://europeana.eu/portal/record/15502/GG_8285.html\","
		+ "\"targetType\": \"[euType:WEB_PAGE]\"},"
		+ "\"serializedAt\": \"\","
		+ "\"serializedBy\": {"
		+ "\"agentType\": \"[prov:SoftwareAgent,euType:SOFTWARE_AGENT]\","
		+ "\"name\": \"annonymous web user\","
		+ "\"homepage\": null,"
		+ "\"mbox\": null,"
		+ "\"openId\": null},"
		+ "\"styledBy\":{"
		+ "\"contentType\": \"style\","
		+ "\"mediaType\": \"text/css\","
		+ "\"httpUri\": \"http://annotorious.github.io/latest/themes/dark/annotorious-dark.css\","
		+ "\"value\": null,"
		+ "\"annotationClass\": \".annotorious-popup\"}}";
	
	public static final String REST_ANNOTATION_JSON_LD = 
        "{\"@context\":{\"oa\":\"http://www.w3.org/ns/oa-context-20130208.json\"},"
        + "\"@type\":\"[oa:annotation,euType:OBJECT_TAG]\",\"annotatedAt\":\"2012-11-10T09:08:07\","
        + "\"annotatedBy\":{\"@id\":\"open_id_1\",\"@type\":\"[foaf:Person, euType:PERSON]\","
        + "\"name\":\"annonymous web user\"},\"body\":{\"@type\":"
        + "\"[oa:Tag,cnt:ContentAsText,dctypes:Text,euType:SEMANTIC_TAG]\",\"chars\":\"Vlad Tepes\","
        + "\"foaf:page\":\"https://www.freebase.com/m/035br4\",\"format\":\"text/plain\","
        + "\"language\":\"ro\",\"multilingual\":\"[ro:Vlad Tepes,en:Vlad the Impaler]\"},"
        + "\"motivation\":\"oa:tagging\",\"serializedAt\":\"2012-11-10T09:08:07\","
        + "\"serializedBy\":{\"@id\": \"open_id_2\",\"@type\": \"[prov:SoftwareAgent,euType:SOFTWARE_AGENT]\","
        + "\"foaf:homepage\": \"http://annotorious.github.io/\",\"name\": \"Annotorious\"},"
        + "\"styledBy\": {\"@type\": \"[oa:CssStyle,euType:CSS]\","
        + "\"source\": \"http://annotorious.github.io/latest/themes/dark/annotorious-dark.css\","
        + "\"styleClass\": \"annotorious-popup\"},"
        + "\"target\": {\"@type\": \"[oa:SpecificResource,euType:IMAGE]\",\"contentType\": \"image/jpeg\","
        + "\"httpUri\": \"http://europeanastatic.eu/api/image?uri=http%3A%2F%2Fbilddatenbank.khm.at%2Fimages%2F500%2FGG_8285.jpg&size=FULL_DOC&type=IMAGE\","
        + "\"selector\": {\"@type\": \"[oa:SvgRectangle,euType:SVG_RECTANGLE_SELECTOR]\","
        + "\"dimensionMap\": \"[left:5,right:3]\"},\"source\": {\"@id\": \"/15502/GG_8285\","
        + "\"contentType\": \"text/html\",\"format\": \"dctypes:Text\"},"
        + "\"targetType\": \"[oa:SpecificResource,euType:IMAGE]\"},"
        + "\"type\": \"OBJECT_TAG\"}";
	
	/**
	 * These namespace prefixes are employed for evaluation of the internal type of the objects in Annotation.
	 */
	public enum TypeNamespaces {
		oa, foaf, prov;
	}	
}
