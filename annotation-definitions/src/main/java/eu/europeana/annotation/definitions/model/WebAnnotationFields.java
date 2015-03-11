package eu.europeana.annotation.definitions.model;

public interface WebAnnotationFields {

	/**
	 * Helping constants for Annotation mapping to the JSON-LD format 
	 */
	public static final String EU_TYPE              = "euType";
	public static final String SPLITTER             = "#";
//	public static final String ANNOTATION_LD_TYPE   = "OBJECT_TYPE"; //"oa:Annotation";
	public static final String ANNOTATED_BY         = "annotatedBy";
	public static final String ANNOTATED_AT         = "annotatedAt";
	public static final String NAME                 = "name";
	public static final String SERIALIZED_AT        = "serializedAt";
	public static final String SERIALIZED_BY        = "serializedBy";
	public static final String FOAF_HOMEPAGE        = "foaf:homepage";
	public static final String MOTIVATED_BY         = "motivatedBy";
	public static final String STYLED_BY            = "styledBy";
	public static final String CONTENT_TYPE         = "contentType";
	public static final String HTTP_URI             = "httpUri";
	public static final String SOURCE               = "source";
	public static final String SELECTOR             = "selector";
	public static final String STYLE_CLASS          = "styleClass";
	public static final String AT_TYPE              = "@type";
	public static final String BODY                 = "body";
	public static final String BODY_TYPE            = "bodyType";
	public static final String TARGET_TYPE          = "targetType";
	public static final String DIMENSION_MAP        = "dimensionMap";
	public static final String CHARS                = "chars";
	public static final String DC_LANGUAGE          = "language";
	public static final String FORMAT               = "format";
	public static final String MEDIA_TYPE           = "mediaType";
	public static final String FOAF_PAGE            = "foaf:page";
	public static final String TARGET               = "target";
	public static final String SID                  = "@id";
	public static final String TYPE                 = "type";
	public static final String ANNOTATION_ID        = "annotationId";
	public static final String DATE_FORMAT          = "yyyy-MM-dd'T'HH:mm:ss";
	//public static final String OA_ANNOTATION        = "oa:annotation"; //"oa:Annotation"
	//public static final String OA_TAGGING           = "oa:tagging"; //"oa:Tagging";
	public static final String LAST_INDEXED_TIMESTAMP = "lastIndexedTimestamp";
	public static final String MULTILINGUAL         = "multilingual";
//	public static final String DEFAULT_EURIPEANA_ID = "/testCollection/testObject";
//	public static final String DEFAULT_STYLE_TYPE   = "[oa:CssStyle,euType:STYLE#CSS]";
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
}
