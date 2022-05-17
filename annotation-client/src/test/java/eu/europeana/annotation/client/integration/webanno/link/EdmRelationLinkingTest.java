package eu.europeana.annotation.client.integration.webanno.link;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.Test;

import eu.europeana.annotation.definitions.model.Annotation;

public class EdmRelationLinkingTest extends BaseLinkingTest {

	@Test
	public void createIsSimilarToLink() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(LINK_EDM_IS_SIMMILAR_TO);
		
		Annotation inputAnno = parseLink(requestBody);
		
		Annotation storedAnno = createLink(requestBody);
		
		//validate the reflection of input in output!
		validateOutputAgainstInput(storedAnno, inputAnno);
		
		removeAnnotation(storedAnno.getIdentifier());
	}
	
	@Test
	public void createIsSimilarToMinimalLink() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(LINK_EDM_IS_SIMMILAR_TO_MINIMAL);
		
		Annotation inputAnno = parseLink(requestBody);
		
		Annotation storedAnno = createLink(requestBody);
		
		//validate the reflection of input in output!
		validateOutputAgainstInput(storedAnno, inputAnno);
		
		removeAnnotation(storedAnno.getIdentifier());
	}
}
