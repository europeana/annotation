package eu.europeana.annotation.client.integration.webanno;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import eu.europeana.annotation.client.WebAnnotationAuxilaryMethodsApi;
import eu.europeana.annotation.client.WebAnnotationAuxilaryMethodsApiImpl;
import eu.europeana.annotation.client.WebAnnotationProtocolApi;
import eu.europeana.annotation.client.WebAnnotationProtocolApiImpl;
import eu.europeana.annotation.client.admin.WebAnnotationAdminApi;
import eu.europeana.annotation.client.admin.WebAnnotationAdminApiImpl;
import eu.europeana.annotation.client.config.ClientConfiguration;
import eu.europeana.annotation.client.utils.BaseUtils;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.search.SearchProfiles;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.definitions.model.vocabulary.StatusTypes;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.vocabulary.fields.WebAnnotationModelKeywords;
import eu.europeana.annotation.utils.parse.AnnotationLdParser;

public class BaseWebAnnotationTest {
    
	protected Logger log = LogManager.getLogger(getClass());
	
	private static List<Annotation> createdAnnotations = new ArrayList<Annotation>();

	String TEST_STATUS = StatusTypes.PRIVATE.name().toLowerCase();

	public static final String LINK_MINIMAL = "/link/minimal.json";
	public static final String LINK_STANDARD = "/link/standard.json";
	public static final String TAG_STANDARD = "/tag/standard.json";
	public static final String TAG_MINIMAL = "/tag/minimal.json";
	public static final String TAG_STANDARD_TEST_VALUE = "/tag/standard_test_value.json";
	public static final String TAG_STANDARD_TEST_VALUE_BODY = "test";

	protected String get_TAG_STANDARD_TEST_VALUE_TARGET() {
	  return ClientConfiguration.getInstance().getPropAnnotationItemDataEndpoint() + "/09102/_UEDIN_214";
	}
	//	public static final String FULL_TEXT_RESOURCE = "/tag/full_text_resource.json";
	public static final String TAG_ANNOTATION = "/tag/annotation.json";
	public static final String WHITELIST_ENTRY = "/whitelist/entry.json";
	public static final String SUBTITLE_MINIMAL = "/subtitle/minimal.json";
	public static final String SUBTITLE_SRT = "/subtitle/subtitle_srt.json";
    public static final String CAPTION_MINIMAL = "/caption/minimal.json";
	public static final String LINK_FOR_CONTRIBUTING_BODY_OBJECT = "/linkforcontributing/link_for_contributing_body_object.json";
	public static final String LINK_FOR_CONTRIBUTING_BODY_STRING = "/linkforcontributing/link_for_contributing_body_string.json";
	public static final String LINK_FOR_CONTRIBUTING_TARGET_SPECIFIC = "/linkforcontributing/link_for_contributing_target_specific.json";
	public static final String LINK_FOR_CONTRIBUTING_TARGET_SPECIFIC_ID = "/linkforcontributing/link_for_contributing_target_specific_id.json";
	
	public static final String VALUE_TESTSET = "generator_uri: \"http://test.europeana.org/45e86248-1218-41fc-9643-689d30dbe651\"";
	public static final String VALUE_ID = "anno_id:";
		
	public static final String VALUE_SEARCH_DESCRIBING_TARGET_SOURCE = "target_uri:\"http://www.europeana1914-1918.eu/attachments/2020601/20841.235882.full.jpg\"";
	
	public static final String VALUE_TAG_BODY_URI = "http://www.geonames.org/2988507";
	public static final String VALUE_SEARCH_TAG_BODY_URI = "body_uri:\"" +VALUE_TAG_BODY_URI+ "\"";
	
	public static final String VALUE_DESCRIBING_BODY_VALUE = "body_value:\"the textual description of the item\"";
	public static final String VALUE_SEARCH_DESCRIBING_BODY_VALUE = "body_value:\"textual description\"";
	
	public static final String VALUE_TAG_BODY_VALUE = "trombone";
	public static final String VALUE_SEARCH_TAG_BODY_VALUE = "body_value:\""+VALUE_TAG_BODY_VALUE+"\"";
	
