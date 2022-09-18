package eu.europeana.annotation.tests.webanno.agent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.tests.utils.AnnotationTestUtils;
import eu.europeana.annotation.tests.webanno.tag.BaseTaggingTest;

@SpringBootTest
@AutoConfigureMockMvc
public class AgentTest extends BaseTaggingTest {
	
	public static final String FULL_AGENT = "/tag/full_agent.json";
	public static final String WRONG_AGENT_ID_NOT_URL = "/tag/wrong/agent_wrong_id_not_url.json";

	@Test
	public void createAnnotationAgentDetails() throws Exception {
		
		ResponseEntity<String> response = storeTestAnnotation(FULL_AGENT, true, null);
		AnnotationTestUtils.validateResponse(response);
		Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
		createdAnnotations.add(storedAnno.getIdentifier());
		
		String requestBody = AnnotationTestUtils.getJsonStringInput(FULL_AGENT);
		Annotation inputAnno = parseTag(requestBody);
		
		//validate the reflection of input in output!
		//but ignore generated timestamp which is always set by the server
		inputAnno.setGenerated(storedAnno.getGenerated());
		AnnotationTestUtils.validateOutputAgainstInput(storedAnno, inputAnno);
	}

//	@Test
	public void agentIDNotUrlMustThrowException() throws Exception {
		
		String requestBody = AnnotationTestUtils.getJsonStringInput(WRONG_AGENT_ID_NOT_URL);
		
		ResponseEntity<String> response = storeTestAnnotationByType(
			false, requestBody, null, null);
		
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());		
		
	}
	
}
