package eu.europeana.annotation.client.integration.webanno.tag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.codehaus.jettison.json.JSONException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.BodyInternalTypes;

public class TaggingTest extends BaseTaggingTest {

	@Test
	public void createTagBodyText() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(TAG_BODY_TEXT);
		
		Annotation storedAnno = createTag(requestBody);
		addCreatedAnnotation(storedAnno);
		
		Annotation inputAnno = parseTag(requestBody);
		
		//validate the reflection of input in output!
		validateOutputAgainstInput(storedAnno, inputAnno);
	}
	
	
	@Test
	public void createTagMinimal() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(TAG_MINIMAL);
		
		Annotation storedAnno = createTag(requestBody);
		addCreatedAnnotation(storedAnno);
		
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
		removeAnnotation(storedAnno.getIdentifier());

	}
	
	@Test
	public void createGeoTag() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(TAG_GEOTAG);
		
		Annotation inputAnno = parseTag(requestBody);
		
		Annotation storedAnno = createTag(requestBody);
		
		assertTrue(BodyInternalTypes.isGeoTagBody(storedAnno.getBody().getInternalType()));
		//validate the reflection of input in output!
		validateOutputAgainstInput(storedAnno, inputAnno);
		removeAnnotation(storedAnno.getIdentifier());
	}
	
	@Test
	public void createWrongTag() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(TAG_MINIMAL_WRONG);
		
		ResponseEntity<String> response = getApiProtocolClient().createTag(
				true, requestBody);
		
		assertEquals(response.getStatusCode().value(), HttpStatus.BAD_REQUEST.value());
		//log.debug("Error message: " + );
	}
	
	@Test
    public void createTagWithoutBody() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        
        String requestBody = getJsonStringInput(TAG_WITHOUT_BODY);
        
        ResponseEntity<String> response = getApiProtocolClient().createTag(
                true, requestBody);
        
        assertEquals(response.getStatusCode().value(), HttpStatus.BAD_REQUEST.value());
        //log.debug("Error message: " + );
    }
	
	
	@Test
	public void createWrongGeoTagLat() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(TAG_GEO_WRONG_LAT);
		
		ResponseEntity<String> response = getApiProtocolClient().createTag(
				false, requestBody);
		
		assertEquals(response.getStatusCode().value(), HttpStatus.BAD_REQUEST.value());
	}
	
	@Test
	public void createWrongGeoTagLong() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(TAG_GEO_WRONG_LONG);
		
		ResponseEntity<String> response = getApiProtocolClient().createTag(
				false, requestBody);
		
		assertEquals(response.getStatusCode().value(), HttpStatus.BAD_REQUEST.value());
	}

	@Test
	public void createCanonicalTag() throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, JsonParseException {
			
	  Annotation storedAnno = createAndValidateTag(TAG_CANONICAL);
	  removeAnnotation(storedAnno.getIdentifier());
	}
	
	@Test
	public void createViaTagString() throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, JsonParseException, JSONException {
		
	    Annotation storedAnno = createAndValidateTag(TAG_VIA_STRING);
		removeAnnotation(storedAnno.getIdentifier());
	}
	
	@Test
	public void createViaTagArray() throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, JsonParseException, JSONException {

	    Annotation storedAnno = createAndValidateTag(TAG_VIA_ARRAY);
	    removeAnnotation(storedAnno.getIdentifier());
	}

	
}