	public static final String VALUE_BODY_LINK_RELATION = "isSimilarTo";
	public static final String VALUE_SEARCH_BODY_LINK_RELATION = "link_relation:\"" +VALUE_BODY_LINK_RELATION+ "\"";
	
	public static final String VALUE_BODY_LINK_RESOURCE_URI = "https://www.wikidata.org/wiki/Q762";
	public static final String VALUE_SEARCH_BODY_LINK_RESOURCE_URI = "link_resource_uri:\""+VALUE_BODY_LINK_RESOURCE_URI+"\"";
	
	
	public static final String VALUE_BODY_SPECIFIC_RESOURCE = "http://www.geonames.org/2988507"; // source
	public static final String VALUE_SEARCH_BODY_SPECIFIC_RESOURCE = "body_uri:\""+VALUE_BODY_SPECIFIC_RESOURCE+"\""; // source
	
	public static final String VALUE_BODY_FULL_TEXT_RESOURCE = "... complete transcribed text in HTML ...";
	public static final String VALUE_SEARCH_BODY_FULL_TEXT_RESOURCE = "body_value:\"transcribed text in HTML\"";
	public static final String VALUE_SEARCH_BODY_VALUE_IT = "body_value.it:(con il grande finale)";

	public static final String TAG_BODY_TEXT = "/tag/bodyText.json";
	public static final String TAG_MINIMAL_WRONG = "/tag/wrong/minimal_wrong.json";
	public static final String TAG_WITHOUT_BODY = "/tag/wrong/without_body.json";
    public static final String TAG_GEO_WRONG_LAT = "/tag/wrong/geotag_wrong_lat.json";
	public static final String TAG_GEO_WRONG_LONG = "/tag/wrong/geotag_wrong_long.json";
	public static final String TAG_GEOTAG = "/tag/geotag.json";
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
	public static final String TRANSCRIPTION_MINIMAL_DUPLICATE_UPDATE = "/transcription/minimal-duplicate-update.json";
	public static final String TRANSCRIPTION_MINIMAL = "/transcription/minimal.json";
	public static final String TRANSLATION_MINIMAL = "/translation/minimal.json";
	public static final String DESCRIBING_WITHOUT_BODY_LANGUAGE = "/describing/web_resource_without_body_language.json";
	public static final String TAGGING_BODY_ADDRESS_NO_TYPE = "/tag/wrong/body_address_no_type.json";
	public static final String TAGGING_BODY_ADDRESS_NO_STREET_ADDRESS = "/tag/wrong/body_address_no_street_address.json";
	
	String START = "{";
	String END = "}";
	String TYPE = "\"@context\": \"" + WebAnnotationModelKeywords.WA_CONTEXT + "\","
			+ "\"type\": \"oa:Annotation\",";

	//TODO: migrate old test cases to use annotations from test files
	String ANNOTATED_SERIALIZED = "\"creator\": {" + "\"id\": \"https://www.historypin.org/en/person/55376/\","
			+ "\"type\": \"Person\"," + "\"name\": \"John Smith\"" + "},"
			+ "\"created\": \"2015-02-27T12:00:43Z\"," + "\"generated\": \"2015-02-28T13:00:34Z\","
			+ "\"generator\": \"http://www.historypin.org\",";

	String EQUIVALENT_TO = "\"oa:equivalentTo\": \"https://www.historypin.org/en/item/456\",";

	public String BODY_VALUE_TO_TRIM = " überhaupt ";
	public String BODY_VALUE_AFTER_TRIMMING = "überhaupt";

	protected String get_TAG_JSON_BY_TYPE_JSONLD() {
	  String TAG_CORE = TYPE + ANNOTATED_SERIALIZED + "\"bodyValue\": \"church\","
          + "\"target\": \"" + ClientConfiguration.getInstance().getPropAnnotationItemDataEndpoint() + "/123/xyz\"," + EQUIVALENT_TO;
	  return START + TAG_CORE + END;
	}
	
