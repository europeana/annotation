package eu.europeana.annotation.client.integration.webanno.search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.Test;

import eu.europeana.annotation.client.config.ClientConfiguration;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.body.TagBody;
import eu.europeana.annotation.definitions.model.body.impl.EdmPlaceBody;
import eu.europeana.annotation.definitions.model.body.impl.SemanticTagBody;
import eu.europeana.annotation.definitions.model.entity.impl.EdmPlace;
import eu.europeana.annotation.definitions.model.target.Target;
import eu.europeana.annotation.definitions.model.vocabulary.BodyInternalTypes;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

/**
 * This class implements tests for searching annotation with motivation tagging
 * 
 * @author GrafR
 *
 */
public class SearchTagsTest extends BaseSearchTest {
		
	@Test
	public void searchSemanticTag() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(SEMANTICTAG_SIMPLE_STANDARD);
		
		Annotation inputAnno = parseTag(requestBody);
		
		// create indexed tag
		Annotation createdAnno = createTag(requestBody);
		addCreatedAnnotation(createdAnno);
		
		assertTrue(BodyInternalTypes.isSemanticTagBody(createdAnno.getBody().getInternalType()));
		//validate the reflection of input in output!
		validateOutputAgainstInput(createdAnno, inputAnno);
		
		// search for indexed id and textual values
		Annotation retrievedAnno = searchLastCreated(VALUE_ID+"\""+createdAnno.getIdentifier()+"\"");
		validateSemanticTag(retrievedAnno);
		retrievedAnno = searchLastCreated(VALUE_SEARCH_TAG_BODY_URI);
		validateSemanticTag(retrievedAnno);
		String VALUE_TARGET_URI = ClientConfiguration.getInstance().getPropAnnotationItemDataEndpoint() + "/09102/_UEDIN_214";
	    String VALUE_SEARCH_TARGET = "target_uri:\""+ VALUE_TARGET_URI +"\"";
		retrievedAnno = searchLastCreated(VALUE_SEARCH_TARGET);
		validateSemanticTag(retrievedAnno);
	}

	/**
	 * Validate semantic tag fields after search.
	 * 
	 * @param storedAnno
	 */
	private void validateSemanticTag(Annotation storedAnno) {
		assertTrue(storedAnno.getMotivation().equals(MotivationTypes.TAGGING.name().toLowerCase()));
		assertEquals(storedAnno.getBody().getInternalType(), BodyInternalTypes.SEMANTIC_TAG.name());
		TagBody tagBody = ((SemanticTagBody) storedAnno.getBody());
		assertNotNull(tagBody.getHttpUri());
		assertTrue(tagBody.getHttpUri().equals("http://www.geonames.org/2988507"));
		assertNotNull(tagBody.getLanguage());
		assertTrue(tagBody.getLanguage().equals("en"));
		Target target = storedAnno.getTarget().get(0);
		assertNotNull(target.getHttpUri());
		assertTrue(target.getHttpUri().equals(ClientConfiguration.getInstance().getPropAnnotationItemDataEndpoint() + "/09102/_UEDIN_214"));
	}

	@Test
	public void searchGeoTag() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(TAG_GEOTAG);
		
		Annotation inputAnno = parseTag(requestBody);
		
		// create indexed tag
		Annotation createdAnno = createTag(requestBody);
		addCreatedAnnotation(createdAnno);
		
		assertTrue(BodyInternalTypes.isGeoTagBody(createdAnno.getBody().getInternalType()));
		//validate the reflection of input in output!
		validateOutputAgainstInput(createdAnno, inputAnno);
		
		// search for indexed id and textual values
		Annotation retrievedAnno = searchLastCreated(VALUE_ID+"\""+createdAnno.getIdentifier()+"\"");
		validateGeoTag(retrievedAnno);
		String VALUE_TARGET_URI = ClientConfiguration.getInstance().getPropAnnotationItemDataEndpoint() + "/09102/_UEDIN_214";
	    String VALUE_SEARCH_TARGET = "target_uri:\""+ VALUE_TARGET_URI +"\"";
		retrievedAnno = searchLastCreated(VALUE_SEARCH_TARGET);
		validateGeoTag(retrievedAnno);
	}

	/**
	 * Validate geo tag fields after search.
	 * 
	 * @param storedAnno
	 */
	private void validateGeoTag(Annotation storedAnno) {
		assertTrue(storedAnno.getMotivation().equals(MotivationTypes.TAGGING.name().toLowerCase()));
		assertEquals(storedAnno.getBody().getInternalType(), BodyInternalTypes.GEO_TAG.name());
		EdmPlace placeBody = (EdmPlace) ((EdmPlaceBody) storedAnno.getBody()).getPlace();
		assertNotNull(placeBody.getLatitude());
		assertTrue(placeBody.getLatitude().equals("48.853415"));
		assertNotNull(placeBody.getLongitude());
		assertTrue(placeBody.getLongitude().equals("-102.348800"));
		Target target = storedAnno.getTarget().get(0);
		assertNotNull(target.getHttpUri());
		assertTrue(target.getHttpUri().equals(ClientConfiguration.getInstance().getPropAnnotationItemDataEndpoint() + "/09102/_UEDIN_214"));
	}

	@Test
	public void searchTag() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(TAG_BODY_TEXT);
		
		Annotation inputAnno = parseTag(requestBody);
		
		// create indexed tag
		Annotation createdAnno = createTag(requestBody);
		
		assertTrue(BodyInternalTypes.isTagBody(createdAnno.getBody().getInternalType()));
		//validate the reflection of input in output!
		validateOutputAgainstInput(createdAnno, inputAnno);
		
		// search for indexed id and textual values
		Annotation retrievedAnno = searchLastCreated(VALUE_ID+"\""+createdAnno.getIdentifier()+"\"");
		validateTag(retrievedAnno);
		retrievedAnno = searchLastCreated(VALUE_SEARCH_TAG_BODY_VALUE);
		validateTag(retrievedAnno);
		String VALUE_TARGET_TAG_URI = ClientConfiguration.getInstance().getPropAnnotationItemDataEndpoint() + "/000002/_UEDIN_214";
		String VALUE_SEARCH_TARGET_TAG = "target_uri:\""+ VALUE_TARGET_TAG_URI +"\"";
		retrievedAnno = searchLastCreated(VALUE_SEARCH_TARGET_TAG);
		validateTag(retrievedAnno);

		// remove tag
		removeAnnotation(createdAnno.getIdentifier());
	}

	/**
	 * Validate tag fields after search.
	 * 
	 * @param storedAnno
	 */
	private void validateTag(Annotation storedAnno) {
		assertTrue(storedAnno.getMotivation().equals(MotivationTypes.TAGGING.name().toLowerCase()));
		assertEquals(storedAnno.getBody().getInternalType(), BodyInternalTypes.TAG.name());
		TagBody tagBody = ((TagBody) storedAnno.getBody());
		assertNotNull(tagBody.getValue());
		assertTrue(tagBody.getValue().equals("trombone"));
		Target target = storedAnno.getTarget().get(0);
		assertNotNull(target.getHttpUri());
		assertTrue(target.getHttpUri().equals(ClientConfiguration.getInstance().getPropAnnotationItemDataEndpoint() + "/000002/_UEDIN_214"));
	}
	
	
	@Test
	public void searchSemanticTagSpecific() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(SEMANTICTAG_SPECIFIC_STANDARD);
		
		Annotation inputAnno = parseTag(requestBody);
		
		// create indexed tag
		Annotation createdAnno = createTag(requestBody);
		
		assertTrue(BodyInternalTypes.isSemanticTagBody(createdAnno.getBody().getInternalType()));
		//validate the reflection of input in output!
		validateOutputAgainstInput(createdAnno, inputAnno);
		
		// search for indexed id and textual values
		Annotation retrievedAnno = searchLastCreated(VALUE_ID+"\""+createdAnno.getIdentifier()+"\"");
		validateSemanticTagSpecific(retrievedAnno);
		retrievedAnno = searchLastCreated(VALUE_SEARCH_BODY_SPECIFIC_RESOURCE);
		validateSemanticTagSpecific(retrievedAnno);
	    String VALUE_TARGET_URI = ClientConfiguration.getInstance().getPropAnnotationItemDataEndpoint() + "/09102/_UEDIN_214";
	    String VALUE_SEARCH_TARGET = "target_uri:\""+ VALUE_TARGET_URI +"\"";
		retrievedAnno = searchLastCreated(VALUE_SEARCH_TARGET);
		validateSemanticTagSpecific(retrievedAnno);

		// remove tag
		removeAnnotation(createdAnno.getIdentifier());
	}

	/**
	 * Validate semantic tag specific fields after search.
	 * 
	 * @param storedAnno
	 */
	private void validateSemanticTagSpecific(Annotation storedAnno) {
		assertTrue(storedAnno.getMotivation().equals(MotivationTypes.TAGGING.name().toLowerCase()));
		assertEquals(storedAnno.getBody().getInternalType(), BodyInternalTypes.SEMANTIC_TAG.name());
		TagBody tagBody = ((SemanticTagBody) storedAnno.getBody());
		assertNotNull(tagBody.getHttpUri());
		assertTrue(tagBody.getHttpUri().equals("http://sws.geonames.org/2988506"));
		assertNotNull(tagBody.getLanguage());
		assertTrue(tagBody.getLanguage().equals("en"));
		Target target = storedAnno.getTarget().get(0);
		assertNotNull(target.getHttpUri());
		assertTrue(target.getHttpUri().equals(ClientConfiguration.getInstance().getPropAnnotationItemDataEndpoint() + "/09102/_UEDIN_214"));
	}

}
