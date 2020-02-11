package eu.europeana.annotation.client.integration.webanno.tag;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.stanbol.commons.exception.JsonParseException;

import eu.europeana.annotation.client.integration.webanno.BaseWebAnnotationProtocolTest;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

public class BaseTaggingTest extends BaseWebAnnotationProtocolTest {

	public static final String TAG_MINIMAL = "/tag/minimal.json";
	public static final String TAG_BODY_TEXT = "/tag/bodyText.json";
	public static final String TAG_MINIMAL_WRONG = "/tag/wrong/minimal_wrong.json";
	public static final String TAG_GEO_WRONG_LAT = "/tag/wrong/geotag_wrong_lat.json";
	public static final String TAG_GEO_WRONG_LONG = "/tag/wrong/geotag_wrong_long.json";
	public static final String TAG_STANDARD = "/tag/standard.json";
	public static final String TAG_GEOTAG = "/tag/geotag.json";
	public static final String SEMANTICTAG_SIMPLE_MINIMAL = "/semantictag/simple_minimal.json";
	public static final String SEMANTICTAG_SIMPLE_STANDARD = "/semantictag/simple_standard.json";
	public static final String SEMANTICTAG_SPECIFIC_MINIMAL = "/semantictag/specific_minimal.json";
	public static final String SEMANTICTAG_SPECIFIC_STANDARD = "/semantictag/specific_standard.json";
	public static final String SEMANTICTAG_WEB_RESOURCE = "/semantictag/web_resource.json";
	public static final String SEMANTICTAG_ENTITY = "/semantictag/semantictag_entity.json";
	public static final String SEMANTICTAG_VCARD_ADDRESS = "/semantictag/vcard_address.json";
	public static final String TAG_CANONICAL = "/tag/canonical.json";
	public static final String TAG_VIA_STRING = "/tag/via_string.json";
	public static final String TAG_VIA_ARRAY = "/tag/via_array.json";
	public static final String DEREFERENCED_SEMANTICTAG_MOZART_ENTITY = "/semantictag/dereferenced_semantictag_mozart_entity.json";
	public static final String DEREFERENCED_SEMANTICTAG_TEST_ENTITY = "/semantictag/dereferenced_semantictag_viaf_test_entity.json";
	public static final String DEREFERENCED_SEMANTICTAG_TEST_ENTITY_2 = "/semantictag/dereferenced_semantictag_viaf_test_entity2.json";
	public static final String DEREFERENCED_SEMANTICTAG_TEST_ENTITY_3 = "/semantictag/dereferenced_semantictag_viaf_test_entity3.json";
	
	protected Annotation createAndValidateTag(String inputFile) throws IOException, JsonParseException,
		IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return createTag(inputFile, true, true);
	}
	
	protected Annotation createTag(String inputFile, boolean validate, boolean indexOnCreate) throws IOException, JsonParseException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		System.out.println("Input File: " + inputFile);

		String requestBody = getJsonStringInput(inputFile);

		Annotation storedAnno = createTestAnnotation(inputFile, indexOnCreate);

		Annotation inputAnno = parseTag(requestBody);

		// validate the reflection of input in output!
		if (validate) {
			validateOutputAgainstInput(storedAnno, inputAnno);
		}

		return storedAnno;
	}

	protected Annotation parseTag(String jsonString) throws JsonParseException {
		MotivationTypes motivationType = MotivationTypes.TAGGING;
		return parseAnnotation(jsonString, motivationType);
	}

	// protected Annotation createTag(String requestBody) throws
	// JsonParseException {
	// ResponseEntity<String> response = getApiClient().createTag(
	// WebAnnotationFields.PROVIDER_WEBANNO, null, false, requestBody,
	// TEST_USER_TOKEN);
	//
	// assertNotNull(response.getBody());
	// assertEquals(response.getStatusCode(), HttpStatus.CREATED);
	//
	// Annotation storedAnno = getApiClient().parseResponseBody(response);
	// assertNotNull(storedAnno.getCreator());
	// assertNotNull(storedAnno.getGenerator());
	// return storedAnno;
	// }

	// protected Annotation createTagWithProviderAndUserToken(
	// String requestBody, String provider, String userToken) throws
	// JsonParseException {
	// ResponseEntity<String> response = getApiClient().createTag(
	// provider, null, false, requestBody, userToken);
	//
	// assertNotNull(response.getBody());
	// assertEquals(response.getStatusCode(), HttpStatus.CREATED);
	//
	// Annotation storedAnno = getApiClient().parseResponseBody(response);
	// assertNotNull(storedAnno.getCreator());
	// assertNotNull(storedAnno.getGenerator());
	// return storedAnno;
	// }
	
//	protected ResponseEntity<String> getAnnotation(Annotation anno) {
//		// add profile
//		String searchProfile = "dereference";
//		return getAnnotation(anno, searchProfile);
//	}
	
}