	protected String get_TAG_JSON_VALIDATION() {
	   String TAG_CORE_VALIDATION = TYPE + ANNOTATED_SERIALIZED + 
	       "\"motivation\": \"oa:tagging\"," +
	       "\"bodyValue\": \"" + BODY_VALUE_TO_TRIM + "\"," +
	       "\"target\": \"" + ClientConfiguration.getInstance().getPropAnnotationItemDataEndpoint() + "/123/xyz\"," + EQUIVALENT_TO;
	  return START + TAG_CORE_VALIDATION + END;
	}

	protected String get_LINK_CORE() {
	   String LINK_CORE = TYPE + ANNOTATED_SERIALIZED + "\"target\": ["
	       + "\"" + ClientConfiguration.getInstance().getPropAnnotationItemDataEndpoint() +  "/123/xyz.html\","
	       + "\"" + ClientConfiguration.getInstance().getPropAnnotationItemDataEndpoint() +  "/333/xyz.html\"" + "]," + EQUIVALENT_TO;
	  return LINK_CORE;
	}

	protected String get_LINK_JSON_BY_TYPE_JSONLD() {
	   String LINK_CORE = TYPE + ANNOTATED_SERIALIZED + "\"target\": ["
	       + "\"" + ClientConfiguration.getInstance().getPropAnnotationItemDataEndpoint() +  "/123/xyz.html\","
	       + "\"" + ClientConfiguration.getInstance().getPropAnnotationItemDataEndpoint() +  "/333/xyz.html\"" + "]," + EQUIVALENT_TO;
	  return START + LINK_CORE + END;
	}

//	public String LINK_JSON = START + LINK_CORE + "\"motivation\": \"oa:linking\"," + END;
	
	private static WebAnnotationProtocolApi apiProtocolClient;
	private static WebAnnotationAuxilaryMethodsApi apiAuxilaryMethodsClient;

	@BeforeAll
	public static void initObjects() {
		apiProtocolClient = new WebAnnotationProtocolApiImpl();
		apiAuxilaryMethodsClient =  new WebAnnotationAuxilaryMethodsApiImpl();
	}

	public WebAnnotationProtocolApi getApiProtocolClient() {
		return apiProtocolClient;
	}
	
	public WebAnnotationAuxilaryMethodsApi getApiAuxilaryMethodsClient() {
		return apiAuxilaryMethodsClient;
	}

	/**
	 * This method creates test annotation object
	 * 
	 * @return response entity that contains response body, headers and status
	 *         code.
	 * @throws IOException 
	 */
	protected ResponseEntity<String> storeTestAnnotation(String jsonFile) throws IOException {
	    return storeTestAnnotation(jsonFile, true, null);
	}
	


	/**
	 * This method creates test annotation object and allows choosing if it should be indexed or not.
	 * @param user 
	 * 
	 * @return response entity that contains response body, headers and status
	 *         code.
	 * @throws IOException 
	 */
	private ResponseEntity<String> storeTestAnnotation(String inputFile, boolean indexOnCreate, String user) throws IOException {

		String requestBody = getJsonStringInput(inputFile);
		
		/**
		 * store annotation
		 */
		ResponseEntity<String> storedResponse = getApiProtocolClient().createAnnotation(indexOnCreate, requestBody, null, user);
		return storedResponse;
	}
	

	/**
	 * This method creates test annotation report object
	 * 
	 * @param apiKey
	 * @param identifier
	 * @param userToken
	 * @return response entity that contains response body, headers and status code.
	 */
	protected ResponseEntity<String> storeTestAnnotationReport(
			String apiKey
			, long identifier
			, String userToken) {

		/**
		 * store annotation report
		 */
		ResponseEntity<String> storedResponse = getApiProtocolClient().createAnnotationReport(
				apiKey, identifier, userToken);
		return storedResponse;
	}

	/**
	 * This method retrieves test annotation moderation report object
	 * 
	 * @param apiKey
	 * @param provider
	 * @param identifier
	 * @param userToken
	 * @return response entity that contains response body, headers and status code.
	 */
	protected ResponseEntity<String> getModerationReport(
			String apiKey
			, String provider
			, long identifier
			, String userToken) {

		/**
		 * get annotation report
		 */
		ResponseEntity<String> storedResponse = getApiProtocolClient().getModerationReport(
				apiKey, identifier, userToken);
		return storedResponse;
	}

