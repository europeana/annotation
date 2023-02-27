package eu.europeana.annotation.tests.web;

import static eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields.BATCH_TOTAL_FIELD;
import static eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields.RESP_OPERATION_REPORT_ERRORS_FIELD;
import static eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields.RESP_OPERATION_REPORT_FAILURECOUNT_FIELD;
import static eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields.RESP_OPERATION_REPORT_FIELD;
import static eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields.RESP_OPERATION_REPORT_SUCCESSCOUNT_FIELD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.agent.impl.EdmAgent;
import eu.europeana.annotation.definitions.model.body.impl.EdmAgentBody;
import eu.europeana.annotation.definitions.model.body.impl.VcardAddressBody;
import eu.europeana.annotation.definitions.model.vocabulary.BodyInternalTypes;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.definitions.model.vocabulary.ResourceTypes;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.tests.AbstractIntegrationTest;
import eu.europeana.annotation.tests.config.AnnotationTestsConfiguration;
import eu.europeana.annotation.tests.utils.AnnotationTestUtils;

/**
 * Annotation API Batch Upload Test class
 * 
 * @author Sven Schlarb
 */
@SpringBootTest
@AutoConfigureMockMvc
public class AnnotationCreateIT extends AbstractIntegrationTest {

    private String get_CORRUPTED_JSON() throws IOException {
      return START + get_LINK_CORE(AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()) + "\"motivation\",=\"oa:linking\"" + END;
    }
    private String get_LINK_JSON_WITH_WRONG_MOTIVATION() throws IOException {
      return START + get_LINK_CORE(AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()) + "\"motiv\":\"oa:wrong\"" + END;
    }
    
    public String INVALID_ANNO_TYPE = "taglink";
    // annotation page test resources
	public static final String TAG_ANNO_PAGE = "/tag/batch/annotation_page.json";
	public static final String TAG_ANNO_PAGE_VIA = "/tag/batch/annotation_page_via.json";
	public static final String TAG_ANNO_PAGE_VALIDATION_ERROR = "/tag/wrong/batch/annotation_page_validation_error.json";
	public static final String TAG_ANNO_PAGE_NONEXISTING_ERROR = "/tag/wrong/batch/annotation_page_nonexisting_ids.json";
    public static final String FULL_AGENT = "/tag/full_agent.json";
    public static final String WRONG_AGENT_ID_NOT_URL = "/tag/wrong/agent_wrong_id_not_url.json";
    public static final String DESCRIBING_CHO = "/describing/cho.json";
    public static final String LINK_EDM_IS_SIMMILAR_TO = "/link/edmIsSimilarTo.json";
    public static final String LINK_EDM_IS_SIMMILAR_TO_MINIMAL = "/link/edmIsSimilarTo_minimal.json";
    public static final String TRANSCRIPTION_WITHOUT_RIGHTS = "/transcription/transcription-without-rights.json";
    public static final String TRANSCRIPTION_WITHOUT_LANG = "/transcription/transcription-without-language.json";
    public static final String TRANSCRIPTION_WITHOUT_VALUE = "/transcription/transcription-without-value.json";
    public static final String TRANSCRIPTION_WITH_ALTO_BODY = "/transcription/transcription-with-alto-body.json";
    public static final String TRANSCRIPTION_WITH_ALTO_BODY_WRONG = "/transcription/transcription-with-alto-body-wrong.json";
    public static final String TRANSCRIPTION_WITH_PAGE_XML_BODY = "/transcription/transcription-with-page-xml-body.json";
    public static final String TRANSCRIPTION_WITH_PAGE_XML_BODY_WRONG = "/transcription/transcription-with-page-xml-body-wrong.json";
    
	static final String VALUE_BATCH_TESTSET = "body_value: \"*-ff45d28b-8717-42f4-a486-f3a62f97fb64\"";

	// test annotations
	private List<Annotation> testAnnotations;


	// number of test annotations created in database
	public static final int TEST_NUM_ANNOTATIONS = 2;

	/**
	 * Create test annotations before test execution.
	 * 
	 * @throws Exception
	 */
	@BeforeEach
	public void createTestAnnotations() throws Exception {
		testAnnotations = new ArrayList<Annotation>();
		// create test annotations (representing the existing annotations to be
		// updated)
		Annotation testAnnotation = null;
		for (int i = 0; i < TEST_NUM_ANNOTATIONS; i++) {
			testAnnotation = createTestAnnotation(TAG_MINIMAL, false, null);
			testAnnotations.add(testAnnotation);
			createdAnnotations.add(testAnnotation.getIdentifier());
		}
	}
	
