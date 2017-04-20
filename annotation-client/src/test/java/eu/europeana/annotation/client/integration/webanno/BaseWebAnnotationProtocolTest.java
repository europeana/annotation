package eu.europeana.annotation.client.integration.webanno;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertArrayEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.Before;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import eu.europeana.annotation.client.admin.WebAnnotationAdminApi;
import eu.europeana.annotation.client.admin.WebAnnotationAdminApiImpl;
import eu.europeana.annotation.client.config.ClientConfiguration;
import eu.europeana.annotation.client.webanno.WebAnnotationProtocolApi;
import eu.europeana.annotation.client.webanno.WebAnnotationProtocolApiImpl;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.definitions.model.vocabulary.StatusTypes;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.vocabulary.fields.WebAnnotationModelKeywords;
import eu.europeana.annotation.utils.parse.AnnotationLdParser;

public class BaseWebAnnotationProtocolTest {

	protected Logger log = Logger.getLogger(getClass());

	String TEST_STATUS = StatusTypes.PRIVATE.name().toLowerCase();

	public static final String LINK_MINIMAL = "/link/minimal.json";
	public static final String LINK_STANDARD = "/link/standard.json";
	public static final String TAG_STANDARD = "/tag/standard.json";
	public static final String TAG_STANDARD_TEST_VALUE = "/tag/standard_test_value.json";
	public static final String TAG_STANDARD_TEST_VALUE_BODY = "test";
	public static final String TAG_STANDARD_TEST_VALUE_TARGET = "http://data.europeana.eu/item/09102/_UEDIN_214";
	
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

	public String TAG_CORE = TYPE + ANNOTATED_SERIALIZED + "\"bodyValue\": \"church\","
			+ "\"target\": \"http://data.europeana.eu/item/123/xyz\"," + EQUIVALENT_TO;

	public String BODY_VALUE_TO_TRIM = " überhaupt ";
	public String BODY_VALUE_AFTER_TRIMMING = "überhaupt";

	public String TAG_CORE_VALIDATION = TYPE + ANNOTATED_SERIALIZED + 
			 "\"motivation\": \"oa:tagging\"," +
		     "\"bodyValue\": \"" + BODY_VALUE_TO_TRIM + "\"," +
			 "\"target\": \"http://data.europeana.eu/item/123/xyz\"," + EQUIVALENT_TO;

	public String TAG_JSON_BY_TYPE_JSONLD = START + TAG_CORE + END;
	
	public String TAG_JSON_VALIDATION = START + TAG_CORE_VALIDATION + END;
	
	public String LINK_CORE = TYPE + ANNOTATED_SERIALIZED + "\"target\": ["
			+ "\"http://data.europeana.eu/item/123/xyz.html\","
			+ "\"http://data.europeana.eu/item/333/xyz.html\"" + "]," + EQUIVALENT_TO;

	public String LINK_JSON_BY_TYPE_JSONLD = START + LINK_CORE + END;

	public String LINK_JSON = START + LINK_CORE + "\"motivation\": \"oa:linking\"," + END;

	public String TEST_USER_TOKEN = "tester1";
	public String ANONYMOUS_USER_TOKEN = "anonymous";

	private WebAnnotationProtocolApi apiClient;

	@Before
	public void initObjects() {
		apiClient = new WebAnnotationProtocolApiImpl();
	}

	public WebAnnotationProtocolApi getApiClient() {
		return apiClient;
	}

	/**
	 * This method creates test annotation object
	 * 
	 * @return response entity that contains response body, headers and status
	 *         code.
	 * @throws IOException 
	 */
	protected ResponseEntity<String> storeTestAnnotation() throws IOException {
		
		return storeTestAnnotation(TAG_STANDARD);

	}
	


