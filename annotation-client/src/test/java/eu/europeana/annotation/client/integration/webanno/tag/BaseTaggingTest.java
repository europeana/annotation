package eu.europeana.annotation.client.integration.webanno.tag;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.stanbol.commons.exception.JsonParseException;

import eu.europeana.annotation.client.integration.webanno.BaseWebAnnotationTest;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

public class BaseTaggingTest extends BaseWebAnnotationTest {

	public static final String TAG_MINIMAL = "/tag/minimal.json";
	public static final String TAG_BODY_TEXT = "/tag/bodyText.json";
	public static final String TAG_MINIMAL_WRONG = "/tag/wrong/minimal_wrong.json";
	public static final String TAG_GEO_WRONG_LAT = "/tag/wrong/geotag_wrong_lat.json";
	public static final String TAG_GEO_WRONG_LONG = "/tag/wrong/geotag_wrong_long.json";
	public static final String TAG_STANDARD = "/tag/standard.json";
	public static final String TAG_GEOTAG = "/tag/geotag.json";
	public static final String LINK_STANDARD = "/link/standard.json";
	public static final String LINK_SEMANTIC = "/link/edmIsSimilarTo.json";
	public static final String SEMANTICTAG_SIMPLE_MINIMAL = "/semantictag/simple_minimal.json";
	public static final String SEMANTICTAG_SIMPLE_STANDARD = "/semantictag/simple_standard.json";
	public static final String SEMANTICTAG_SPECIFIC_MINIMAL = "/semantictag/specific_minimal.json";
	public static final String SEMANTICTAG_SPECIFIC_STANDARD = "/semantictag/specific_standard.json";
	public static final String SEMANTICTAG_WEB_RESOURCE = "/semantictag/web_resource.json";
	public static final String SEMANTICTAG_ENTITY = "/semantictag/semantictag_entity.json";
	public static final String SEMANTICTAG_AGENT_ENTITY = "/semantictag/semantictag_agent_entity.json";
	public static final String SEMANTICTAG_VCARD_ADDRESS = "/semantictag/vcard_address.json";
	public static final String TAG_CANONICAL = "/tag/canonical.json";
	public static final String TAG_VIA_STRING = "/tag/via_string.json";
	public static final String TAG_VIA_ARRAY = "/tag/via_array.json";
	public static final String DEREFERENCED_SEMANTICTAG_MOZART_ENTITY = "/semantictag/dereferenced_semantictag_mozart_entity.json";
	public static final String DEREFERENCED_SEMANTICTAG_TEST_ENTITY = "/semantictag/dereferenced_semantictag_viaf_test_entity.json";
	public static final String DEREFERENCED_SEMANTICTAG_TEST_ENTITY_2 = "/semantictag/dereferenced_semantictag_viaf_test_entity2.json";
	public static final String DEREFERENCED_SEMANTICTAG_TEST_ENTITY_3 = "/semantictag/dereferenced_semantictag_viaf_test_entity3.json";
	public static final String DESCRIBING_WEB_RESOURCE = "/describing/web_resource.json";
	public static final String TRANSCRIPTION_WITH_RIGHTS = "/transcription/transcription-with-rights.json";
	
	protected Annotation createAndValidateTag(String inputFile) throws IOException, JsonParseException,
		IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return createTag(inputFile, true, true);
	}
	
	protected Annotation createTag(String inputFile, boolean validate, boolean indexOnCreate) throws IOException, JsonParseException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		System.out.println("Input File: " + inputFile);

		String requestBody = getJsonStringInput(inputFile);

		Annotation storedAnno = createTestAnnotation(inputFile, indexOnCreate, null);

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
	
}
