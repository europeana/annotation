package eu.europeana.annotation.definitions.model;

public interface WebAnnotationFields {

	/**
	 * Helping constants for Annotation mapping to the JSON-LD format 
	 */
	public static final String ANNOTATION_LD_TYPE   = "OBJECT_TYPE"; //"oa:Annotation";
	public static final String ANNOTATED_BY         = "annotatedBy";
	public static final String ANNOTATED_AT         = "annotatedAt";
	public static final String NAME                 = "name";
	public static final String SERIALIZED_AT        = "serializedAt";
	public static final String SERIALIZED_BY        = "serializedBy";
	public static final String FOAF_HOMEPAGE        = "foaf:homepage";
	public static final String MOTIVATED_BY         = "motivatedBy";
	public static final String STYLED_BY            = "styledBy";
	public static final String SOURCE               = "source";
	public static final String SELECTOR             = "selector";
	public static final String STYLE_CLASS          = "styleClass";
	public static final String BODY                 = "body";
	public static final String CHARS                = "chars";
	public static final String DC_LANGUAGE          = "dc:language";
	public static final String FORMAT               = "format";
	public static final String FOAF_PAGE            = "foaf:page";
	public static final String TARGET               = "target";
	public static final String SID                  = "@id";
	public static final String TYPE                 = "type";
	public static final String ANNOTATION_ID        = "annotationId";
	public static final String DATE_FORMAT          = "yyyy-MM-dd'T'HH:mm:ss";
}
