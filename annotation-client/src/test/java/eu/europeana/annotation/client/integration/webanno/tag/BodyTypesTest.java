package eu.europeana.annotation.client.integration.webanno.tag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import eu.europeana.annotation.client.AnnotationSearchApiImpl;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.body.GraphBody;
import eu.europeana.annotation.definitions.model.body.TagBody;
import eu.europeana.annotation.definitions.model.body.impl.EdmPlaceBody;
import eu.europeana.annotation.definitions.model.body.impl.FullTextResourceBody;
import eu.europeana.annotation.definitions.model.body.impl.SemanticTagBody;
import eu.europeana.annotation.definitions.model.body.impl.TextBody;
import eu.europeana.annotation.definitions.model.entity.impl.EdmPlace;
import eu.europeana.annotation.definitions.model.graph.Graph;
import eu.europeana.annotation.definitions.model.search.SearchProfiles;
import eu.europeana.annotation.definitions.model.search.result.AnnotationPage;
import eu.europeana.annotation.definitions.model.target.Target;
import eu.europeana.annotation.definitions.model.vocabulary.BodyInternalTypes;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

public class BodyTypesTest extends BaseTaggingTest {

	static final String VALUE_TESTSET = "generator_uri: \"http://test.europeana.org/45e86248-1218-41fc-9643-689d30dbe651\"";
	static final String VALUE_ID = "anno_id:";
	static final String VALUE_TARGET = "target_uri:\"http://data.europeana.eu/item/09102/_UEDIN_214\"";
	static final String VALUE_TARGET_TAG = "target_uri:\"http://data.europeana.eu/item/000002/_UEDIN_214\"";
	static final String VALUE_TARGET_LINK = "target_uri:\"http://data.europeana.eu/item/2020601/https___1914_1918_europeana_eu_contributions_19584\"";
	static final String VALUE_TARGET_LINK_SEMANTIC = "target_uri:\"http://data.europeana.eu/item/2048410/item_I5DUPVW2Q5HT2OQFSVXV7VYODA5P32P6\"";
	static final String VALUE_DESCRIBING_TARGET_SCOPE = "target_uri:\"http://data.europeana.eu/item/07931/diglit_uah_m1\"";
	static final String VALUE_BODY_URI = "body_uri:\"http://www.geonames.org/2988507\"";
	static final String VALUE_DESCRIBING_BODY_VALUE = "body_value:\"... this is the textual description of the item ...\"";
	static final String VALUE_BODY_VALUE = "body_value:\"trombone\"";
	static final String VALUE_BODY_LINK_RELATION = "link_relation:\"isSimilarTo\"";
	static final String VALUE_BODY_LINK_RESOURCE_URI = "link_resource_uri:\"https://www.wikidata.org/wiki/Q762\"";
	static final String VALUE_BODY_SPECIFIC_RESOURCE = "body_uri:\"http://www.geonames.org/2988507\""; // source
	static final String VALUE_BODY_FULL_TEXT_RESOURCE = "body_value:\"... complete transcribed text in HTML ...\"";

	static final int TOTAL_BY_ID_FOUND = 1;
	
	protected Logger log = LogManager.getLogger(getClass());
	
	private AnnotationSearchApiImpl annotationSearchApi;
	
	
	/**
	 * Create annotations data set before each test execution
	 * 
	 * @throws IOException
	 */
	@BeforeEach
	public void createAnnotationSearchApi() throws JsonParseException, IOException {
		annotationSearchApi = new AnnotationSearchApiImpl();
	}
	
	@Test
	public void createSemanticTag() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(SEMANTICTAG_SIMPLE_STANDARD);
		
		Annotation inputAnno = parseTag(requestBody);
		
		// create indexed tag
		Annotation storedAnno = createTag(requestBody);
		
		assertTrue(BodyInternalTypes.isSemanticTagBody(storedAnno.getBody().getInternalType()));
		//validate the reflection of input in output!
		validateOutputAgainstInput(storedAnno, inputAnno);
		
		// search for indexed id and textual values
		searchByBodyValue(VALUE_ID+"\""+storedAnno.getAnnotationId().getIdentifier()+"\"", TOTAL_BY_ID_FOUND);
		searchByBodyValue(VALUE_BODY_URI, TOTAL_BY_ID_FOUND);
		searchByBodyValue(VALUE_TARGET, TOTAL_BY_ID_FOUND);
		
		// validate fields
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

