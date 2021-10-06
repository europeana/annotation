package eu.europeana.annotation.client.integration.webanno.link;

import org.apache.stanbol.commons.exception.JsonParseException;

import eu.europeana.annotation.client.integration.webanno.BaseWebAnnotationProtocolTest;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

public class BaseLinkingTest extends BaseWebAnnotationProtocolTest {

	public static final String LINK_MINIMAL = "/link/minimal.json";
	public static final String LINK_STANDARD = "/link/standard.json";
	public static final String LINK_EDM_IS_SIMMILAR_TO = "/link/edmIsSimilarTo.json";
	public static final String LINK_EDM_IS_SIMMILAR_TO_MINIMAL = "/link/edmIsSimilarTo_minimal.json";
	
	
	protected Annotation parseLink(String jsonString) throws JsonParseException {
		MotivationTypes motivationType = MotivationTypes.LINKING;
		return parseAnnotation(jsonString, motivationType);		
	}

//	protected Annotation createLink(String requestBody) throws JsonParseException {
//		ResponseEntity<String> response = getApiClient().createAnnotation(
//				getApiKey(), WebAnnotationFields.PROVIDER_WEBANNO, null, true, requestBody, 
//				TEST_USER_TOKEN, 
//				null //WebAnnotationFields.LINK
//				);
//				
//				
//		assertNotNull(response.getBody());
//		assertEquals(response.getStatusCode(), HttpStatus.CREATED);
//		
//		Annotation storedAnno = getApiClient().parseResponseBody(response);
//		assertNotNull(storedAnno.getCreator());
//		assertNotNull(storedAnno.getGenerator());
//		return storedAnno;
//	}

}
