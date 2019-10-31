package eu.europeana.annotation.client.integration.webanno;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import eu.europeana.annotation.client.integration.webanno.BaseWebAnnotationProtocolTest;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.ResourceTypes;

public class FullTextResourceTest extends BaseWebAnnotationProtocolTest {
	
	private String FULL_TEXT_RESOURCE_VALUE = "... complete transcribed text in HTML ...";

	@Test
	public void createFullTextResource() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		ResponseEntity<String> response = storeTestAnnotation(FULL_TEXT_RESOURCE);
		validateResponse(response);
		Annotation storedAnno = getApiClient().parseResponseBody(response);				
		assertEquals(storedAnno.getBody().getInternalType(), ResourceTypes.FULL_TEXT_RESOURCE.name());
		assertTrue(storedAnno.getBody().getValue().equals(FULL_TEXT_RESOURCE_VALUE));
	}
	
}
