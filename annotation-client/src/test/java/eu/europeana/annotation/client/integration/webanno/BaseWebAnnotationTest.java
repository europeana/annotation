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
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.tests.constants.AnnotationTestsConstants;
import eu.europeana.annotation.utils.parse.AnnotationLdParser;

public class BaseWebAnnotationTest extends AnnotationTestsConstants{
    
	protected Logger log = LogManager.getLogger(getClass());
	
	private static WebAnnotationProtocolApi apiProtocolClient;
	private static WebAnnotationAuxilaryMethodsApi apiAuxilaryMethodsClient;
	private static WebAnnotationAdminApi webannoAdminApi;
	protected static List<Long> createdAnnotations = new ArrayList<Long>();

	@BeforeAll
	public static void initObjects() throws Exception {
		apiProtocolClient = new WebAnnotationProtocolApiImpl();
		apiAuxilaryMethodsClient =  new WebAnnotationAuxilaryMethodsApiImpl();
	}
	
	@AfterEach
	public void removeCreatedAnnotations() throws Exception {
	  if(webannoAdminApi==null) {
	    webannoAdminApi = new WebAnnotationAdminApiImpl();
	  }
	  for(Long identifier : createdAnnotations) {
	    webannoAdminApi.removeAnnotation(identifier);
	  }
	  createdAnnotations.clear();
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
		createdAnnotations.add(annotation.getIdentifier());
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
	 * @throws Exception 
	 */
	protected void deleteAnnotation(Annotation annotation) throws Exception {
		deleteAnnotation(annotation.getIdentifier());
	}
	
    protected void deleteAnnotation(long identifier) throws Exception {
		WebAnnotationAdminApi webannoAdminApi = new WebAnnotationAdminApiImpl();
		ResponseEntity<String> re = webannoAdminApi.deleteAnnotation(identifier);
		assertEquals(HttpStatus.NO_CONTENT, re.getStatusCode());
		log.trace("Annotation deleted: /" + identifier);
	}

	protected Annotation[] createMultipleTestAnnotations(Integer numTestAnno) throws JsonParseException, IOException {
		
		Annotation[] testAnnotations = new Annotation[numTestAnno];
		for( int i = 0; i < numTestAnno; i++) {
			Annotation annotation = this.createTestAnnotation(TAG_STANDARD, null);
			assertNotNull(annotation);
			testAnnotations[i] = annotation;
		}
		return testAnnotations;
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
	
	protected void validateResponse(ResponseEntity<String> response) throws JsonParseException {
		validateResponse(response, HttpStatus.CREATED);
	}
	
	protected void validateResponse(ResponseEntity<String> response, HttpStatus status) throws JsonParseException {
		assertNotNull(response.getBody());
		assertEquals(response.getStatusCode(), status);
		
		Annotation storedAnno = getApiProtocolClient().parseResponseBody(response);
		assertNotNull(storedAnno.getIdentifier());
	}
	
}
