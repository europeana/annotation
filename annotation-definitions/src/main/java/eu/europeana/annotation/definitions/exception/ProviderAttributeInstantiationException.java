package eu.europeana.annotation.definitions.exception;

/**
 * This class is used represent validation errors for the provider attributes instantiation
 */
public class ProviderAttributeInstantiationException extends RuntimeException{

	private static final long serialVersionUID = -2419759006948411637L;
	public static final String BASE_MESSAGE = "Cannot instantiate provider attribute. ";
	public static final String DEFAULT_MESSAGE = "Cannot instantiate provider attribute for type: ";
	public static final String MESSAGE_UNKNOWN_TYPE = "Cannot instantiate provider attribute for type: ";
	
	public ProviderAttributeInstantiationException(String attributeType){
		this(attributeType, DEFAULT_MESSAGE);
	}
	
	public ProviderAttributeInstantiationException(String attributeType, String message){
		this(attributeType, message, null);
	}
	
	public ProviderAttributeInstantiationException(String attributeType, String message, Throwable th){
		super(message + attributeType, th);
	}
	
}
