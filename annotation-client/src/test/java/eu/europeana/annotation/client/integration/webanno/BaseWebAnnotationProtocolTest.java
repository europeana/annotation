package eu.europeana.annotation.client.integration.webanno;

import static org.junit.Assert.assertEquals;

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

	String START = "{";
	String END = "}";
	String TYPE = "\"@context\": \"" + WebAnnotationModelKeywords.WA_CONTEXT + "\","
			+ "\"type\": \"oa:Annotation\",";

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

	public String TAG_JSON = START + TAG_CORE + "\"motivation\": \"oa:tagging\"," + END;

	public String LINK_JSON = START + LINK_CORE + "\"motivation\": \"oa:linking\"," + END;

	public String UPDATE_BODY = "\"bodyValue\": \"Buccin Trombone\"";

	public String UPDATE_TARGET = "\"target\": \"http://data.europeana.eu/item/09102/_UEDIN_214\"";

	public String UPDATE_JSON = START + UPDATE_BODY + "," + UPDATE_TARGET + END;

	// public String TEST_WSKEY = "apidemo";

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
	 */
	protected ResponseEntity<String> storeTestAnnotation() {

		/**
		 * store annotation
		 */
		ResponseEntity<String> storedResponse = getApiClient().createAnnotation(getApiKey(),
				WebAnnotationFields.PROVIDER_WEBANNO, null, TAG_JSON, TEST_USER_TOKEN, null);
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
	 */
	protected Annotation createTestAnnotation() throws JsonParseException {

		/**
		 * store annotation
		 */
		ResponseEntity<String> response = storeTestAnnotation();
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
					if(currentMethod.getName().startsWith("get")){
						inputProp = currentMethod.invoke(inputAnno, (Object[]) null);
						
						//compare non null fields only
						if(inputProp != null){
							storedProp = currentMethod.invoke(storedAnno, (Object[])null);
							assertEquals(inputProp, storedProp);
						}			
					}			
				}
				
			}
}
