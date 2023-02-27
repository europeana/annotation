package eu.europeana.annotation.web.validation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import eu.europeana.annotation.definitions.exception.AnnotationServiceInstantiationException;
import eu.europeana.annotation.definitions.model.impl.MediaFormat;
import eu.europeana.annotation.definitions.model.impl.MediaFormats;

public class MediaFormatValidator {
	
	protected static Logger logger = LogManager.getLogger(MediaFormatValidator.class);
	
	private static final String XSD_VALIDATION = "xsd";
	
	MediaFormats mediaFormats = null;
	
    void initMediaFormats(String formatsFile) {
		try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(formatsFile)) {
		    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		    String contents = reader.lines().collect(Collectors.joining(System.lineSeparator()));
		    mediaFormats = (new XmlMapper()).readValue(contents, MediaFormats.class);
		}catch (Exception e) {
		    throw new AnnotationServiceInstantiationException("MediaFormats", e);
		}
    }

    public MediaFormatValidator(String formatsFile) {
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
	        URL url = getClass().getClassLoader().getResource(xsdFilePath);
	        if(url == null) {
	        	return false;
	        }
	        File file = new File(url.getFile());
	        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	        Schema schema = factory.newSchema (file);
	        Validator validator = schema.newValidator();
//	        String altoXmlFile = "xsd-validation/Glyph_Sample01_General.xml";
//	        String pageXmlFile = "xsd-validation/aletheiaexamplepage.xml";
//	        File file2 = new File(getClass().getClassLoader().getResource(pageXmlFile).getFile());
//	        validator.validate(new StreamSource(file2));
	        Reader xmlReader = new StringReader(xmlToValidate);
	        validator.validate(new StreamSource(xmlReader));
	    } catch (IOException | SAXException e) {
	    	logger.error("XML does not match the xsd schema.");
	        return false;
	    }   
    	return true;
    }

}
