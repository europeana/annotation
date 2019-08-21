package eu.europeana.annotation.client.integration.webanno.agent;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import eu.europeana.annotation.client.integration.webanno.tag.BaseTaggingTest;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;

public class AgentTest extends BaseTaggingTest {
	
	public static final String TAG_FULL_AGENT = "/tag/full_agent.json";
	public static final String TAG_WRONG_AGENT_ID_NOT_URL = "/tag/wrong/agent_wrong_id_not_url.json";

	@Test
	public void createAnnotationAgentDetails() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(TAG_FULL_AGENT);
		
		Annotation storedAnno = createTag(requestBody);
		
		Annotation inputAnno = parseTag(requestBody);
		
		//validate the reflection of input in output!
		//but ignore generated timestamp which is always set by the server
		inputAnno.setGenerated(storedAnno.getGenerated());
		validateOutputAgainstInput(storedAnno, inputAnno);
	}

	@Test
	public void agentIDNotUrlMustThrowException() throws IOException {
		
		String requestBody = getJsonStringInput(TAG_WRONG_AGENT_ID_NOT_URL);
		
		ResponseEntity<String> response = getApiClient().createTag(WebAnnotationFields.PROVIDER_WEBANNO, null, 
				false, requestBody,	TEST_USER_TOKEN);
		
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());		
		
	}
	
}
