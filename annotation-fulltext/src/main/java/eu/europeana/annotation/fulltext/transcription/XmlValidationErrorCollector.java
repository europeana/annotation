package eu.europeana.annotation.fulltext.transcription;

import java.util.ArrayList;
import java.util.List;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XmlValidationErrorCollector implements ErrorHandler{

 
  List<XmlValidationError> errors = new ArrayList<XmlValidationError>();
  List<XmlValidationError> warnings = new ArrayList<XmlValidationError>();

  @Override
  public void error(SAXParseException exc) throws SAXException {
      errors.add(new XmlValidationError(exc.getMessage(), "Line "+exc.getLineNumber()+", Column: "+exc.getColumnNumber()));
  }

  @Override
  public void fatalError(SAXParseException exc) throws SAXException {
      errors.add(new XmlValidationError(exc.getMessage(), "Line "+exc.getLineNumber()+", Column: "+exc.getColumnNumber()));
  }

  @Override
  public void warning(SAXParseException exc) throws SAXException {
      warnings.add(new XmlValidationError(exc.getMessage(), "Line "+exc.getLineNumber()+", Column: "+exc.getColumnNumber()));
  }
  
  public boolean hasErrors() {
      return !errors.isEmpty();
  }

  public List<XmlValidationError> getErrors() {
    return errors;
  }

  public List<XmlValidationError> getWarnings() {
    return warnings;
  }
  
  public String getValidationErrors() {
    if(!hasErrors()) {
      return "";
    }
    
    StringBuilder formattingErros = new StringBuilder();
    for (XmlValidationError xmlValidationError : getErrors()) {
      formattingErros.append(xmlValidationError.getMessageWithLocation()).append('\n');
    }
    return formattingErros.toString();
  }
  
}
