package eu.europeana.annotation.fulltext.exception;

public class MediaTypeValidationException extends Exception{

  /**
   * 
   */
  private static final long serialVersionUID = 1127147643344253375L;
  
  public static final String MESSAGE_BLANK_CONTENT = "Xml content expected, but null or blank string was provided";
  public static final String MESSAGE_INVALID_CONFIG = "Invalid mediatype configurations. No resource validation available for format: ";
  public static final String MESSAGE_CANNOT_READ_CONFIG = "Invalid mediatype configurations. No resource validation available for format: ";
  public static final String MESSAGE_INIT_VALIDATOR_ERROR = "Cannot initialize validator for media type: ";
  public static final String MESSAGE_CANNOT_PERFORM_VALIDATION = "Cannot perform validation for xml content: \n";
  public static final String MESSAGE_INVALID_XML = "Cannot parse xml content, invalid formatting: \n";
      
  
  public MediaTypeValidationException(String message) {
    super(message);
  }
  
  
  public MediaTypeValidationException(String message, Throwable th) {
    super(message, th);
  }
}
