package eu.europeana.annotation.client.integration.webanno.tag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import eu.europeana.annotation.client.integration.webanno.BaseWebAnnotationProtocolTest;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
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
		AnnotationLdParser europeanaParser = new AnnotationLdParser();
		Annotation inputAnno = europeanaParser.parseAnnotation(MotivationTypes.TAGGING, requestBody);
		//validate the reflection of input in output!
		validateOutputAgainstInput(storedAnno, inputAnno);

	}

	private void validateOutputAgainstInput(Annotation storedAnno, Annotation inputAnno) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		Method[] methods = inputAnno.getClass().getMethods();
		Method currentMethod;
		Object inputProp;
		Object storedProp;
		
		for (int i = 0; i < methods.length; i++) {
			currentMethod = methods[i];
			if(currentMethod.getName().startsWith("get")){
				inputProp = currentMethod.invoke(inputAnno, (Object[]) null);
				
				//compare non null fields only
				if(inputProp != null){
					storedProp = currentMethod.invoke(storedAnno, (Object[])null);
					assertEquals(inputProp, storedProp);
				}			
			}			
		}
		
	}

	

	
}
