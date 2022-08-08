package eu.europeana.annotation.client.integration.webanno.linkforcontributing;

import java.io.IOException;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import eu.europeana.annotation.client.integration.webanno.BaseWebAnnotationTest;
import eu.europeana.annotation.definitions.model.Annotation;

public class LinkForContributingTest extends BaseWebAnnotationTest {

    
  
    @Test
    public void createLinkForContributingBodyObject() throws JsonParseException, IOException {        
        ResponseEntity<String> response = storeTestAnnotation(LINK_FOR_CONTRIBUTING_BODY_OBJECT);
        validateResponse(response); 
        
        Annotation storedAnno = getApiProtocolClient().parseResponseBody(response);
        addCreatedAnnotation(storedAnno);
//        removeAnnotation(storedAnno.getIdentifier());
    }
    
    @Test
    public void createLinkForContributingBodyString() throws JsonParseException, IOException {        
        ResponseEntity<String> response = storeTestAnnotation(LINK_FOR_CONTRIBUTING_BODY_STRING);
        Annotation storedAnno = validateResponse(response);     
        
        addCreatedAnnotation(storedAnno);
//        removeAnnotation(storedAnno.getIdentifier());
    }

    @Test
    public void createLinkForContributingTargetSpecific() throws JsonParseException, IOException {        
        ResponseEntity<String> response = storeTestAnnotation(LINK_FOR_CONTRIBUTING_TARGET_SPECIFIC);
        Annotation storedAnno = validateResponse(response);     
        
        addCreatedAnnotation(storedAnno);
//        removeAnnotation(storedAnno.getIdentifier());
    }
    
}
