package eu.europeana.api.common.config;

public interface I18nConstants extends eu.europeana.api.commons.definitions.config.i18n.I18nConstants{
	//TODO: add extends api-commons.I18nConstants
	static final String ANNOTATION_NOT_FOUND = "error.annotation_not_found";
//	static final String OPERATION_NOT_AUTHORIZED = "error.annotation_operation_not_authorized";
	static final String CLIENT_NOT_AUTHORIZED = "error.annotation_client_not_authorized";
	@Deprecated
	static final String INVALID_TOKEN = "error.annotation_invalid_token";
	static final String USER_NOT_AUTHORIZED = "error.annotation_user_not_authorized";
	static final String ANNOTATION_NOT_ACCESSIBLE = "error.annotation_not_accessible";
	static final String ANNOTATION_INVALID_BODY = "error.annotation_invalid_body";
	static final String ANNOTATION_INVALID_SUBTITLES_FORMATS = "error.annotation_invalid_subtitles_formats";
	static final String ANNOTATION_INVALID_SUBTITLES_VALUE = "error.annotation_invalid_subtitles_value";
	static final String ANNOTATION_INVALID_RIGHTS = "annotation_invalid_rights";
	static final String ANNOTATION_CANT_PARSE_BODY = "error.annotation_cant_parse_body";
	static final String API_WRITE_LOCK = "error.annotation_write_lock";
	static final String ANNOTATION_VALIDATION = "error.annotation_validation";
	static final String ANNOTATION_DUPLICATION = "error.annotation_duplication";
	static final String APIKEY_FILE_NOT_FOUND = "error.annotation_apikey_file_not_found";
//	static final String INVALID_APIKEY = "error.annotation_invalid_apikey";
	static final String TEST_USER_FORBIDDEN = "error.annotation_test_user_forbidden";
	static final String LOCKED_MAINTENANCE = "error.annotation_lock_maintenance";
	static final String AUTHENTICATION_FAIL = "error.annotation_authentication_fail";
	static final String BASE64_DECODING_FAIL = "error.annotation_base64_encoding_fail";
	static final String UNSUPPORTED_TOKEN_TYPE = "error.annotation_unsupported_token_type";
	static final String INVALID_HEADER_FORMAT = "error.annotation_invalid_format";
	static final String INVALID_PROPERTY_VALUE = "error.annotation_invalid_property_value";
	static final String ANNOTATION_INVALID_TARGET_BASE_URL = "error.annotation_target_base_url";
	
	
	static final String MESSAGE_IDENTIFIER_NOT_NULL = "error.message_identifier_not_null";
	static final String MESSAGE_IDENTIFIER_NULL = "error.message_identifier_null";
	static final String MESSAGE_IDENTIFIER_WRONG = "error.message_identifier_wrong";
	static final String MESSAGE_ANNOTATION_ID_EXISTS = "error.message_annotation_id_exists";
	static final String MESSAGE_ANNOTATION_ID_NOT_EXISTS = "error.message_annotation_id_not_exists";
//	static final String MESSAGE_INVALID_PARAMETER_VALUE = "error.message_invalid_parameter_value";
	static final String MESSAGE_BLANK_PARAMETER_VALUE = "error.message_blank_parameter_value";
	static final String MESSAGE_URL_NOT_VALID = "error.message_url_not_valid";
	static final String MESSAGE_INVALID_TAG_SIZE = "error.message_invalid_tag_size";
	static final String MESSAGE_INVALID_TAG_FORMAT = "error.message_invalid_tag_format";
	static final String MESSAGE_INVALID_SIMPLE_TAG = "error.message_invalid_simple_tag";
	static final String MESSAGE_INVALID_TAG_SPECIFIC_RESOURCE = "error.message_invalid_tag_specific_resource";
	static final String MESSAGE_INVALID_TAG_ID_FORMAT = "error.message_invalid_tag_id_format";
	static final String MESSAGE_MISSING_MANDATORY_FIELD = "error.message_missing_mandatory_field";
	static final String INVALID_PROVIDER = "error.invalid_provider";
	static final String SOLR_EXCEPTION = "error.solr_exception";
	static final String SOLR_MALFORMED_QUERY_EXCEPTION = "error.solr_malformed_query_exception";
	
	static final String BATCH_UPLOAD_FAILED = "error.batch_upload_failed";
	
	static final String OPERATION_EXECUTION_NOT_ALLOWED = "error.operation_execution_not_allowed";
	
}
