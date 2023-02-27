package eu.europeana.annotation.web.validation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Optional;
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

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import eu.europeana.annotation.definitions.model.impl.MediaFormat;
import eu.europeana.annotation.definitions.model.impl.MediaFormats;

public class MediaFormatValidator {
	
	protected Logger logger = LogManager.getLogger(MediaFormatValidator.class);
	
	private static final String XSD_VALIDATION = "xsd";
	
	MediaFormats mediaFormats;
	
    void initMediaFormats(String formatsFile) throws IOException {
    	try (InputStream inputStream = getClass().getResourceAsStream(formatsFile)) {
		    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		    String contents = reader.lines().collect(Collectors.joining(System.lineSeparator()));
		    mediaFormats = (new XmlMapper()).readValue(contents, MediaFormats.class);
    	}
    }

    public MediaFormatValidator(String formatsFile) throws IOException {
		initMediaFormats(formatsFile);
	}

	public boolean validateMediaFormat(String content, String format){ 
    	if (StringUtils.isBlank(content)) {
    	    return true;
    	}
        Optional<MediaFormat> mediaF = mediaFormats.getFormat(format);
        if(mediaF.isEmpty()) {
        	return true;
        }
        //in case of xsd validation
        if(XSD_VALIDATION.equals(mediaF.get().getValidation())) {
            String xsdFilePath = mediaF.get().getValidationResource();
            if(StringUtils.isBlank(xsdFilePath)) {
            	return false;
            }                
            return validateXsd(content, xsdFilePath);
        }
        
        //in case of not supported validation return true
        return true;
    }
    
    //validate xml using xsd schema
    private boolean validateXsd(String xmlToValidate, String xsdFilePath) {
    	try {
	    	if(xsdFilePath==null) {
		       	return false;
		    }
	        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	        Source xsdSource = new StreamSource(getClass().getResourceAsStream(xsdFilePath));
	        Schema schema = factory.newSchema(xsdSource);
	        Validator validator = schema.newValidator();

//	        String altoXmlFile = "/xsd-validation/Glyph_Sample01_General.xml";
//	        String pageXmlFile = "/xsd-validation/aletheiaexamplepage.xml";
//	        validator.validate(new StreamSource(getClass().getResourceAsStream(altoXmlFile)));
	        Reader xmlReader = new StringReader(xmlToValidate);
	        validator.validate(new StreamSource(xmlReader));
	    } catch (IOException | SAXException e) {
	    	logger.error("XML does not match the XSD schema.", e);
	        return false;
	    }   
    	return true;
    }

}