	/**
	 * Testing successful batch upload of annotations.
	 * 
	 * @throws Exception
	 */
	@Test
	public void successfulBatchUploadTest() throws Exception {

		String requestBody = replaceIdentifiers(AnnotationTestUtils.getJsonStringInput(TAG_ANNO_PAGE), "httpurl");

		// batch upload request
		ResponseEntity<String> uploadResponse = uploadAnnotations(
			requestBody, true);
		
		// response status must be 201 CREATED
		assertEquals(HttpStatus.CREATED, uploadResponse.getStatusCode());

		// check response
		JSONObject jsonObj = new JSONObject(uploadResponse.getBody());
		assertEquals(5, jsonObj.get(BATCH_TOTAL_FIELD));
		// check if test annotations which have been created previously in the database 
		// have been updated correctly
		ResponseEntity<String> response;
		Annotation storedAnnotation;
		
		for (int i = 0; i < testAnnotations.size(); i++) {
			response =  getAnnotation(testAnnotations.get(i));
			storedAnnotation = AnnotationTestUtils.parseResponseBody(response);
			String value = storedAnnotation.getBody().getValue();
			assertTrue(value.startsWith("tag"));
			// assuming equal order of test annotations and updated annotations
			assertEquals(testAnnotations.get(i).getIdentifier(), storedAnnotation.getIdentifier());
		}
		
		long startingId = testAnnotations.get(0).getIdentifier();
		for(long i=startingId + TEST_NUM_ANNOTATIONS; i<startingId+5; i++) {
		  createdAnnotations.add(i);
		}
	}
	
	

	/**
	 * Testing batch upload of annotations where the ID is provided by the via field.
	 * 
	 * @throws Exception
	 */
	@Test
	@Disabled
	public void viaBatchUploadTest() throws Exception {
		String requestBody = AnnotationTestUtils.getJsonStringInput(TAG_ANNO_PAGE_VIA);
		
		assertNotNull(requestBody);

		// batch upload request (using pundit provider, ID must be provided by via field)
		ResponseEntity<String> uploadResponse = uploadAnnotations(
				requestBody, true);
		// response status must be 201 CREATED
		assertEquals(HttpStatus.CREATED, uploadResponse.getStatusCode());
		
		// get response body properties
		JSONObject jsonObj = new JSONObject(uploadResponse.getBody());
		Integer total = (Integer) jsonObj.get(BATCH_TOTAL_FIELD);
		assertEquals(Integer.valueOf(1), total);
	}

	/**
	 * Error when trying to update non existing annotations
	 * 
	 * @throws Exception
	 */
	@Test
	public void updateNonExistingAnnotationsError() throws Exception {

		String requestBody = replaceIdentifiers(AnnotationTestUtils.getJsonStringInput(TAG_ANNO_PAGE_NONEXISTING_ERROR), "httpurl");

		// batch upload request
		ResponseEntity<String> response = uploadAnnotations(
				requestBody, false);

		// response status must be 404 NOT FOUND
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		
		jsonPrettyPrint(response.getBody());
				
		// get response body properties
		JSONObject jsonObj = new JSONObject(response.getBody());
		JSONObject opRepJsonObj = jsonObj.getJSONObject(RESP_OPERATION_REPORT_FIELD);
		assertEquals(5, opRepJsonObj.get(RESP_OPERATION_REPORT_SUCCESSCOUNT_FIELD));
		assertEquals(3, opRepJsonObj.get(RESP_OPERATION_REPORT_FAILURECOUNT_FIELD));
		JSONObject errors = opRepJsonObj.getJSONObject(RESP_OPERATION_REPORT_ERRORS_FIELD);

		// positions 2, 6 and 8 do not exist
		assertTrue(((String)errors.get("-1")).startsWith("Annotation does not exist"));
		assertTrue(((String)errors.get("-2")).startsWith("Annotation does not exist"));
		assertTrue(((String)errors.get("-3")).startsWith("Annotation does not exist"));
		
        long startingId = testAnnotations.get(0).getIdentifier();
        for(long i=startingId + TEST_NUM_ANNOTATIONS; i<startingId+8; i++) {
          createdAnnotations.add(i);
        }
	}

	/**
	 * Annotation Page contains two annotations which do not validate. The test
	 * must correctly identify the validation errors for the 3rd and for the 5th
	 * position (inclusive counting).
	 * 
	 * @throws Exception
	 */
	@Test
	public void validationError() throws Exception {

		// annotation page string which contains two annotations which do not
		// validate.
		String requestBody = AnnotationTestUtils.getJsonStringInput(TAG_ANNO_PAGE_VALIDATION_ERROR);

		// batch upload request
		ResponseEntity<String> response = uploadAnnotations(
				requestBody, false);

		// response status must be 400 BAD_REQUEST
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

		// get response body properties
		JSONObject jsonObj = new JSONObject(response.getBody());
		JSONObject opRepJsonObj = jsonObj.getJSONObject(RESP_OPERATION_REPORT_FIELD);
		assertEquals(3, opRepJsonObj.get(RESP_OPERATION_REPORT_SUCCESSCOUNT_FIELD));
		assertEquals(2, opRepJsonObj.get(RESP_OPERATION_REPORT_FAILURECOUNT_FIELD));
		JSONObject errors = opRepJsonObj.getJSONObject(RESP_OPERATION_REPORT_ERRORS_FIELD);

		// keys 1 and 2 have errors
		assertEquals("Invalid tag size. Must be shorter then 64 characters! tag.size: 170", errors.get("1"));
		assertEquals("Invalid tag size. Must be shorter then 64 characters! tag.size: 170", errors.get("2"));
	
        long startingId = testAnnotations.get(0).getIdentifier();
        for(long i=startingId + TEST_NUM_ANNOTATIONS; i<startingId+5; i++) {
          createdAnnotations.add(i);
        }
	}

