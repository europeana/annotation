package eu.europeana.annotation.tests.webanno.link;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.tests.AnnotationTestUtils;
import eu.europeana.annotation.tests.AbstractIntegrationTest;

public class BaseLinkingTest extends AbstractIntegrationTest {

	public static final String LINK_MINIMAL = "/link/minimal.json";
	public static final String LINK_STANDARD = "/link/standard.json";
	public static final String LINK_EDM_IS_SIMMILAR_TO = "/link/edmIsSimilarTo.json";
	public static final String LINK_EDM_IS_SIMMILAR_TO_MINIMAL = "/link/edmIsSimilarTo_minimal.json";
	
    @Test
    public void createLinkAnnotation() throws Exception {
        ResponseEntity<String> response = storeTestAnnotation(LINK_STANDARD, true, null);
        AnnotationTestUtils.validateResponse(response);  
        
        Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
        createdAnnotations.add(storedAnno.getIdentifier());
    }

}
