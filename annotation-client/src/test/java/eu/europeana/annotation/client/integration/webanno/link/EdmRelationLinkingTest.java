package eu.europeana.annotation.client.integration.webanno.link;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import eu.europeana.annotation.definitions.model.Annotation;

public class EdmRelationLinkingTest extends BaseLinkingTest {

	@Test
	public void createIsSimilarToLink() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(LINK_EDM_IS_SIMMILAR_TO);
		
		Annotation inputAnno = parseLink(requestBody);
		
		Annotation storedAnno = createLink(requestBody);
		addCreatedAnnotation(storedAnno);
		
		//validate the reflection of input in output!
		validateOutputAgainstInput(storedAnno, inputAnno);
	}
	
	@Test
	public void createIsSimilarToMinimalLink() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(LINK_EDM_IS_SIMMILAR_TO_MINIMAL);
		
		Annotation inputAnno = parseLink(requestBody);
		
		Annotation storedAnno = createLink(requestBody);
		addCreatedAnnotation(storedAnno);
		
		//validate the reflection of input in output!
		validateOutputAgainstInput(storedAnno, inputAnno);

	}
	
	@Test
    public void createLinkAnnotation() throws JsonParseException, IOException {
        ResponseEntity<String> response = storeTestAnnotation(LINK_STANDARD);
        Annotation storedAnno = validateResponse(response);
        addCreatedAnnotation(storedAnno);
    }
}
