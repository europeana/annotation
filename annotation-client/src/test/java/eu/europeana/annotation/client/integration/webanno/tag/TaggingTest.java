package eu.europeana.annotation.client.integration.webanno.tag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.BodyInternalTypes;
import eu.europeana.annotation.definitions.model.vocabulary.ResourceTypes;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;

public class TaggingTest extends BaseTaggingTest {

	@Test
	public void createTagBodyText() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(TAG_BODY_TEXT);
		
		Annotation storedAnno = createTag(requestBody);
		
		Annotation inputAnno = parseTag(requestBody);
		
		//validate the reflection of input in output!
		validateOutputAgainstInput(storedAnno, inputAnno);
	}
	
	
	@Test
	public void createTagMinimal() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(TAG_MINIMAL);
		
		Annotation storedAnno = createTag(requestBody);
		
		Annotation inputAnno = parseTag(requestBody);
		
		//validate the reflection of input in output!
		validateOutputAgainstInput(storedAnno, inputAnno);
	}
	

	@Test
	public void createTagStandard() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(TAG_STANDARD);
		
		Annotation storedAnno = createTag(requestBody);
		
		Annotation inputAnno = parseTag(requestBody);
		//validate the reflection of input in output!
		//but ignore generated timestamp which is always set by the server
		inputAnno.setGenerated(storedAnno.getGenerated());
		validateOutputAgainstInput(storedAnno, inputAnno);

	}
	
	
	@Test
	public void createGeoTag() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(TAG_GEOTAG);
		
		Annotation inputAnno = parseTag(requestBody);
		
		Annotation storedAnno = createTag(requestBody);
		
		assertTrue(BodyInternalTypes.isGeoTagBody(storedAnno.getBody().getInternalType()));
		//validate the reflection of input in output!
		validateOutputAgainstInput(storedAnno, inputAnno);
		
	}
	
	@Test
	public void createWrongTag() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(TAG_MINIMAL_WRONG);
		
		ResponseEntity<String> response = getApiClient().createTag(
				WebAnnotationFields.PROVIDER_WEBANNO, null, false, requestBody, 
				TEST_USER_TOKEN);
		
		assertEquals(response.getStatusCode().value(), HttpStatus.BAD_REQUEST.value());
		//log.debug("Error message: " + );
	}
	
	@Test
	public void createWrongGeoTagLat() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(TAG_GEO_WRONG_LAT);
		
		ResponseEntity<String> response = getApiClient().createTag(
				WebAnnotationFields.PROVIDER_WEBANNO, null, false, requestBody, 
				TEST_USER_TOKEN);
		
		assertEquals(response.getStatusCode().value(), HttpStatus.BAD_REQUEST.value());
	}
	
	@Test
	public void createWrongGeoTagLong() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(TAG_GEO_WRONG_LONG);
		
		ResponseEntity<String> response = getApiClient().createTag(
				WebAnnotationFields.PROVIDER_WEBANNO, null, false, requestBody, 
				TEST_USER_TOKEN);
		
		assertEquals(response.getStatusCode().value(), HttpStatus.BAD_REQUEST.value());
	}

	@Test
	public void createCanonicalTag() throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, JsonParseException {
			
		createAndValidateTag(TAG_CANONICAL);
	}
	
	@Test
	public void createViaTagString() throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, JsonParseException, JSONException {
		
		String requestBody = getJsonStringInput(TAG_VIA_STRING);

		ResponseEntity<String> response = getApiClient().createTag(WebAnnotationFields.PROVIDER_WEBANNO, null, false,
				requestBody, TEST_USER_TOKEN);

		assertEquals(HttpStatus.CREATED.value(), response.getStatusCode().value());
		
		JSONObject jRequestBody = new JSONObject(requestBody);
		String requestVia = jRequestBody.getString("via");
		String[] storedVia = getApiClient().parseResponseBody(response).getVia();
		
		assertEquals(requestVia, storedVia[0]);
	}
	
	@Test
	public void createViaTagArray() throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, JsonParseException, JSONException {
		
		String requestBody = getJsonStringInput(TAG_VIA_ARRAY);

		ResponseEntity<String> response = getApiClient().createTag(WebAnnotationFields.PROVIDER_WEBANNO, null, false,
				requestBody, TEST_USER_TOKEN);

		assertEquals(HttpStatus.CREATED.value(), response.getStatusCode().value());
		
		JSONObject jRequestBody = new JSONObject(requestBody);
		JSONArray jViaArray = (JSONArray) jRequestBody.get("via");
		
		List<String> list = new ArrayList<String>();
		for(int i = 0; i < jViaArray.length(); i++){
		    list.add(jViaArray.getString(i));
		}
		String[] requestVia = list.toArray(new String[0]);
		String[] storedVia = getApiClient().parseResponseBody(response).getVia();
		
		assertEquals(requestVia, storedVia);
	}

	
}