	/**
	 * This method creates test annotation object
	 * 
	 * @return response entity that contains response body, headers and status
	 *         code.
	 * @throws IOException 
	 */
	protected ResponseEntity<String> storeTestAnnotation(String tag) throws IOException {

		String requestBody = getJsonStringInput(tag);
		
		/**
		 * store annotation
		 */
		ResponseEntity<String> storedResponse = getApiClient().createAnnotation(getApiKey(),
				WebAnnotationFields.PROVIDER_WEBANNO, null, requestBody, TEST_USER_TOKEN, null);
		return storedResponse;
	}
	


	/**
	 * This method creates test annotation object and allows choosing if it should be indexed or not.
	 * 
	 * @return response entity that contains response body, headers and status
	 *         code.
	 * @throws IOException 
	 */
	protected ResponseEntity<String> storeTestAnnotation(String tag, boolean indexOnCreate) throws IOException {

		String requestBody = getJsonStringInput(tag);
		
		/**
		 * store annotation
		 */
		ResponseEntity<String> storedResponse = getApiClient().createAnnotation(getApiKey(), 
				WebAnnotationFields.PROVIDER_WEBANNO, null, indexOnCreate, requestBody, TEST_USER_TOKEN, null);
		return storedResponse;
	}

	/**
	 * This method creates test annotation report object
	 * 
	 * @param apiKey
	 * @param provider
	 * @param identifier
	 * @param userToken
	 * @return response entity that contains response body, headers and status code.
	 */
	protected ResponseEntity<String> storeTestAnnotationReport(
			String apiKey
			, String provider
			, String identifier
			, String userToken) {

		/**
		 * store annotation report
		 */
		ResponseEntity<String> storedResponse = getApiClient().createAnnotationReport(
				apiKey, provider, identifier, userToken);
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
			, String identifier
			, String userToken) {

		/**
		 * get annotation report
		 */
		ResponseEntity<String> storedResponse = getApiClient().getModerationReport(
				apiKey, provider, identifier, userToken);
		return storedResponse;
	}

	/**
	 * This method creates test annotation object
	 * 
	 * @return response entity that contains response body, headers and status
	 *         code.
	 * @throws JsonParseException
	 * @throws IOException 
	 */
	protected Annotation createTestAnnotation() throws JsonParseException, IOException {
		return createTestAnnotation(TAG_STANDARD);
	}
	


	/**
	 * This method creates test annotation object
	 * 
	 * @return response entity that contains response body, headers and status
	 *         code.
	 * @throws JsonParseException
	 * @throws IOException 
	 */
	protected Annotation createTestAnnotation(String tag) throws JsonParseException, IOException {

		return createTestAnnotation(tag, true);
		
	}

	/**
	 * This method creates test annotation object allowing to decide if it should be indexed or not.
	 * 
	 * @param tag Annotation tag
	 * @param indexOnCreate Flag to decide if the test annotation should be indexed or not
	 * @return response entity that contains response body, headers and status
	 *         code.
	 * @throws JsonParseException
	 * @throws IOException 
	 */
	protected Annotation createTestAnnotation(String tag, boolean indexOnCreate) throws JsonParseException, IOException {

		ResponseEntity<String> response = storeTestAnnotation(tag, indexOnCreate);
		Annotation annotation = parseAndVerifyTestAnnotation(response);

		return annotation;
		
	}
	

	protected Annotation parseAndVerifyTestAnnotation(ResponseEntity<String> response) throws JsonParseException {
		
		return parseAndVerifyTestAnnotation(response, HttpStatus.CREATED);
	}
	
	protected Annotation parseAndVerifyTestAnnotation(ResponseEntity<String> response, HttpStatus status) throws JsonParseException {
		assertEquals(""+status.value(), ""+response.getStatusCode().value());

		Annotation annotation = getApiClient().parseResponseBody(response);

		assertEquals(WebAnnotationFields.PROVIDER_WEBANNO, annotation.getAnnotationId().getProvider());
		return annotation;
	}
	
	protected Annotation parseAndVerifyTestAnnotationUpdate(ResponseEntity<String> response) throws JsonParseException {
		
		return parseAndVerifyTestAnnotation(response, HttpStatus.OK);
	}
	
