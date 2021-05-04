package eu.europeana.annotation.client.integration.webanno.search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.Test;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.body.TagBody;
import eu.europeana.annotation.definitions.model.body.impl.EdmPlaceBody;
import eu.europeana.annotation.definitions.model.body.impl.FullTextResourceBody;
import eu.europeana.annotation.definitions.model.body.impl.SemanticTagBody;
import eu.europeana.annotation.definitions.model.body.impl.TextBody;
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
		Annotation storedAnno = createTag(requestBody);
		
		assertTrue(BodyInternalTypes.isSemanticTagBody(storedAnno.getBody().getInternalType()));
		//validate the reflection of input in output!
		validateOutputAgainstInput(storedAnno, inputAnno);
		
		// search for indexed id and textual values
		searchByBodyValue(VALUE_ID+"\""+storedAnno.getAnnotationId().getIdentifier()+"\"", TOTAL_BY_ID_FOUND);
		validateSemanticTag(storedAnno);
		searchByBodyValue(VALUE_SEARCH_TAG_BODY_URI, TOTAL_BY_ID_FOUND);
		validateSemanticTag(storedAnno);
		searchByBodyValue(VALUE_SEARCH_TARGET, TOTAL_BY_ID_FOUND);
		validateSemanticTag(storedAnno);

		// remove tag
		deleteAnnotation(storedAnno);
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
		Target target = storedAnno.getTarget();
		assertNotNull(target.getHttpUri());
		assertTrue(target.getHttpUri().equals("http://data.europeana.eu/item/09102/_UEDIN_214"));
	}

	@Test
	public void searchGeoTag() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(TAG_GEOTAG);
		
		Annotation inputAnno = parseTag(requestBody);
		
		// create indexed tag
		Annotation storedAnno = createTag(requestBody);
		
		assertTrue(BodyInternalTypes.isGeoTagBody(storedAnno.getBody().getInternalType()));
		//validate the reflection of input in output!
		validateOutputAgainstInput(storedAnno, inputAnno);
		
		// search for indexed id and textual values
		searchByBodyValue(VALUE_ID+"\""+storedAnno.getAnnotationId().getIdentifier()+"\"", TOTAL_BY_ID_FOUND);
		validateGeoTag(storedAnno);
		searchByBodyValue(VALUE_SEARCH_TARGET, TOTAL_BY_ID_FOUND);
		validateGeoTag(storedAnno);

		// remove tag
		deleteAnnotation(storedAnno);
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
		Target target = storedAnno.getTarget();
		assertNotNull(target.getHttpUri());
		assertTrue(target.getHttpUri().equals("http://data.europeana.eu/item/09102/_UEDIN_214"));
	}

	@Test
	public void searchTag() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(TAG_BODY_TEXT);
		
		Annotation inputAnno = parseTag(requestBody);
		
		// create indexed tag
		Annotation storedAnno = createTag(requestBody);
		
		assertTrue(BodyInternalTypes.isTagBody(storedAnno.getBody().getInternalType()));
		//validate the reflection of input in output!
		validateOutputAgainstInput(storedAnno, inputAnno);
		
		// search for indexed id and textual values
		searchByBodyValue(VALUE_ID+"\""+storedAnno.getAnnotationId().getIdentifier()+"\"", TOTAL_BY_ID_FOUND);
		validateTag(storedAnno);
		searchByBodyValue(VALUE_SEARCH_TAG_BODY_VALUE, TOTAL_BY_ID_FOUND);
		validateTag(storedAnno);
		searchByBodyValue(VALUE_SEARCH_TARGET_TAG, TOTAL_BY_ID_FOUND);
		validateTag(storedAnno);

		// remove tag
		deleteAnnotation(storedAnno);
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
		Target target = storedAnno.getTarget();
		assertNotNull(target.getHttpUri());
		assertTrue(target.getHttpUri().equals("http://data.europeana.eu/item/000002/_UEDIN_214"));
	}
	
	@Test
	public void searchTagText() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(DESCRIBING_WEB_RESOURCE);
		
		// create indexed tag
		Annotation storedAnno = createTag(requestBody);
		
		assertTrue(BodyInternalTypes.isTextualBody(storedAnno.getBody().getInternalType()));
		
		// search for indexed id and textual values
		searchByBodyValue(VALUE_ID+"\""+storedAnno.getAnnotationId().getIdentifier()+"\"", TOTAL_BY_ID_FOUND);
		validateTagText(storedAnno);
		searchByBodyValue(VALUE_SEARCH_DESCRIBING_BODY_VALUE, TOTAL_BY_ID_FOUND);
		validateTagText(storedAnno);
		searchByBodyValue(VALUE_SEARCH_DESCRIBING_TARGET_SCOPE, TOTAL_BY_ID_FOUND);
		validateTagText(storedAnno);

		// remove tag
		deleteAnnotation(storedAnno);
	}

	/**
	 * Validate tag text fields after search.
	 * 
	 * @param storedAnno
	 */
	private void validateTagText(Annotation storedAnno) {
		assertTrue(storedAnno.getMotivation().equals(MotivationTypes.DESCRIBING.name().toLowerCase()));
		assertEquals(storedAnno.getBody().getInternalType(), BodyInternalTypes.TEXT.name());
		TextBody textBody = ((TextBody) storedAnno.getBody());
		assertNotNull(textBody.getValue());
		assertTrue(textBody.getValue().equals("... this is the textual description of the item ..."));
		Target target = storedAnno.getTarget();
		assertNotNull(target.getSource());
		assertTrue(target.getSource().equals("http://www.europeana1914-1918.eu/attachments/2020601/20841.235882.full.jpg"));
		assertNotNull(target.getScope());
		assertTrue(target.getScope().equals("http://data.europeana.eu/item/07931/diglit_uah_m1"));
	}
	
	@Test
	public void searchSemanticTagSpecific() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(SEMANTICTAG_SPECIFIC_STANDARD);
		
		Annotation inputAnno = parseTag(requestBody);
		
		// create indexed tag
		Annotation storedAnno = createTag(requestBody);
		
		assertTrue(BodyInternalTypes.isSemanticTagBody(storedAnno.getBody().getInternalType()));
		//validate the reflection of input in output!
		validateOutputAgainstInput(storedAnno, inputAnno);
		
		// search for indexed id and textual values
		searchByBodyValue(VALUE_ID+"\""+storedAnno.getAnnotationId().getIdentifier()+"\"", TOTAL_BY_ID_FOUND);
		validateSemanticTagSpecific(storedAnno);
		searchByBodyValue(VALUE_SEARCH_BODY_SPECIFIC_RESOURCE, TOTAL_BY_ID_FOUND);
		validateSemanticTagSpecific(storedAnno);
		searchByBodyValue(VALUE_SEARCH_TARGET, TOTAL_BY_ID_FOUND);
		validateSemanticTagSpecific(storedAnno);

		// remove tag
		deleteAnnotation(storedAnno);
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
		Target target = storedAnno.getTarget();
		assertNotNull(target.getHttpUri());
		assertTrue(target.getHttpUri().equals("http://data.europeana.eu/item/09102/_UEDIN_214"));
	}

	@Test
	public void searchFullTextResourceTag() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(TRANSCRIPTION_WITH_RIGHTS);
		
		Annotation inputAnno = parseTag(requestBody);
		
		// create indexed tag
		Annotation storedAnno = createTag(requestBody);
		
		assertTrue(BodyInternalTypes.isFullTextResourceTagBody(storedAnno.getBody().getInternalType()));
		//validate the reflection of input in output!
		validateOutputAgainstInput(storedAnno, inputAnno);
		
		// search for indexed id and textual values
		searchByBodyValue(VALUE_ID+"\""+storedAnno.getAnnotationId().getIdentifier()+"\"", TOTAL_BY_ID_FOUND);
		validateFullTextResourceTag(storedAnno);
		searchByBodyValue(VALUE_SEARCH_BODY_FULL_TEXT_RESOURCE, TOTAL_BY_ID_FOUND);
		validateFullTextResourceTag(storedAnno);
		searchByBodyValue(VALUE_SEARCH_TARGET, TOTAL_BY_ID_FOUND);
		validateFullTextResourceTag(storedAnno);

		// remove tag
		deleteAnnotation(storedAnno);
	}

	/**
	 * Validate full text resource tag fields after search.
	 * 
	 * @param storedAnno
	 */
	private void validateFullTextResourceTag(Annotation storedAnno) {
		assertTrue(storedAnno.getMotivation().equals(MotivationTypes.TRANSCRIBING.name().toLowerCase()));
		assertEquals(storedAnno.getBody().getInternalType(), BodyInternalTypes.FULL_TEXT_RESOURCE.name());
		FullTextResourceBody textBody = ((FullTextResourceBody) storedAnno.getBody());
		assertNotNull(textBody.getValue());
		assertTrue(textBody.getValue().equals("... complete transcribed text in HTML ..."));
		Target target = storedAnno.getTarget();
		assertNotNull(target.getSource());
		assertTrue(target.getSource().equals("http://www.europeana1914-1918.eu/attachments/2020601/20841.235882.full.jpg"));
		assertNotNull(target.getScope());
		assertTrue(target.getScope().equals("http://data.europeana.eu/item/07931/diglit_uah_m1"));
	}

}
