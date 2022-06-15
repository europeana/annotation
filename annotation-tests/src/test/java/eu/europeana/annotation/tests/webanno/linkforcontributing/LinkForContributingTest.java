package eu.europeana.annotation.tests.webanno.linkforcontributing;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.tests.AnnotationTestUtils;
import eu.europeana.annotation.tests.BaseAnnotationTest;

public class LinkForContributingTest extends BaseAnnotationTest {

    @Test
    public void createLinkForContributingBodyObject() throws Exception {        
        ResponseEntity<String> response = storeTestAnnotation(LINK_FOR_CONTRIBUTING_BODY_OBJECT, true, null);
        AnnotationTestUtils.validateResponse(response); 
        Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
        removeAnnotationManually(storedAnno.getIdentifier());
    }
    
    @Test
    public void createLinkForContributingBodyString() throws Exception {        
        ResponseEntity<String> response = storeTestAnnotation(LINK_FOR_CONTRIBUTING_BODY_STRING, true, null);
        AnnotationTestUtils.validateResponse(response);     
        Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
        removeAnnotationManually(storedAnno.getIdentifier());
    }
	
}
