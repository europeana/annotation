package eu.europeana.annotation.fulltext.transcription;

public class XmlValidationError {

  private String location;
  protected String message;

  public XmlValidationError(String message, String location) {
    this.message = message;
    this.location = location;
  }

  /**
   * Returns the location of the error (usually line and column).
   * 
   * @return Location text
   */
  public String getLocation() {
    return location;
  }

  public String getMessage() {
    return message;
  }

  public String getMessageWithLocation() {
    return location + " " + message;
  }
}
