package eu.europeana.annotation.fulltext.transcription;

import static eu.europeana.annotation.fulltext.exception.MediaTypeValidationException.MESSAGE_BLANK_CONTENT;
import static eu.europeana.annotation.fulltext.exception.MediaTypeValidationException.MESSAGE_CANNOT_PERFORM_VALIDATION;
import static eu.europeana.annotation.fulltext.exception.MediaTypeValidationException.MESSAGE_CANNOT_READ_CONFIG;
import static eu.europeana.annotation.fulltext.exception.MediaTypeValidationException.MESSAGE_INIT_VALIDATOR_ERROR;
import static eu.europeana.annotation.fulltext.exception.MediaTypeValidationException.MESSAGE_INVALID_CONFIG;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import eu.europeana.annotation.definitions.model.impl.MediaFormat;
import eu.europeana.annotation.definitions.model.impl.MediaFormats;
import eu.europeana.annotation.fulltext.exception.MediaTypeValidationException;


public class TranscriptionFormatValidator {

  protected Logger logger = LogManager.getLogger(TranscriptionFormatValidator.class);

  private static final String VALIDATION_XSD = "xsd";
  private static final String VALIDATION_NONE = "none";
  static final String MEDIA_FORMATS_FILE = "/media-formats.xml";
  private MediaFormats mediaFormats;
  private final Map<MediaFormat, Validator> validators = new ConcurrentHashMap<>();

  public TranscriptionFormatValidator() throws MediaTypeValidationException {
    this(MEDIA_FORMATS_FILE);
  }

  public TranscriptionFormatValidator(String formatsFile) throws MediaTypeValidationException {
    initMediaFormats(formatsFile);
  }
  
  void initMediaFormats(String formatsFile) throws MediaTypeValidationException {
    //read media formats file
    try (InputStream inputStream = getClass().getResourceAsStream(formatsFile)) {
      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
      String contents = reader.lines().collect(Collectors.joining(System.lineSeparator()));
      mediaFormats = (new XmlMapper()).readValue(contents, MediaFormats.class);    
    } catch (IOException e) {
      throw new MediaTypeValidationException(
          MESSAGE_CANNOT_READ_CONFIG + formatsFile, e);
    }
    
    //validate media formats configurations
    for (MediaFormat mediaFormat : mediaFormats.getFormats()) {
      //validation is mandatoy none can be used for skipping
      if (StringUtils.isBlank(mediaFormat.getValidation())) {
        throw new MediaTypeValidationException(
            MESSAGE_INVALID_CONFIG + " no validation element for mime type " + mediaFormat.getMimetype());
      } 
      //if xsd validation required validation resource
      if (VALIDATION_XSD.equals(mediaFormat.getValidation()) && StringUtils.isBlank(mediaFormat.getValidationResource())) {
        throw new MediaTypeValidationException(
            MESSAGE_INVALID_CONFIG + mediaFormat.getMimetype());
      }
    }
  }

  /**
   * Returns the results of validation or null in the case that the validation was not performed (e.g. validation not set to xsd in configurations)
   * @param content
   * @param format
   * @return
   * @throws MediaTypeValidationException
   */
  public XmlValidationErrorCollector validateMediaFormat(String content, String format)
      throws MediaTypeValidationException {
    if (StringUtils.isBlank(content)) {
      throw new MediaTypeValidationException(MESSAGE_BLANK_CONTENT);
    }
    Optional<MediaFormat> mediaFormatOptional = mediaFormats.getFormat(format);
    if (mediaFormatOptional.isEmpty()) {
      throw new MediaTypeValidationException("Media format not supported: " + format);
    }
    
    if(VALIDATION_NONE.equals(mediaFormatOptional.get().getValidation())) {
      return null;
    }
    // in case of xsd validation
    if (VALIDATION_XSD.equals(mediaFormatOptional.get().getValidation())) {
      return validateXsd(content, mediaFormatOptional.get());
    }

    //no validation applied, return null
    return null;
  }

  // validate xml using xsd schema
  private XmlValidationErrorCollector validateXsd(String xmlToValidate, MediaFormat mediaFormat) throws MediaTypeValidationException {
    
      Validator validator = getXmlValidator(mediaFormat);
      XmlValidationErrorCollector validationErrorCollector = new XmlValidationErrorCollector();
      validator.setErrorHandler(validationErrorCollector);
      try {
        validator.validate(new StreamSource(new StringReader(xmlToValidate)));
      } catch (SAXParseException e) {
        throw new MediaTypeValidationException(MediaTypeValidationException.MESSAGE_INVALID_XML + xmlToValidate, e);
      } catch (Exception e) {
        throw new MediaTypeValidationException(MESSAGE_CANNOT_PERFORM_VALIDATION + xmlToValidate, e);
      }
      return validationErrorCollector;
    
  }

  private Validator getXmlValidator(MediaFormat mediaFormat) throws MediaTypeValidationException {
    if(!validators.containsKey(mediaFormat)) {
      validators.put(mediaFormat, initXmlValidator(mediaFormat));
    }
    return validators.get(mediaFormat);
  }

  private Validator initXmlValidator(MediaFormat mediaFormat) throws MediaTypeValidationException{
    try(InputStream xsdInputStream = getClass().getResourceAsStream(mediaFormat.getValidationResource())){
      SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      // to be compliant, completely disable DOCTYPE declaration:
      factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
      // or prohibit the use of all protocols by external entities:
      factory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
      factory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
      
      Source xsdSource = new StreamSource(xsdInputStream);
      Schema schema = factory.newSchema(xsdSource);
      return schema.newValidator();
    } catch (IOException | SAXException e) {
      throw new MediaTypeValidationException(MESSAGE_INIT_VALIDATOR_ERROR + mediaFormat.getMimetype(), e);
    }
  }

}