	/**
	 * This method creates test annotation object
	 * @param user 
	 * 
	 * @return response entity that contains response body, headers and status
	 *         code.
	 * @throws JsonParseException
	 * @throws IOException 
	 */
	protected Annotation createTestAnnotation(String inputFile, String user) throws JsonParseException, IOException {

		return createTestAnnotation(inputFile, true, user);
		
	}

	/**
	 * This method creates test annotation object allowing to decide if it should be indexed or not.
	 * 
	 * @param inputFile Annotation tag
	 * @param indexOnCreate Flag to decide if the test annotation should be indexed or not
	 * @param user 
	 * @return response entity that contains response body, headers and status
	 *         code.
	 * @throws JsonParseException
	 * @throws IOException 
	 */
	protected Annotation createTestAnnotation(String inputFile, boolean indexOnCreate, String user) throws JsonParseException, IOException {

		ResponseEntity<String> response = storeTestAnnotation(inputFile, indexOnCreate, user);
		Annotation annotation = parseAndVerifyTestAnnotation(response);

		return annotation;
		
	}

	protected Annotation parseAndVerifyTestAnnotation(ResponseEntity<String> response) throws JsonParseException {
		
		return parseAndVerifyTestAnnotation(response, HttpStatus.CREATED);
	}
	
	protected Annotation parseAndVerifyTestAnnotation(ResponseEntity<String> response, HttpStatus status) throws JsonParseException {
		assertEquals(status.value(), response.getStatusCode().value());

		Annotation annotation = getApiProtocolClient().parseResponseBody(response);
		assertNotNull(annotation.getCreator());
		

		return annotation;
	}
	
	protected Annotation parseAndVerifyTestAnnotationUpdate(ResponseEntity<String> response) throws JsonParseException {
		
		return parseAndVerifyTestAnnotation(response, HttpStatus.OK);
	}
	
	protected Annotation parseAndVerifyTestAnnotationUpdate(ResponseEntity<String> response, HttpStatus status) throws JsonParseException {
		assertEquals(""+status.value(), ""+response.getStatusCode().value());

		Annotation annotation = getApiProtocolClient().parseResponseBody(response);

		return annotation;
	}

	public String getApiKey() {

		return ClientConfiguration.getInstance().getApiKey();
		// return TEST_WSKEY;
	}

	protected String getJsonStringInput(String resource) throws IOException {
		return (new BaseUtils()).getJsonStringInput(resource);
	}
	
	protected Annotation parseAnnotation(String jsonString, MotivationTypes motivationType) throws JsonParseException {
		AnnotationLdParser europeanaParser = new AnnotationLdParser();
		return europeanaParser.parseAnnotation(motivationType, jsonString);
	}

	protected void validateOutputAgainstInput(Annotation storedAnno, Annotation inputAnno)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
				
				Method[] methods = inputAnno.getClass().getMethods();
				Method currentMethod;
				Object inputProp;
				Object storedProp;
				
