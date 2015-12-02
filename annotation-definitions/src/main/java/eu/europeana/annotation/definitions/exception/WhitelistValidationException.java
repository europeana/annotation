package eu.europeana.annotation.definitions.exception;

/**
 * This class is used represent validation errors for the whitelist class 
 */
public class WhitelistValidationException extends RuntimeException{

	private static final long serialVersionUID = -5929252558632037271L;

	public static final String ERROR_NULL_OBJECT_ID = "Object ID must be null";
	public static final String ERROR_NOT_NULL_URI = "Whitelist HTTP uri must not be null";
	public static final String ERROR_NOT_HTTP_URI = "Whitelist HTTP uri must start with \"http\"";
	public static final String ERROR_NOT_NULL_NAME = "Whitelist name must not be null";
	public static final String ERROR_NOT_NULL_STATUS = "Whitelist status must not be null";
	public static final String ERROR_MISSING_ID = "Whitelist ID must not be null";
	public static final String ERROR_HTTP_URL_EXISTS = "Whitelist entry for the given URL already exists. ";
	
	public static final String HTTP = "http";
	public static final String DATE_FORMAT = "yyyy-MM-dd";

	public WhitelistValidationException(String message){
		super(message);
	}
	
	public WhitelistValidationException(String message, Throwable th){
		super(message, th);
	}
	
	
}
