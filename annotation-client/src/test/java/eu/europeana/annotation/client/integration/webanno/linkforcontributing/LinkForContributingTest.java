package eu.europeana.annotation.client.integration.webanno.linkforcontributing;

import java.io.IOException;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import eu.europeana.annotation.client.integration.webanno.BaseWebAnnotationTest;

public class LinkForContributingTest extends BaseWebAnnotationTest {

    @Test
    public void createLinkForContributingBodyObject() throws JsonParseException, IOException {        
        ResponseEntity<String> response = storeTestAnnotation(LINK_FOR_CONTRIBUTING_BODY_OBJECT);
        validateResponse(response);     
    }
    
    @Test
    public void createLinkForContributingBodyString() throws JsonParseException, IOException {        
        ResponseEntity<String> response = storeTestAnnotation(LINK_FOR_CONTRIBUTING_BODY_STRING);
        validateResponse(response);     
    }
	
}
