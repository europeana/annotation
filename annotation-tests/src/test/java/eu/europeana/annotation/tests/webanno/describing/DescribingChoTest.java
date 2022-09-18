package eu.europeana.annotation.tests.webanno.describing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.BodyInternalTypes;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.tests.AbstractIntegrationTest;
import eu.europeana.annotation.tests.utils.AnnotationTestUtils;

/**
 * This is a test for an annotation object with motivation "describing"
 * and for provided CHO with textual body.
 * 
 * @author GrafR
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
public class DescribingChoTest extends AbstractIntegrationTest {

    public static final String DESCRIBING_CHO = "/describing/cho.json";

	@Test
	public void createFullTextResource() throws Exception {
		
		String requestBody = AnnotationTestUtils.getJsonStringInput(DESCRIBING_CHO);
		
		ResponseEntity<String> response = storeTestAnnotationByType(
				true, requestBody, null, null);
								
		assertNotNull(response.getBody());
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		
		Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
		createdAnnotations.add(storedAnno.getIdentifier());
								
		assertTrue(storedAnno.getMotivation().equals(MotivationTypes.DESCRIBING.name().toLowerCase()));
		assertEquals(storedAnno.getBody().getInternalType(), BodyInternalTypes.TEXT.name());
	}
	
}
