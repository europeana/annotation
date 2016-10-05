package eu.europeana.annotation.client.integration.webanno.link;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import eu.europeana.annotation.client.integration.webanno.BaseWebAnnotationProtocolTest;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;

public class BaseLinkingTest extends BaseWebAnnotationProtocolTest {

//	public static final String LINK_MINIMAL = "/tag/minimal.json";
//	public static final String LINK_STANDARD = "/tag/standard.json";
	public static final String LINK_EDM_IS_SIMMILAR_TO = "/link/edmIsSimilarTo.json";
	public static final String LINK_EDM_IS_SIMMILAR_TO_MINIMAL = "/link/edmIsSimilarTo_minimal.json";
	
	
	protected Annotation parseLink(String jsonString) throws JsonParseException {
		MotivationTypes motivationType = MotivationTypes.LINKING;
		return parseAnnotation(jsonString, motivationType);		
	}

	protected Annotation createLink(String requestBody) throws JsonParseException {
		ResponseEntity<String> response = getApiClient().createAnnotation(
				getApiKey(), WebAnnotationFields.PROVIDER_WEBANNO, null, true, requestBody, 
				TEST_USER_TOKEN, 
				null //WebAnnotationFields.LINK
				);
				
				
		assertNotNull(response.getBody());
		assertEquals(response.getStatusCode(), HttpStatus.CREATED);
		
		Annotation storedAnno = getApiClient().parseResponseBody(response);
		assertNotNull(storedAnno.getCreator());
		assertNotNull(storedAnno.getGenerator());
		return storedAnno;
	}

}
