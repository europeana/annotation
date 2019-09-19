package eu.europeana.annotation.client.integration.webanno.tag;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.Test;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.ResourceTypes;

public class FullTextResourceTest extends BaseTaggingTest {

	@Test
	public void createFullTextResource() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(TAG_FULL_TEXT_RESOURCE);
		
		Annotation storedAnno = createTag(requestBody);
				
		assertEquals(storedAnno.getBody().getInternalType(), ResourceTypes.FULL_TEXT_RESOURCE.name());
	}
	
}
