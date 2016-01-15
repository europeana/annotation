package eu.europeana.annotation.web.http;

public interface SwaggerConstants {

	/**
	 * Implementation Notes
	 */
	public static final String SAMPLES_JSON_LINK = "Please find JSON samples for annotation in <a href=\"./jsp/template/templates.jsp\" target=\"_blank\">templates</a>";
	public static final String SAMPLES_JSONLD_LINK = "Please find JSON-LD samples for annotation in <a href=\"./jsp/template/templates.jsp\" target=\"_blank\">templates</a>. " +
			"Provider parameter is optional but checked if provided. For now historypin is the only accepted provider. AnnotationNr is optional, but if provided a valid provider must be submitted";
	@Deprecated
	public static final String SAMPLES_EUROPEANA_API = "Please find JSON-LD samples for annotation in <a href=\"./jsp/template/europeana-ld-api.jsp\" target=\"_blank\">templates</a>. " +
			"Provider parameter is optional but checked if provided. For now historypin is the only accepted provider. AnnotationNr is optional, but if provided a valid provider must be submitted";
	public static final String SAMPLES_JSONLD = "Please find JSON-LD samples for annotation in <a href=\"./jsp/template/jsonld.jsp\" target=\"_blank\">templates</a>. " +
			"Provider parameter is optional but checked if provided. For now historypin is the only accepted provider. AnnotationNr is optional, but if provided a valid provider must be submitted";
	public static final String UPDATE_SAMPLES_JSONLD = "Please find JSON-LD samples for annotation in <a href=\"./jsp/template/jsonld.jsp\" target=\"_blank\">templates</a>. " +
			"Please create your JSON update request using selected fields you are going to update. E.g. 'body' and 'target' example:   { \"body\": \"Buccin Trombone\",\"target\": \"http://data.europeana.eu/item/09102/_UEDIN_214\" }";
	public static final String SAMPLES_JSONLD_WITH_TYPE = "Please find JSON-LD samples for annotation in <a href=\"./jsp/template/jsonld.jsp?withType=yes\" target=\"_blank\">templates</a>. " +
			"Provider parameter is optional but checked if provided. For now historypin is the only accepted provider. AnnotationNr is optional, but if provided a valid provider must be submitted";
	public static final String SEARCH_FIELDS_LINK = "Valid fields are 'all', 'label', 'body_value', 'tag_id', 'multilingual'.";
	public static final String SEARCH_STATUS_FIELDS_LINK = "Valid status fields are 'public', 'private', 'disabled'.";
	public static final String SEARCH_NOTES = "Please fill in either 'resourceId' or 'target' field. One of this fields is mandatory for search." 
			+ " Sample 'resourceId'='/webanno/234'. Sample 'target'='http://data.europeana.eu/item/123/xyz'.";
	
	public static final String SEARCH_SOLR_FIELDS = "motivation_key, target_urls, body_value, body_text, annotation_id_url, target_record_ids";
	public static final String SEARCH_PROFILES_LIST = "facet, standard.";
	public static final String SEARCH_SORT_FIELD_LIST = "annotatedAt, serializedAt and modified.";
	
	
	public static final String SEARCH_HELP_NOTE = "The following fields are available for search: "+ SEARCH_SOLR_FIELDS 
			+". Default is body_text (i.e. no field specified in search query), urls and ids are keywords and need to be submitted in quotes (e.g. target_record_ids:\"/123/xyz\"). "
			+ "The following profiles are available for search: "+ SEARCH_PROFILES_LIST 
			+ " Sorting is available for fields: " + SEARCH_SORT_FIELD_LIST;
	
			
}