	/**
	 * Replace identifiers in annotation page template. It is assumed that the
	 * number of variables in the template corresponds to the number of test
	 * annotations.
	 * 
	 * @param template
	 *            annotation page template
	 * @param varPrefix
	 *            prefix of the variable name used in the template
	 * @return replacement result
	 * @throws IOException
	 */
	private String replaceIdentifiers(String template, String varPrefix) throws IOException {
		List<String> httpUrlsList = testAnnotations.stream().map(x -> String.valueOf(x.getIdentifier())).collect(Collectors.toList());
		String[] httpUrls = new String[httpUrlsList.size()];
		httpUrls = httpUrlsList.toArray(httpUrls);
		String[] replacementVars = new String[httpUrlsList.size()];
		for (int i = 0; i < httpUrlsList.size(); i++)
			replacementVars[i] = "%" + varPrefix + (i + 1) + "%";
		return StringUtils.replaceEach(template, replacementVars, httpUrls);
	}
	
	/**
	 * Pretty print json output helper method
	 * @param jsonStr JSON string
	 */
	private void jsonPrettyPrint(String jsonStr) {
		JsonParser parser = new JsonParser();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonElement el = parser.parse(jsonStr);
		log.debug(gson.toJson(el));
	}
	
    @Test
    public void createWebannoAnnotationWithoutBody() throws Exception {
        ResponseEntity<String> response = storeTestAnnotation(null, false, null);
        assertEquals( HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    @Test
    public void createWebAnnotationWithCorruptedBody() throws Exception {
        
        ResponseEntity<String> response = storeTestAnnotationByType(
            false, get_CORRUPTED_JSON(), null, null);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    @Test
    public void createWebannoAnnotationLinkWithWrongMotivation() throws Exception {
        ResponseEntity<String> response = storeTestAnnotationByType(
                false, get_LINK_JSON_WITH_WRONG_MOTIVATION(), null, null
                );
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    } 
    
    @Test
    public void createWebannoAnnotationByWrongAnnoTypeJsonld() throws Exception {
        ResponseEntity<String> response = storeTestAnnotationByType(
                false, get_TAG_JSON_BY_TYPE_JSONLD(AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()), INVALID_ANNO_TYPE, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    @Test
    public void createDescribingAnnoWithoutBodyLanguage() throws Exception { 
        String requestBody = AnnotationTestUtils.getJsonStringInput(DESCRIBING_WITHOUT_BODY_LANGUAGE);
        ResponseEntity<String> response = storeTestAnnotationByType(
            false, requestBody, null, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    @Test
    public void createTaggingAnnoBodyAddressWithoutType() throws Exception { 
        String requestBody = AnnotationTestUtils.getJsonStringInput(TAGGING_BODY_ADDRESS_NO_TYPE);
        ResponseEntity<String> response = storeTestAnnotationByType(
            false, requestBody, null, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    @Test
    public void createTaggingAnnoBodyAddressWithoutStreetAddress() throws Exception { 
        String requestBody = AnnotationTestUtils.getJsonStringInput(TAGGING_BODY_ADDRESS_NO_STREET_ADDRESS);
        ResponseEntity<String> response = storeTestAnnotationByType(
            false, requestBody, null, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }    
    
    @Test
    public void createWebannoAnnotationTag() throws Exception {
        
        ResponseEntity<String> response = storeTestAnnotation(TAG_STANDARD, true, null);

        AnnotationTestUtils.validateResponse(response);
        
        Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
        createdAnnotations.add(storedAnno.getIdentifier());
    }
        
    @Test
    public void createWebannoAnnotationLink() throws Exception {
        
        String requestBody = AnnotationTestUtils.getJsonStringInput(LINK_STANDARD);
        
        ResponseEntity<String> response = storeTestAnnotationByType(
            false, requestBody, null, null);
        
        AnnotationTestUtils.validateResponse(response);
        
        Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
        createdAnnotations.add(storedAnno.getIdentifier());
    }
        
    @Test
    public void createWebannoAnnotationLinkWithoutBlanksInMotivation() throws Exception {
        ResponseEntity<String> response = storeTestAnnotationByType(
                true, get_LINK_JSON_WITHOUT_BLANK(), null, null);
        Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
        createdAnnotations.add(storedAnno.getIdentifier());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void createWebannoAnnotationTagByTypeJsonld() throws Exception {
        
        ResponseEntity<String> response = storeTestAnnotationByType(
                true, get_TAG_JSON_BY_TYPE_JSONLD(AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()), WebAnnotationFields.TAG, null);
        
        AnnotationTestUtils.validateResponse(response);
        
        Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
        createdAnnotations.add(storedAnno.getIdentifier());
    }    
    
    /**
     * {
     *     "@context": "http://www.europeana.eu/annotation/context.jsonld",
     *     "@type": "oa:Annotation",
     *     "motivation": "oa:tagging",
     *     "annotatedBy": {
     *         "@id": "https://www.historypin.org/en/person/55376/",
     *         "@type": "foaf:Person",
     *         "name": "John Smith"
     *     },
     *     "annotatedAt": "2015-02-27T12:00:43Z",
     *     "serializedAt": "2015-02-28T13:00:34Z",
     *     "serializedBy": "http://www.historypin.org",
     *     "body": " überhaupt ",
     *     "target": "http://data.europeana.eu/item/123/xyz",
     *     "oa:equivalentTo": "https://www.historypin.org/en/item/456"
     * }
     * @throws Exception 
     */
    @Test
    public void createWebannoAnnotationTagForValidation() throws Exception {
        
        ResponseEntity<String> response = storeTestAnnotationByType(
            true, get_TAG_JSON_VALIDATION(AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()), WebAnnotationFields.TAG, null);
        
        AnnotationTestUtils.validateResponseForTrimming(response);
        
        Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
        createdAnnotations.add(storedAnno.getIdentifier());
    }    
    
    @Test
    public void createWebannoAnnotationLinkByTypeJsonld() throws Exception {
        
        ResponseEntity<String> response = storeTestAnnotationByType(
                true, get_LINK_JSON_BY_TYPE_JSONLD(AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()), WebAnnotationFields.LINK, null);
        
        AnnotationTestUtils.validateResponse(response);
        
        Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
        createdAnnotations.add(storedAnno.getIdentifier());
    }   
    
    /*
     * For the below duplicate tests to pass please make sure that there are no annotations 
     * stored in Solr that are the same as the ones defined in the test, prior to the test execution.
     */
    @Test
    public void checkAnnotationDuplicatesCreateTranscriptions() throws Exception {
        ResponseEntity<String> response = storeTestAnnotation(TRANSCRIPTION_MINIMAL, true, null);
        Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);    
        createdAnnotations.add(storedAnno.getIdentifier());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        response = storeTestAnnotation(TRANSCRIPTION_MINIMAL, true, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    @Test
    public void checkAnnotationDuplicatesCreateCaptions() throws Exception {
        ResponseEntity<String> response = storeTestAnnotation(CAPTION_MINIMAL, true, null);
        Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
        createdAnnotations.add(storedAnno.getIdentifier());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        response = storeTestAnnotation(CAPTION_MINIMAL_EN, true, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    @Test
    public void checkAnnotationDuplicatesCaptionsThenSubtitles() throws Exception {
        ResponseEntity<String> response = storeTestAnnotation(CAPTION_MINIMAL, true, null);
        Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
        createdAnnotations.add(storedAnno.getIdentifier());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        response = storeTestAnnotation(SUBTITLE_MINIMAL, true, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    @Test
    public void checkAnnotationDuplicatesCreateSubtitles() throws Exception {
        ResponseEntity<String> response = storeTestAnnotation(SUBTITLE_MINIMAL, true, null);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
        createdAnnotations.add(storedAnno.getIdentifier());
        response = storeTestAnnotation(SUBTITLE_MINIMAL, true, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void checkAnnotationDuplicatesSubtitlesThenCaptions() throws Exception {
        ResponseEntity<String> response = storeTestAnnotation(SUBTITLE_MINIMAL, true, null);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
        createdAnnotations.add(storedAnno.getIdentifier());
        response = storeTestAnnotation(CAPTION_MINIMAL, true, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void checkAnnotationDuplicatesCreateTags() throws Exception {
        ResponseEntity<String> response = storeTestAnnotation(TAG_MINIMAL, true, null);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
        createdAnnotations.add(storedAnno.getIdentifier());
        response = storeTestAnnotation(TAG_MINIMAL, true, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    @Test
    public void checkAnnotationDuplicatesCreateSemanticTags() throws Exception {
        ResponseEntity<String> response = storeTestAnnotation(SEMANTICTAG_SIMPLE_MINIMAL, true, null);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
        createdAnnotations.add(storedAnno.getIdentifier());
        response = storeTestAnnotation(SEMANTICTAG_SIMPLE_MINIMAL, true, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    @Test
    public void checkAnnotationDuplicatesCreateLinkForContributing() throws Exception {
        ResponseEntity<String> response = storeTestAnnotation(LINK_FOR_CONTRIBUTING_BODY_OBJECT, true, null);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
        createdAnnotations.add(storedAnno.getIdentifier());
        response = storeTestAnnotation(LINK_FOR_CONTRIBUTING_BODY_OBJECT, true, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());        
    }
    
    /*
     * For this test to pass please comment out the validation part in the validateWebAnnotation() method
     * in the BaseAnnotationValidator class, if the whitelists are not created
     */
    @Disabled
    @Test
    public void checkAnnotationDuplicatesCreateObjectLinks() throws Exception {
        ResponseEntity<String> response = storeTestAnnotation(LINK_MINIMAL, true, null);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        response = storeTestAnnotation(LINK_MINIMAL, true, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

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

//  @Test
    public void agentIDNotUrlMustThrowException() throws Exception {
        
        String requestBody = AnnotationTestUtils.getJsonStringInput(WRONG_AGENT_ID_NOT_URL);
        
        ResponseEntity<String> response = storeTestAnnotationByType(
            false, requestBody, null, null);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());     
        
    }
    
    protected Annotation createAndValidateTag(String inputFile) throws Exception {
      return createTag(inputFile, true, true);
    }
  
    @Test
    public void createDescribing1() throws Exception {
        
        String requestBody = AnnotationTestUtils.getJsonStringInput(DESCRIBING_CHO);
        
        ResponseEntity<String> response = storeTestAnnotationByType(
                true, requestBody, null, null);
                                
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        
        Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
        createdAnnotations.add(storedAnno.getIdentifier());
                                
        assertTrue(storedAnno.getMotivation().equals(MotivationTypes.DESCRIBING.name().toLowerCase()));
        assertEquals(storedAnno.getBody().getInternalType(), BodyInternalTypes.TEXT.name());
    }
    
    @Test
    public void createDescribing2() throws Exception {
        
        String requestBody = AnnotationTestUtils.getJsonStringInput(DESCRIBING_WEB_RESOURCE);
        
        ResponseEntity<String> response = storeTestAnnotationByType(
                true, requestBody, 
                null, null
                );
                                
        assertNotNull(response.getBody());
        assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        
        Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
        createdAnnotations.add(storedAnno.getIdentifier());
                
        assertTrue(storedAnno.getMotivation().equals(MotivationTypes.DESCRIBING.name().toLowerCase()));
        assertTrue(storedAnno.getTarget().getSource() != null);
        assertEquals(storedAnno.getBody().getInternalType(), BodyInternalTypes.TEXT.name());
    }
    
    @Test
    public void createLinkAnnotation() throws Exception {
        ResponseEntity<String> response = storeTestAnnotation(LINK_STANDARD, true, null);
        AnnotationTestUtils.validateResponse(response);  
        
        Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
        createdAnnotations.add(storedAnno.getIdentifier());
    }
    
    @Test
    public void createIsSimilarToLink() throws Exception {
        String requestBody = AnnotationTestUtils.getJsonStringInput(LINK_EDM_IS_SIMMILAR_TO);
        Annotation inputAnno = AnnotationTestUtils.parseAnnotation(requestBody, MotivationTypes.LINKING);
        Annotation storedAnno = createLink(requestBody);
        createdAnnotations.add(storedAnno.getIdentifier());
        //validate the reflection of input in output!
        AnnotationTestUtils.validateOutputAgainstInput(storedAnno, inputAnno);
    }
    
    @Test
    public void createIsSimilarToMinimalLink() throws Exception {
        String requestBody = AnnotationTestUtils.getJsonStringInput(LINK_EDM_IS_SIMMILAR_TO_MINIMAL);
        Annotation inputAnno = AnnotationTestUtils.parseAnnotation(requestBody, MotivationTypes.LINKING);
        Annotation storedAnno = createLink(requestBody);
        createdAnnotations.add(storedAnno.getIdentifier());
        //validate the reflection of input in output!
        AnnotationTestUtils.validateOutputAgainstInput(storedAnno, inputAnno);
    }

    @Test
    public void createLinkForContributingBodyObject() throws Exception {        
        ResponseEntity<String> response = storeTestAnnotation(LINK_FOR_CONTRIBUTING_BODY_OBJECT, true, null);
        AnnotationTestUtils.validateResponse(response); 
        Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
        createdAnnotations.add(storedAnno.getIdentifier());
    }
    
    @Test
    public void createLinkForContributingBodyString() throws Exception {        
        ResponseEntity<String> response = storeTestAnnotation(LINK_FOR_CONTRIBUTING_BODY_STRING, true, null);
        AnnotationTestUtils.validateResponse(response);     
        Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
        createdAnnotations.add(storedAnno.getIdentifier());
    }

    protected Annotation parseCaption(String jsonString) throws JsonParseException {
    MotivationTypes motivationType = MotivationTypes.CAPTIONING;
    return AnnotationTestUtils.parseAnnotation(jsonString, motivationType);
    }

    @Test
    public void createMinimalCaption() throws Exception {

    String requestBody = AnnotationTestUtils.getJsonStringInput(CAPTION_MINIMAL);
    Annotation inputAnno = parseCaption(requestBody);

    Annotation storedAnno = createTestAnnotation(CAPTION_MINIMAL, true, null);
    createdAnnotations.add(storedAnno.getIdentifier());

    // validate the reflection of input in output!
    AnnotationTestUtils.validateOutputAgainstInput(storedAnno, inputAnno);
    }
  
    protected Annotation parseSubtitle(String jsonString) throws JsonParseException {
    MotivationTypes motivationType = MotivationTypes.SUBTITLING;
    return AnnotationTestUtils.parseAnnotation(jsonString, motivationType);
    }
    
    @Test
    public void createMinimalSubtitle() throws Exception {

    String requestBody = AnnotationTestUtils.getJsonStringInput(SUBTITLE_MINIMAL);

    Annotation inputAnno = parseSubtitle(requestBody);

    Annotation storedAnno = createTestAnnotation(SUBTITLE_MINIMAL, true, null);
    createdAnnotations.add(storedAnno.getIdentifier());

    // validate the reflection of input in output!
    AnnotationTestUtils.validateOutputAgainstInput(storedAnno, inputAnno);
    
    Assertions.assertTrue(true);
    }
 
    @Test
    public void createSemanticTagSimpleMinimal() throws Exception {

        Annotation anno = createAndValidateTag(SEMANTICTAG_SIMPLE_MINIMAL);
        createdAnnotations.add(anno.getIdentifier());
        log.info(anno.getBody().getInternalType());
    }

    @Test
    public void createSemanticTagSimpleStandard() throws Exception {

        Annotation anno = createAndValidateTag(SEMANTICTAG_SIMPLE_STANDARD);
        createdAnnotations.add(anno.getIdentifier());
        log.info(anno.getBody().getInternalType());
    }

    @Test
    public void createSemanticTagSpecificMinimal() throws Exception {

        Annotation anno = createAndValidateTag(SEMANTICTAG_SPECIFIC_MINIMAL);
        createdAnnotations.add(anno.getIdentifier());
        log.info(anno.getBody().getInternalType());
    }

    @Test
    public void createSemanticTagSpecificStandard() throws Exception {

        Annotation anno = createAndValidateTag(SEMANTICTAG_SPECIFIC_STANDARD);
        createdAnnotations.add(anno.getIdentifier());
        log.info(anno.getBody().getInternalType());
    }

    @Test
    public void createSemanticTagWebResource() throws Exception {

        Annotation storedAnno = createTag(SEMANTICTAG_WEB_RESOURCE, false, true);
        createdAnnotations.add(storedAnno.getIdentifier());
        log.info(storedAnno.getBody().getInternalType());
        assertTrue(storedAnno.getMotivation().equals(MotivationTypes.TAGGING.name().toLowerCase()));
        assertTrue(storedAnno.getTarget().getSource() != null);
        assertEquals(storedAnno.getBody().getInternalType(), BodyInternalTypes.SEMANTIC_TAG.name());
    }

    @Test
    public void createSemanticTagEntity() throws Exception {

        Annotation storedAnno = createTag(SEMANTICTAG_ENTITY, false, true);
        createdAnnotations.add(storedAnno.getIdentifier());
        log.info(storedAnno.getBody().getInternalType());
        assertTrue(storedAnno.getMotivation().equals(MotivationTypes.TAGGING.name().toLowerCase()));
        assertEquals(storedAnno.getBody().getInternalType(), BodyInternalTypes.AGENT.name());
        EdmAgent agentBody = (EdmAgent) ((EdmAgentBody) storedAnno.getBody()).getAgent();
        assertNotNull(agentBody.getDateOfBirth());
        assertNotNull(agentBody.getDateOfDeath());
        assertNotNull(agentBody.getPlaceOfBirth());
        assertNotNull(agentBody.getPlaceOfDeath());
        assertNotNull(agentBody.getPrefLabel());
    }

    @Test
    public void createSemanticTagAgentEntity() throws Exception {

        Annotation storedAnno = createTag(SEMANTICTAG_AGENT_ENTITY, false, true);
        createdAnnotations.add(storedAnno.getIdentifier());
        log.info(storedAnno.getBody().getInternalType());
        assertTrue(storedAnno.getMotivation().equals(MotivationTypes.TAGGING.name().toLowerCase()));
        assertEquals(storedAnno.getBody().getInternalType(), BodyInternalTypes.AGENT.name());
        EdmAgent agentBody = (EdmAgent) ((EdmAgentBody) storedAnno.getBody()).getAgent();
        assertNotNull(agentBody.getPrefLabel());
        assertTrue(agentBody.getPrefLabel().size() == 43);
        assertNotNull(agentBody.getDateOfBirth());
        assertNotNull(agentBody.getDateOfDeath());
    }

    @Test
    public void createTagBodyText() throws Exception {
        
        String requestBody = AnnotationTestUtils.getJsonStringInput(TAG_BODY_TEXT);
        
        Annotation storedAnno = createTag(requestBody);
        createdAnnotations.add(storedAnno.getIdentifier());
        
        Annotation inputAnno = parseTag(requestBody);
        
        //validate the reflection of input in output!
        AnnotationTestUtils.validateOutputAgainstInput(storedAnno, inputAnno);
    }
    
    @Test
    public void createTagMinimal() throws Exception {
        
        String requestBody = AnnotationTestUtils.getJsonStringInput(TAG_MINIMAL);
        
        Annotation storedAnno = createTag(requestBody);
        createdAnnotations.add(storedAnno.getIdentifier());
        
        Annotation inputAnno = parseTag(requestBody);
        
        //validate the reflection of input in output!
        AnnotationTestUtils.validateOutputAgainstInput(storedAnno, inputAnno);
    }
    

    @Test
    public void createTagStandard() throws Exception {
        
        String requestBody = AnnotationTestUtils.getJsonStringInput(TAG_STANDARD);
        
        Annotation storedAnno = createTag(requestBody);
        createdAnnotations.add(storedAnno.getIdentifier());
        
        Annotation inputAnno = parseTag(requestBody);
        //validate the reflection of input in output!
        //but ignore generated timestamp which is always set by the server
        inputAnno.setGenerated(storedAnno.getGenerated());
        AnnotationTestUtils.validateOutputAgainstInput(storedAnno, inputAnno);
    }
    
    @Test
    public void createGeoTag() throws Exception {
        
        String requestBody = AnnotationTestUtils.getJsonStringInput(TAG_GEOTAG);
        
        Annotation inputAnno = parseTag(requestBody);
        
        Annotation storedAnno = createTag(requestBody);
        createdAnnotations.add(storedAnno.getIdentifier());
        
        assertTrue(BodyInternalTypes.isGeoTagBody(storedAnno.getBody().getInternalType()));
        //validate the reflection of input in output!
        AnnotationTestUtils.validateOutputAgainstInput(storedAnno, inputAnno);
    }
    
    @Test
    public void createWrongTag() throws Exception {
        
        String requestBody = AnnotationTestUtils.getJsonStringInput(TAG_MINIMAL_WRONG);
        
        ResponseEntity<String> response = storeTestAnnotationByType(true, requestBody,  WebAnnotationFields.TAG, null);
        
        assertEquals(response.getStatusCode().value(), HttpStatus.BAD_REQUEST.value());
        //log.debug("Error message: " + );
    }
    
    @Test
    public void createTagWithoutBody() throws Exception {
        
        String requestBody = AnnotationTestUtils.getJsonStringInput(TAG_WITHOUT_BODY);
        
        ResponseEntity<String> response = storeTestAnnotationByType(true, requestBody,  WebAnnotationFields.TAG, null);
        
        assertEquals(response.getStatusCode().value(), HttpStatus.BAD_REQUEST.value());
        //log.debug("Error message: " + );
    }
    
    
    @Test
    public void createWrongGeoTagLat() throws Exception {
        
        String requestBody = AnnotationTestUtils.getJsonStringInput(TAG_GEO_WRONG_LAT);
        
        ResponseEntity<String> response = storeTestAnnotationByType(true, requestBody,  WebAnnotationFields.TAG, null);
        
        assertEquals(response.getStatusCode().value(), HttpStatus.BAD_REQUEST.value());
    }
    
    @Test
    public void createWrongGeoTagLong() throws Exception {
        
        String requestBody = AnnotationTestUtils.getJsonStringInput(TAG_GEO_WRONG_LONG);
        
        ResponseEntity<String> response = storeTestAnnotationByType(true, requestBody,  WebAnnotationFields.TAG, null);
        
        assertEquals(response.getStatusCode().value(), HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void createCanonicalTag() throws Exception {
            
      Annotation storedAnno = createAndValidateTag(TAG_CANONICAL);
      createdAnnotations.add(storedAnno.getIdentifier());
    }
    
    @Test
    public void createViaTagString() throws Exception {
        
        Annotation storedAnno = createAndValidateTag(TAG_VIA_STRING);
        createdAnnotations.add(storedAnno.getIdentifier());
    }
    
    @Test
    public void createViaTagArray() throws Exception {

        Annotation storedAnno = createAndValidateTag(TAG_VIA_ARRAY);
        createdAnnotations.add(storedAnno.getIdentifier());
    }

    @Test
    public void createSemanticTagWithVcardAddress() throws Exception {

        Annotation storedAnno = createTag(SEMANTICTAG_VCARD_ADDRESS, false, true);
        createdAnnotations.add(storedAnno.getIdentifier());
        log.info(storedAnno.getBody().getInternalType());
        assertTrue(storedAnno.getMotivation().equals(MotivationTypes.TAGGING.name().toLowerCase()));
        assertEquals(storedAnno.getBody().getInternalType(), BodyInternalTypes.VCARD_ADDRESS.name());
        assertEquals(
                ((VcardAddressBody) storedAnno.getBody()).getAddress().getVcardCountryName()
                ,"The Netherlands");
        assertNotNull(
                ((VcardAddressBody) storedAnno.getBody()).getAddress().getVcardHasGeo());
        assertNotNull(
                ((VcardAddressBody) storedAnno.getBody()).getAddress().getVcardLocality());
        assertNotNull(
                ((VcardAddressBody) storedAnno.getBody()).getAddress().getVcardPostalCode());
        assertNotNull(
                ((VcardAddressBody) storedAnno.getBody()).getAddress().getVcardPostOfficeBox());
        assertNotNull(
                ((VcardAddressBody) storedAnno.getBody()).getAddress().getVcardStreetAddress());
    }    
    
    protected Annotation parseTranscription(String jsonString) throws JsonParseException {
      MotivationTypes motivationType = MotivationTypes.TRANSCRIBING;
      return AnnotationTestUtils.parseAnnotation(jsonString, motivationType);     
    }
    
    @Test
    public void createMinimalTranscription() throws Exception {

    String requestBody = AnnotationTestUtils.getJsonStringInput(TRANSCRIPTION_MINIMAL);
    Annotation inputAnno = parseTranscription(requestBody);

    Annotation storedAnno = createTestAnnotation(TRANSCRIPTION_MINIMAL, true, null);
    createdAnnotations.add(storedAnno.getIdentifier());
    
    // validate the reflection of input in output!
    AnnotationTestUtils.validateOutputAgainstInput(storedAnno, inputAnno);
    }
   
    @Test
    public void createTranscriptionWithRights() throws Exception {

    String requestBody = AnnotationTestUtils.getJsonStringInput(TRANSCRIPTION_WITH_RIGHTS);
    Annotation inputAnno = parseTranscription(requestBody);

    Annotation storedAnno = createTestAnnotation(TRANSCRIPTION_WITH_RIGHTS, true, null);
    createdAnnotations.add(storedAnno.getIdentifier());

    // validate the reflection of input in output!
    AnnotationTestUtils.validateOutputAgainstInput(storedAnno, inputAnno);

    assertEquals(ResourceTypes.FULL_TEXT_RESOURCE.name(), storedAnno.getBody().getInternalType());
    assertTrue(storedAnno.getBody().getEdmRights().equals(inputAnno.getBody().getEdmRights()));
    
    }

    @Test
    public void createTranscriptionWithoutRights() throws Exception {

    ResponseEntity<String> response = storeTestAnnotation(TRANSCRIPTION_WITHOUT_RIGHTS, true, null);
    assertEquals(org.apache.commons.httpclient.HttpStatus.SC_BAD_REQUEST, response.getStatusCodeValue());
    String expectedMessage = "Missing mandatory field! transcription.body.edmRights";
    assertTrue(response.getBody().contains(expectedMessage));
    }
    
    @Test
    public void createTranscriptionWithoutLanguage() throws Exception {

    ResponseEntity<String> response = storeTestAnnotation(TRANSCRIPTION_WITHOUT_LANG, true, null);
    assertEquals(org.apache.commons.httpclient.HttpStatus.SC_BAD_REQUEST, response.getStatusCodeValue());
    String expectedMessage = "Missing mandatory field! transcription.body.language";
    assertTrue(response.getBody().contains(expectedMessage));
    }
    
    @Test
    public void createTranscriptionWithoutValue() throws Exception {

    ResponseEntity<String> response = storeTestAnnotation(TRANSCRIPTION_WITHOUT_VALUE, true, null);
    assertEquals(org.apache.commons.httpclient.HttpStatus.SC_BAD_REQUEST, response.getStatusCodeValue());
    String expectedMessage = "Missing mandatory field! transcription.body.value";
    assertTrue(response.getBody().contains(expectedMessage));
    }
    
    @Test
    public void createTranscriptionWithAltoBody() throws Exception {
        Annotation storedAnno = createTestAnnotation(TRANSCRIPTION_WITH_ALTO_BODY, true, null);
        createdAnnotations.add(storedAnno.getIdentifier());
    	ResponseEntity<String> response = storeTestAnnotation(TRANSCRIPTION_WITH_ALTO_BODY_WRONG, true, null);
    	assertEquals(response.getStatusCode().value(), HttpStatus.BAD_REQUEST.value());    	
    }
    
    @Test
	public void createTranscriptionWithPageXmlBody() throws Exception {
        Annotation storedAnno = createTestAnnotation(TRANSCRIPTION_WITH_PAGE_XML_BODY, true, null);
        createdAnnotations.add(storedAnno.getIdentifier());
    	ResponseEntity<String> response = storeTestAnnotation(TRANSCRIPTION_WITH_PAGE_XML_BODY_WRONG, true, null);
    	assertEquals(response.getStatusCode().value(), HttpStatus.BAD_REQUEST.value());        
    }    
    
}
