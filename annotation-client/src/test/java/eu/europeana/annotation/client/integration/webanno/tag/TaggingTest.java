package eu.europeana.annotation.client.integration.webanno.tag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import eu.europeana.annotation.client.integration.webanno.BaseWebAnnotationProtocolTest;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.view.AnnotationView;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.definitions.model.vocabulary.fields.WebAnnotationFields;
import eu.europeana.annotation.utils.parse.AnnotationLdParser;

public class TaggingTest extends BaseWebAnnotationProtocolTest {

	@Test
	public void createMinimalTag() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(TAG_MINIMAL);
		
		ResponseEntity<String> response = getApiClient().createTag(
				WebAnnotationFields.PROVIDER_WEBANNO, null, false, requestBody, 
				TEST_USER_TOKEN);
		
		assertNotNull(response.getBody());
		assertEquals(response.getStatusCode(), HttpStatus.CREATED);
		
		Annotation storedAnno = getApiClient().parseResponseBody(response);
		assertNotNull(storedAnno.getAnnotatedBy());
		assertNotNull(storedAnno.getSerializedBy());
		
		
		AnnotationLdParser europeanaParser = new AnnotationLdParser();
		Annotation inputAnno = europeanaParser.parseAnnotation(MotivationTypes.TAGGING, requestBody);
		//validate the reflection of input in output!
		validateOutputAgainstInput(storedAnno, inputAnno);

	}

	

	
}
