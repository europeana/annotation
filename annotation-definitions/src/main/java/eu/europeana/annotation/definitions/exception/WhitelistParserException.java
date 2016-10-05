package eu.europeana.annotation.definitions.exception;

/**
 * This class is used represent validation errors for the whitelist class 
 */
public class WhitelistParserException extends RuntimeException{

	private static final long serialVersionUID = -5929252558632037271L;

	public static final String ERROR_DEFAULT = "Cannot parse json to whitelist(entry)!";
	
	public WhitelistParserException(Throwable th){
		this(ERROR_DEFAULT, th);
	}
	
	public WhitelistParserException(String message){
		this(message, null);
	}
	
	public WhitelistParserException(String message, Throwable th){
		super(message, th);
	}
	
	
}
