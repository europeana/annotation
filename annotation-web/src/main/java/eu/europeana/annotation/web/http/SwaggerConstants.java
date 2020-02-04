package eu.europeana.annotation.web.http;

import eu.europeana.annotation.definitions.model.utils.ModelConst;

public interface SwaggerConstants {

	/**
	 * Implementation Notes
	 */
	public static final String SAMPLES_JSON_LINK = "Please find JSON samples for annotation in <a href=\"../jsp/template/templates.jsp\" target=\"_blank\">templates</a>";
	//public static final String SAMPLES_JSONLD_LINK = "Please find JSON-LD samples for annotation in <a href=\"../jsp/template/templates.jsp\" target=\"_blank\">templates</a>. ";
//	@Deprecated
//	public static final String SAMPLES_JSONLD_LINK = SAMPLES_JSON_LINK;
//	@Deprecated
//	public static final String SAMPLES_JSONLD = SAMPLES_JSON_LINK;
	public static final String UPDATE_SAMPLES_JSONLD = SAMPLES_JSON_LINK +
			"Please create your JSON update request using selected fields you are going to update. E.g. 'body' and 'target' example:   { \"body\": \"Buccin Trombone\",\"target\": \"http://data.europeana.eu/item/09102/_UEDIN_214\" }";
	public static final String SAMPLES_JSONLD_WITH_TYPE = "Please find JSON-LD samples for annotation in <a href=\"../jsp/template/jsonld.jsp?withType=yes\" target=\"_blank\">templates</a>. " +
			"Provider parameter is optional but checked if provided. For now historypin is the only accepted provider. AnnotationNr is optional, but if provided a valid provider must be submitted";
	public static final String SEARCH_FIELDS_LINK = "Valid fields are 'all', 'label', 'body_value', 'tag_id', 'multilingual'.";
	public static final String SEARCH_STATUS_FIELDS_LINK = "Valid status fields are 'public', 'private', 'disabled'.";
	public static final String SEARCH_NOTES = "Please fill in either 'resourceId' or 'target' field. One of this fields is mandatory for search." 
			+ " Sample 'resourceId'='/webanno/234'. Sample 'target'='http://data.europeana.eu/item/123/xyz'.";
	
	public static final String SEARCH_SOLR_FIELDS = "motivation, anno_uri, anno_id, generator_uri, generator_name, generated, " 
	        + "creator_uri, creator_name, created, modified, moderation_score, text, body_value, "
			+ "body_value.&lt;lang&gt;, body_uri, target_uri, target_record_id, link_resource_uri, link_relation";	
	public static final String SEARCH_PROFILES_LIST = "facet, standard.";
	public static final String SEARCH_SORT_FIELD_LIST = "motivation, anno_uri, anno_id, generator_uri, generator_name, generated, creator_uri, creator_name, created, modified, moderation_score,  body_value, body_value.&lt;lang&gt;, body_uri, link_resource_uri, link_relation";
	
	
	public static final String SEARCH_HELP_NOTE = "The following fields are available for search: "+ SEARCH_SOLR_FIELDS 
			+". Default is text (i.e. no field specified in search query), urls and ids are keywords and need to be submitted in quotes (e.g. target_record_ids:\"/123/xyz\"). "
			+ "The following profiles are available for search: "+ SEARCH_PROFILES_LIST 
			+ " Sorting is available for fields: " + SEARCH_SORT_FIELD_LIST;
	
	public static final String DATE_FORMAT_HELP_NOTE = "Valid date format is: " + ModelConst.DATE_FORMAT;

	public static final String URIS_HELP_NOTE = "uris parameter needs to be provided as a an (json) array of annotation ids";
	
}
