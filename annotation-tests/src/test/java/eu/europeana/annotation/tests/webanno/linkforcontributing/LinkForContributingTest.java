package eu.europeana.annotation.tests.webanno.linkforcontributing;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.tests.AbstractIntegrationTest;
import eu.europeana.annotation.tests.utils.AnnotationTestUtils;

@SpringBootTest
@AutoConfigureMockMvc
public class LinkForContributingTest extends AbstractIntegrationTest {

    @Test
    public void createLinkForContributingBodyObject() throws Exception {        
        ResponseEntity<String> response = storeTestAnnotation(LINK_FOR_CONTRIBUTING_BODY_OBJECT, true, null);
        AnnotationTestUtils.validateResponse(response); 
        Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
        createdAnnotations.add(storedAnno.getIdentifier());
    }
    
    @Test
    public void createLinkForContributingBodyString() throws Exception {        
        ResponseEntity<String> response = storeTestAnnotation(LINK_FOR_CONTRIBUTING_BODY_STRING, true, null);
        AnnotationTestUtils.validateResponse(response);     
        Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
        createdAnnotations.add(storedAnno.getIdentifier());
    }
	
}