	protected Annotation parseAndVerifyTestAnnotationUpdate(ResponseEntity<String> response, HttpStatus status) throws JsonParseException {
		assertEquals(""+status.value(), ""+response.getStatusCode().value());

		Annotation annotation = getApiClient().parseResponseBody(response);

		assertEquals(WebAnnotationFields.PROVIDER_WEBANNO, annotation.getAnnotationId().getProvider());
		return annotation;
	}

	public String getApiKey() {

		return ClientConfiguration.getInstance().getApiKey();
		// return TEST_WSKEY;
	}

	protected String getJsonStringInput(String resource) throws IOException {
		InputStream resourceAsStream = getClass().getResourceAsStream(
				resource);
		
		StringBuilder out = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(resourceAsStream));
		for(String line = br.readLine(); line != null; line = br.readLine()) 
		    out.append(line);
		br.close();
		return out.toString();
		
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
					if(currentMethod.getName().startsWith("get") && !isTechnicalMethod(currentMethod.getName())){
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

	protected Annotation createTag(String requestBody) throws JsonParseException {
		String provider = WebAnnotationFields.PROVIDER_WEBANNO;
		String userToken = TEST_USER_TOKEN;
		return createTag(requestBody, provider, getApiKey(), userToken);
	}

	protected Annotation createTag(String requestBody, String provider, String apiKey, String userToken) throws JsonParseException {
		ResponseEntity<String> response = getApiClient().createTag(
				provider, null, false, requestBody, 
				apiKey, userToken);
		
		assertNotNull(response.getBody());
		assertEquals(response.getStatusCode(), HttpStatus.CREATED);
		
		Annotation storedAnno = getApiClient().parseResponseBody(response);
		assertNotNull(storedAnno.getCreator());
		assertNotNull(storedAnno.getGenerator());
		return storedAnno;
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
	
	/**
	 * @deprecated numericId is ambiguous, use relative_uri or full uri (annotationId) when deleting annotations 
	 * @param numericId
	 */
	protected void deleteAnnotation(Integer numericId) {
		WebAnnotationAdminApi webannoAdminApi = new WebAnnotationAdminApiImpl();
		ResponseEntity<String> re = webannoAdminApi.deleteAnnotation(numericId);
		assertEquals(HttpStatus.OK, re.getStatusCode());
		log.trace("Annotation deleted: " + numericId);
	}
	
	protected Integer getNumericAnnotationId(Annotation annotation) {
		String annotIdUriStr = annotation.getAnnotationId().toString();
		Integer numericId = Integer.parseInt(annotIdUriStr.substring(annotIdUriStr.lastIndexOf("/") + 1));
		return numericId;
	}
	
	protected Annotation[] createMultipleTestAnnotations(Integer numTestAnno) throws JsonParseException, IOException {
		
		Annotation[] testAnnotations = new Annotation[numTestAnno];
		for( int i = 0; i < numTestAnno; i++) {
			Annotation annotation = this.createTestAnnotation();
			assertNotNull(annotation);
			testAnnotations[i] = annotation;
		}
		return testAnnotations;
	}
	
	protected Integer[] getNumericAnnotationIds(Annotation[] annotations) {
		Integer[] annotationIds = new Integer[annotations.length];
		for(int i = 0; i < annotations.length; i++) {
			annotationIds[i] = getNumericAnnotationId(annotations[i]);
		}
		return annotationIds;
	}
	
	protected void deleteAnnotations(Integer[] numericIds) {
		WebAnnotationAdminApi webannoAdminApi = new WebAnnotationAdminApiImpl();
		for(int i = 0; i < numericIds.length; i++) {
			ResponseEntity<String> re = webannoAdminApi.deleteAnnotation(numericIds[i]);
			assertEquals(re.getStatusCode(), HttpStatus.OK);
		}
	}
}
