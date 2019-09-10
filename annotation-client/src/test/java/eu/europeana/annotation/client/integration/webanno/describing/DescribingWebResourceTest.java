package eu.europeana.annotation.client.integration.webanno.describing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.Test;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.BodyInternalTypes;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

/**
 * This is a test for an annotation object with motivation "describing"
 * and for provided web resource.
 * 
 * @author GrafR
 *
 */
public class DescribingWebResourceTest extends BaseDescribingTest {

	@Test
	public void createFullTextResource() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(DESCRIBING_WEB_RESOURCE);
		
		Annotation storedAnno = createDescribing(requestBody);
				
		assertTrue(storedAnno.getMotivation().equals(MotivationTypes.DESCRIBING.name().toLowerCase()));
		assertTrue(storedAnno.getTarget().getSource() != null);
		assertEquals(storedAnno.getBody().getInternalType(), BodyInternalTypes.TEXT.name());
	}
	
}
