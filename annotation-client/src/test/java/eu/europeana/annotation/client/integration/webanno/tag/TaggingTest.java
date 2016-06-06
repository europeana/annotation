package eu.europeana.annotation.client.integration.webanno.tag;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;

public class TaggingTest extends BaseTaggingTest {

	@Test
	public void createTagBodyText() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(TAG_BODY_TEXT);
		
		Annotation storedAnno = createTag(requestBody);
		
		Annotation inputAnno = parseTag(requestBody);
		
		//validate the reflection of input in output!
		validateOutputAgainstInput(storedAnno, inputAnno);
	}
	
	
	@Test
	public void createTagMinimal() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(TAG_MINIMAL);
		
		Annotation storedAnno = createTag(requestBody);
		
		Annotation inputAnno = parseTag(requestBody);
		
		//validate the reflection of input in output!
		validateOutputAgainstInput(storedAnno, inputAnno);
	}
	

	@Test
	public void createTagStandard() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(TAG_STANDARD);
		
		Annotation storedAnno = createTag(requestBody);
		
		Annotation inputAnno = parseTag(requestBody);
		//validate the reflection of input in output!
		validateOutputAgainstInput(storedAnno, inputAnno);

	}
	
	@Test
	public void createWrongTag() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(TAG_MINIMAL_WRONG);
		
		ResponseEntity<String> response = getApiClient().createTag(
				WebAnnotationFields.PROVIDER_WEBANNO, null, false, requestBody, 
				TEST_USER_TOKEN);
		
		assertEquals(response.getStatusCode().value(), HttpStatus.BAD_REQUEST.value());
	}

	
}