		// remove tag
		deleteAnnotation(storedAnno);
	}

	@Test
	public void createGeoTag() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(TAG_GEOTAG);
		
		Annotation inputAnno = parseTag(requestBody);
		
		// create indexed tag
		Annotation storedAnno = createTag(requestBody);
		
		assertTrue(BodyInternalTypes.isGeoTagBody(storedAnno.getBody().getInternalType()));
		//validate the reflection of input in output!
		validateOutputAgainstInput(storedAnno, inputAnno);
		
		// search for indexed id and textual values
		searchByBodyValue(VALUE_ID+"\""+storedAnno.getAnnotationId().getIdentifier()+"\"", TOTAL_BY_ID_FOUND);
		searchByBodyValue(VALUE_TARGET, TOTAL_BY_ID_FOUND);
		
		// validate fields
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

		// remove tag
		deleteAnnotation(storedAnno);
	}

	@Test
	public void createTag() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(TAG_BODY_TEXT);
		
		Annotation inputAnno = parseTag(requestBody);
		
		// create indexed tag
		Annotation storedAnno = createTag(requestBody);
		
		assertTrue(BodyInternalTypes.isTagBody(storedAnno.getBody().getInternalType()));
		//validate the reflection of input in output!
		validateOutputAgainstInput(storedAnno, inputAnno);
		
		// search for indexed id and textual values
		searchByBodyValue(VALUE_ID+"\""+storedAnno.getAnnotationId().getIdentifier()+"\"", TOTAL_BY_ID_FOUND);
		searchByBodyValue(VALUE_BODY_VALUE, TOTAL_BY_ID_FOUND);
		searchByBodyValue(VALUE_TARGET_TAG, TOTAL_BY_ID_FOUND);
		
		// validate fields
		assertTrue(storedAnno.getMotivation().equals(MotivationTypes.TAGGING.name().toLowerCase()));
		assertEquals(storedAnno.getBody().getInternalType(), BodyInternalTypes.TAG.name());
		TagBody tagBody = ((TagBody) storedAnno.getBody());
		assertNotNull(tagBody.getValue());
		assertTrue(tagBody.getValue().equals("trombone"));
		Target target = storedAnno.getTarget();
		assertNotNull(target.getHttpUri());
		assertTrue(target.getHttpUri().equals("http://data.europeana.eu/item/000002/_UEDIN_214"));

		// remove tag
		deleteAnnotation(storedAnno);
	}
	
	@Test
	public void createTagText() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(DESCRIBING_WEB_RESOURCE);
		
		// create indexed tag
		Annotation storedAnno = createTag(requestBody);
		
		assertTrue(BodyInternalTypes.isTextualBody(storedAnno.getBody().getInternalType()));
		
		// search for indexed id and textual values
		searchByBodyValue(VALUE_ID+"\""+storedAnno.getAnnotationId().getIdentifier()+"\"", TOTAL_BY_ID_FOUND);
		searchByBodyValue(VALUE_DESCRIBING_BODY_VALUE, TOTAL_BY_ID_FOUND);
		searchByBodyValue(VALUE_DESCRIBING_TARGET_SCOPE, TOTAL_BY_ID_FOUND);
		
		// validate fields
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

		// remove tag
		deleteAnnotation(storedAnno);
	}
	
	@Test
	public void createLink() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(LINK_STANDARD);
		
		// create indexed tag
		Annotation storedAnno = createLink(requestBody);
		
		// search for indexed id and textual values
		searchByBodyValue(VALUE_ID+"\""+storedAnno.getAnnotationId().getIdentifier()+"\"", TOTAL_BY_ID_FOUND);
		searchByBodyValue(VALUE_TARGET_LINK, TOTAL_BY_ID_FOUND);
		
		// validate fields
		assertTrue(storedAnno.getMotivation().equals(MotivationTypes.LINKING.name().toLowerCase()));
		Target target = storedAnno.getTarget();
		assertNotNull(target.getValues());
		assertTrue(target.getValues().contains("http://data.europeana.eu/item/2020601/https___1914_1918_europeana_eu_contributions_19584"));

		// remove tag
		deleteAnnotation(storedAnno);
	}
	
	@Test
	public void createSemanticLink() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(LINK_SEMANTIC);
		
		// create indexed tag
		Annotation storedAnno = createLink(requestBody);
		
		// search for indexed id and textual values
		searchByBodyValue(VALUE_ID+"\""+storedAnno.getAnnotationId().getIdentifier()+"\"", TOTAL_BY_ID_FOUND);
		searchByBodyValue(VALUE_TARGET_LINK_SEMANTIC, TOTAL_BY_ID_FOUND);
		
		// validate fields
		assertTrue(storedAnno.getMotivation().equals(MotivationTypes.LINKING.name().toLowerCase()));
		Target target = storedAnno.getTarget();
		assertNotNull(target.getHttpUri());
		assertTrue(target.getHttpUri().equals("http://data.europeana.eu/item/2048410/item_I5DUPVW2Q5HT2OQFSVXV7VYODA5P32P6"));

		// remove tag
		deleteAnnotation(storedAnno);
	}
	
	@Test
	public void createGraph() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(LINK_SEMANTIC);
		
		// create indexed tag
		Annotation storedAnno = createLink(requestBody);
		
		// search for indexed id and textual values
		searchByBodyValue(VALUE_ID+"\""+storedAnno.getAnnotationId().getIdentifier()+"\"", TOTAL_BY_ID_FOUND);
		searchByBodyValue(VALUE_BODY_LINK_RESOURCE_URI, TOTAL_BY_ID_FOUND);
		searchByBodyValue(VALUE_BODY_LINK_RELATION, TOTAL_BY_ID_FOUND);
		searchByBodyValue(VALUE_TARGET_LINK_SEMANTIC, TOTAL_BY_ID_FOUND);
		
		// validate fields
		assertTrue(storedAnno.getMotivation().equals(MotivationTypes.LINKING.name().toLowerCase()));
		assertEquals(storedAnno.getBody().getInternalType(), BodyInternalTypes.GRAPH.name());
		Graph graphBody = ((GraphBody) storedAnno.getBody()).getGraph();
		assertNotNull(graphBody.getNode());
		assertTrue(graphBody.getNode().getHttpUri().equals("https://www.wikidata.org/wiki/Q762"));
		assertTrue(graphBody.getRelationName().equals("isSimilarTo"));
		assertTrue(graphBody.getResourceUri().equals("http://data.europeana.eu/item/2048410/item_I5DUPVW2Q5HT2OQFSVXV7VYODA5P32P6"));
		Target target = storedAnno.getTarget();
		assertNotNull(target.getHttpUri());
		assertTrue(target.getHttpUri().equals("http://data.europeana.eu/item/2048410/item_I5DUPVW2Q5HT2OQFSVXV7VYODA5P32P6"));

		// remove tag
		deleteAnnotation(storedAnno);
	}
	
	@Test
	public void createSemanticTagSpecific() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(SEMANTICTAG_SPECIFIC_STANDARD);
		
		Annotation inputAnno = parseTag(requestBody);
		
		// create indexed tag
		Annotation storedAnno = createTag(requestBody);
		
		assertTrue(BodyInternalTypes.isSemanticTagBody(storedAnno.getBody().getInternalType()));
		//validate the reflection of input in output!
		validateOutputAgainstInput(storedAnno, inputAnno);
		
		// search for indexed id and textual values
		searchByBodyValue(VALUE_ID+"\""+storedAnno.getAnnotationId().getIdentifier()+"\"", TOTAL_BY_ID_FOUND);
		searchByBodyValue(VALUE_BODY_SPECIFIC_RESOURCE, TOTAL_BY_ID_FOUND);
		searchByBodyValue(VALUE_TARGET, TOTAL_BY_ID_FOUND);
		
		// validate fields
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

		// remove tag
		deleteAnnotation(storedAnno);
	}

	@Test
	public void createFullTextResourceTag() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(TRANSCRIPTION_WITH_RIGHTS);
		
		Annotation inputAnno = parseTag(requestBody);
		
		// create indexed tag
		Annotation storedAnno = createTag(requestBody);
		
		assertTrue(BodyInternalTypes.isFullTextResourceTagBody(storedAnno.getBody().getInternalType()));
		//validate the reflection of input in output!
		validateOutputAgainstInput(storedAnno, inputAnno);
		
		// search for indexed id and textual values
		searchByBodyValue(VALUE_ID+"\""+storedAnno.getAnnotationId().getIdentifier()+"\"", TOTAL_BY_ID_FOUND);
		searchByBodyValue(VALUE_BODY_FULL_TEXT_RESOURCE, TOTAL_BY_ID_FOUND);
		searchByBodyValue(VALUE_TARGET, TOTAL_BY_ID_FOUND);
		
		// validate fields
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

		// remove tag
		deleteAnnotation(storedAnno);
	}

	/**
	 * Search annotations by textual body value for different body types
	 * @param bodyValue
	 * @param foundAnnotationsNumber
	 */
	private void searchByBodyValue(String bodyValue, int foundAnnotationsNumber) {
		AnnotationPage annPg = annotationSearchApi.searchAnnotations(bodyValue, SearchProfiles.MINIMAL, null);
		assertNotNull(annPg, "AnnotationPage must not be null");
		assertTrue(foundAnnotationsNumber <= annPg.getTotalInCollection());
	}	

}
