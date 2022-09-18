package eu.europeana.annotation.tests.webanno.search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.body.TagBody;
import eu.europeana.annotation.definitions.model.body.impl.EdmPlaceBody;
import eu.europeana.annotation.definitions.model.body.impl.SemanticTagBody;
import eu.europeana.annotation.definitions.model.entity.impl.EdmPlace;
import eu.europeana.annotation.definitions.model.target.Target;
import eu.europeana.annotation.definitions.model.vocabulary.BodyInternalTypes;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.tests.config.AnnotationTestsConfiguration;
import eu.europeana.annotation.tests.utils.AnnotationTestUtils;

/**
 * This class implements tests for searching annotation with motivation tagging
 * 
 * @author GrafR
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
public class SearchTagsTest extends BaseSearchTest {
		
	@Test
	public void searchSemanticTag() throws Exception {
		
		String requestBody = AnnotationTestUtils.getJsonStringInput(SEMANTICTAG_SIMPLE_STANDARD);
		
		Annotation inputAnno = parseTag(requestBody);
		
		// create indexed tag
		Annotation createdAnno = createTag(requestBody);
		createdAnnotations.add(createdAnno.getIdentifier());
		
		assertTrue(BodyInternalTypes.isSemanticTagBody(createdAnno.getBody().getInternalType()));
		//validate the reflection of input in output!
		AnnotationTestUtils.validateOutputAgainstInput(createdAnno, inputAnno);
		
		// search for indexed id and textual values
		Annotation retrievedAnno = searchLastCreated(VALUE_ID+"\""+createdAnno.getIdentifier()+"\"");
		validateSemanticTag(retrievedAnno);
		retrievedAnno = searchLastCreated(VALUE_SEARCH_TAG_BODY_URI);
		validateSemanticTag(retrievedAnno);
		String VALUE_TARGET_URI = AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint() + "/09102/_UEDIN_214";
	    String VALUE_SEARCH_TARGET = "target_uri:\""+ VALUE_TARGET_URI +"\"";
		retrievedAnno = searchLastCreated(VALUE_SEARCH_TARGET);
		validateSemanticTag(retrievedAnno);
	}

	/**
	 * Validate semantic tag fields after search.
	 * 
	 * @param storedAnno
	 * @throws IOException 
	 */
	private void validateSemanticTag(Annotation storedAnno) throws IOException {
		assertTrue(storedAnno.getMotivation().equals(MotivationTypes.TAGGING.name().toLowerCase()));
		assertEquals(storedAnno.getBody().getInternalType(), BodyInternalTypes.SEMANTIC_TAG.name());
		TagBody tagBody = ((SemanticTagBody) storedAnno.getBody());
		assertNotNull(tagBody.getHttpUri());
		assertTrue(tagBody.getHttpUri().equals("http://www.geonames.org/2988507"));
		assertNotNull(tagBody.getLanguage());
		assertTrue(tagBody.getLanguage().equals("en"));
		Target target = storedAnno.getTarget();
		assertNotNull(target.getHttpUri());
		assertTrue(target.getHttpUri().equals(AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint() + "/09102/_UEDIN_214"));
	}

	@Test
	public void searchGeoTag() throws Exception {
		
		String requestBody = AnnotationTestUtils.getJsonStringInput(TAG_GEOTAG);
		
		Annotation inputAnno = parseTag(requestBody);
		
		// create indexed tag
		Annotation createdAnno = createTag(requestBody);
		createdAnnotations.add(createdAnno.getIdentifier());
		
		assertTrue(BodyInternalTypes.isGeoTagBody(createdAnno.getBody().getInternalType()));
		//validate the reflection of input in output!
		AnnotationTestUtils.validateOutputAgainstInput(createdAnno, inputAnno);
		
		// search for indexed id and textual values
		Annotation retrievedAnno = searchLastCreated(VALUE_ID+"\""+createdAnno.getIdentifier()+"\"");
		validateGeoTag(retrievedAnno);
		String VALUE_TARGET_URI = AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint() + "/09102/_UEDIN_214";
	    String VALUE_SEARCH_TARGET = "target_uri:\""+ VALUE_TARGET_URI +"\"";
		retrievedAnno = searchLastCreated(VALUE_SEARCH_TARGET);
		validateGeoTag(retrievedAnno);
	}

	/**
	 * Validate geo tag fields after search.
	 * 
	 * @param storedAnno
	 * @throws IOException 
	 */
	private void validateGeoTag(Annotation storedAnno) throws IOException {
		assertTrue(storedAnno.getMotivation().equals(MotivationTypes.TAGGING.name().toLowerCase()));
		assertEquals(storedAnno.getBody().getInternalType(), BodyInternalTypes.GEO_TAG.name());
		EdmPlace placeBody = (EdmPlace) ((EdmPlaceBody) storedAnno.getBody()).getPlace();
		assertNotNull(placeBody.getLatitude());
		assertTrue(placeBody.getLatitude().equals("48.853415"));
		assertNotNull(placeBody.getLongitude());
		assertTrue(placeBody.getLongitude().equals("-102.348800"));
		Target target = storedAnno.getTarget();
		assertNotNull(target.getHttpUri());
		assertTrue(target.getHttpUri().equals(AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint() + "/09102/_UEDIN_214"));
	}

	@Test
	public void searchTag() throws Exception {
		
		String requestBody = AnnotationTestUtils.getJsonStringInput(TAG_BODY_TEXT);
		
		Annotation inputAnno = parseTag(requestBody);
		
		// create indexed tag
		Annotation createdAnno = createTag(requestBody);
		createdAnnotations.add(createdAnno.getIdentifier());
		
		assertTrue(BodyInternalTypes.isTagBody(createdAnno.getBody().getInternalType()));
		//validate the reflection of input in output!
		AnnotationTestUtils.validateOutputAgainstInput(createdAnno, inputAnno);
		
		// search for indexed id and textual values
		Annotation retrievedAnno = searchLastCreated(VALUE_ID+"\""+createdAnno.getIdentifier()+"\"");
		validateTag(retrievedAnno);
		retrievedAnno = searchLastCreated(VALUE_SEARCH_TAG_BODY_VALUE);
		validateTag(retrievedAnno);
		String VALUE_TARGET_TAG_URI = AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint() + "/000002/_UEDIN_214";
		String VALUE_SEARCH_TARGET_TAG = "target_uri:\""+ VALUE_TARGET_TAG_URI +"\"";
		retrievedAnno = searchLastCreated(VALUE_SEARCH_TARGET_TAG);
		validateTag(retrievedAnno);
	}

	/**
	 * Validate tag fields after search.
	 * 
	 * @param storedAnno
	 * @throws IOException 
	 */
	private void validateTag(Annotation storedAnno) throws IOException {
		assertTrue(storedAnno.getMotivation().equals(MotivationTypes.TAGGING.name().toLowerCase()));
		assertEquals(storedAnno.getBody().getInternalType(), BodyInternalTypes.TAG.name());
		TagBody tagBody = ((TagBody) storedAnno.getBody());
		assertNotNull(tagBody.getValue());
		assertTrue(tagBody.getValue().equals("trombone"));
		Target target = storedAnno.getTarget();
		assertNotNull(target.getHttpUri());
		assertTrue(target.getHttpUri().equals(AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint() + "/000002/_UEDIN_214"));
	}
	
	
	@Test
	public void searchSemanticTagSpecific() throws Exception {
		
		String requestBody = AnnotationTestUtils.getJsonStringInput(SEMANTICTAG_SPECIFIC_STANDARD);
		
		Annotation inputAnno = parseTag(requestBody);
		
		// create indexed tag
		Annotation createdAnno = createTag(requestBody);
		createdAnnotations.add(createdAnno.getIdentifier());
		
		assertTrue(BodyInternalTypes.isSemanticTagBody(createdAnno.getBody().getInternalType()));
		//validate the reflection of input in output!
		AnnotationTestUtils.validateOutputAgainstInput(createdAnno, inputAnno);
		
		// search for indexed id and textual values
		Annotation retrievedAnno = searchLastCreated(VALUE_ID+"\""+createdAnno.getIdentifier()+"\"");
		validateSemanticTagSpecific(retrievedAnno);
		retrievedAnno = searchLastCreated(VALUE_SEARCH_BODY_SPECIFIC_RESOURCE);
		validateSemanticTagSpecific(retrievedAnno);
	    String VALUE_TARGET_URI = AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint() + "/09102/_UEDIN_214";
	    String VALUE_SEARCH_TARGET = "target_uri:\""+ VALUE_TARGET_URI +"\"";
		retrievedAnno = searchLastCreated(VALUE_SEARCH_TARGET);
		validateSemanticTagSpecific(retrievedAnno);
	}

	/**
	 * Validate semantic tag specific fields after search.
	 * 
	 * @param storedAnno
	 * @throws IOException 
	 */
	private void validateSemanticTagSpecific(Annotation storedAnno) throws IOException {
		assertTrue(storedAnno.getMotivation().equals(MotivationTypes.TAGGING.name().toLowerCase()));
		assertEquals(storedAnno.getBody().getInternalType(), BodyInternalTypes.SEMANTIC_TAG.name());
		TagBody tagBody = ((SemanticTagBody) storedAnno.getBody());
		assertNotNull(tagBody.getHttpUri());
		assertTrue(tagBody.getHttpUri().equals("http://sws.geonames.org/2988506"));
		assertNotNull(tagBody.getLanguage());
		assertTrue(tagBody.getLanguage().equals("en"));
		Target target = storedAnno.getTarget();
		assertNotNull(target.getHttpUri());
		assertTrue(target.getHttpUri().equals(AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint() + "/09102/_UEDIN_214"));
	}

}
