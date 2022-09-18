package eu.europeana.annotation.tests.webanno.tag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.BodyInternalTypes;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.tests.utils.AnnotationTestUtils;

@SpringBootTest
@AutoConfigureMockMvc
public class TaggingTest extends BaseTaggingTest {

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
	
}
