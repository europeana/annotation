package eu.europeana.annotation.tests.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.tests.AbstractIntegrationTest;

/**
 * This class aims at testing of the annotation methods.
 * @author GrafR
 */
@SpringBootTest
@AutoConfigureMockMvc
public class AnnotationDeleteIT extends AbstractIntegrationTest { 
					
	@Test
	public void deleteAnnotation() throws Exception {
				
//		store annotation and retrieve its identifier URL
		Annotation anno = createTestAnnotation(TAG_STANDARD, true, null);
		
//		delete annotation by identifier URL
		ResponseEntity<String> response = deleteAnnotation(anno.getIdentifier());
		
		log.debug("Response body: " + response.getBody());
		if(!HttpStatus.NO_CONTENT.equals(response.getStatusCode()))
			log.error("Wrong status code: " + response.getStatusCode());
		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		
		createdAnnotations.add(anno.getIdentifier());
	}
			
				
}
