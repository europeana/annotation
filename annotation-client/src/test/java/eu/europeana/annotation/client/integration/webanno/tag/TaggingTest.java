package eu.europeana.annotation.client.integration.webanno.tag;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.Test;

import eu.europeana.annotation.definitions.model.Annotation;

public class TaggingTest extends BaseTaggingTest {

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
	

	
}