				for (int i = 0; i < methods.length; i++) {
					currentMethod = methods[i];
					if(currentMethod.getName().startsWith("get") && !isTechnicalMethod(currentMethod.getName())
							&& !isCreator(currentMethod.getName()) && !isGenerator(currentMethod.getName()) 
							&& !isIdentifier(currentMethod.getName())){
						inputProp = currentMethod.invoke(inputAnno, (Object[]) null);
						
						//compare non null fields only
						if(inputProp != null){
							storedProp = currentMethod.invoke(storedAnno, (Object[])null);
							
							if(inputProp instanceof String[])
								assertArrayEquals((String[])inputProp, (String[])storedProp);
							else
								assertEquals(inputProp, storedProp);
						}			
					}			
				}
				
			}

	private boolean isTechnicalMethod(String name) {
		return "getIdAsString".equals(name);
	}

	private boolean isGenerator(String name) {
		return "getGenerator".equals(name);
	}

	private boolean isCreator(String name) {
		return "getCreator".equals(name);
	}
	
    private boolean isIdentifier(String name) {
       return "getIdentifier".equals(name);
    }

	protected Annotation createTag(String requestBody) throws JsonParseException {
	    ResponseEntity<String> response = getApiProtocolClient().createAnnotation(
			true, requestBody,  WebAnnotationFields.TAG, null);
	    Annotation storedAnno = getApiProtocolClient().parseResponseBody(response);
	    return storedAnno;
	}

	protected Annotation createLink(String requestBody) throws JsonParseException {
		ResponseEntity<String> response = getApiProtocolClient().createAnnotation(
				true, requestBody, WebAnnotationFields.LINK 
				//null 
, null
				);
				
				
		assertNotNull(response.getBody());
		assertEquals(response.getStatusCode(), HttpStatus.CREATED);
		
		Annotation storedAnno = getApiProtocolClient().parseResponseBody(response);
		assertNotNull(storedAnno.getCreator());
//		assertNotNull(storedAnno.getGenerator());
		return storedAnno;
	}
	
//	protected Annotation storeTranscription(String requestBody) throws JsonParseException {
//	    createTestAnnotation
//	    ResponseEntity<String> response = getApiClient().createAnnotation(
//				true, requestBody, null);
//				
//				
//		assertNotNull(response.getBody());
//		assertEquals(response.getStatusCode(), HttpStatus.CREATED);
//		
//		Annotation storedAnno = getApiClient().parseResponseBody(response);
//		assertNotNull(storedAnno.getCreator());
//		//assertNotNull(storedAnno.getGenerator()); Generator is currently purposly not serialized
//		return storedAnno;
//	}
	
	/**
	 * @param annotation
	 */
	protected void deleteAnnotation(Annotation annotation) {
		deleteAnnotation(annotation.getIdentifier());
	}
	
    protected void deleteAnnotation(long identifier) {
		WebAnnotationAdminApi webannoAdminApi = new WebAnnotationAdminApiImpl();
		ResponseEntity<String> re = webannoAdminApi.deleteAnnotation(identifier);
		assertEquals(HttpStatus.NO_CONTENT, re.getStatusCode());
		log.trace("Annotation deleted: /" + identifier);
	}
  
    protected String removeAnnotation(long identifier) {
      WebAnnotationAdminApi webannoAdminApi = new WebAnnotationAdminApiImpl();
      ResponseEntity<String> re = webannoAdminApi.removeAnnotation(identifier);
      return re.getBody();
    }
		
	protected ResponseEntity<String> getAnnotation(Annotation anno) {
		return getApiProtocolClient().getAnnotation(getApiKey(), anno.getIdentifier());
	}

	protected ResponseEntity<String> getAnnotation(Annotation anno, SearchProfiles searchProfile) {
		return getApiProtocolClient().getAnnotation(getApiKey(), anno.getIdentifier(), searchProfile);
	}

	protected ResponseEntity<String> getAnnotationByJwtToken(Annotation anno, SearchProfiles searchProfile) {
		return getApiProtocolClient().getAnnotation(anno.getIdentifier(), searchProfile);
	}
	
	protected Annotation validateResponse(ResponseEntity<String> response) throws JsonParseException {
		return validateResponse(response, HttpStatus.CREATED);
	}
	
	protected Annotation validateResponse(ResponseEntity<String> response, HttpStatus status) throws JsonParseException {
		assertNotNull(response.getBody());
		assertEquals(status, response.getStatusCode());
		
		Annotation storedAnno = getApiProtocolClient().parseResponseBody(response);
		assertNotNull(storedAnno.getIdentifier());
		return storedAnno;
	}

  public List<Annotation> getCreatedAnnotations() {
    return createdAnnotations;
  }

  public void addCreatedAnnotation(Annotation createdAnnotation) {
    createdAnnotations.add(createdAnnotation);
  }

  @AfterEach
  public void removeCreatedAnnotations() {
    for (Annotation annotation : createdAnnotations) {
       removeAnnotation(annotation.getIdentifier());
    }
    createdAnnotations.clear();
  }
}
