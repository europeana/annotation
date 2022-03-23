package eu.europeana.annotation.client.integration.webanno.search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.Test;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.body.GraphBody;
import eu.europeana.annotation.definitions.model.body.TagBody;
import eu.europeana.annotation.definitions.model.body.impl.EdmPlaceBody;
import eu.europeana.annotation.definitions.model.body.impl.SemanticTagBody;
import eu.europeana.annotation.definitions.model.body.impl.TextBody;
import eu.europeana.annotation.definitions.model.entity.impl.EdmPlace;
import eu.europeana.annotation.definitions.model.graph.Graph;
import eu.europeana.annotation.definitions.model.target.Target;
import eu.europeana.annotation.definitions.model.vocabulary.BodyInternalTypes;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

//TODO: this class is redundant with the individual search for annotation type classes, the test cases need to be moved to the approapriate class and this class should be deleted
/** @deprecated use the Search<annotype> classes instead */
@Deprecated
public class AnnotationSearchFieldsTest extends BaseSearchTest {

	static final String VALUE_TESTSET = "generator_uri: \"http://test.europeana.org/45e86248-1218-41fc-9643-689d30dbe651\"";
	static final String VALUE_ID = "anno_id:";
	static final String VALUE_BODY_URI = "body_uri:\"http://www.geonames.org/2988507\"";
	static final String VALUE_DESCRIBING_BODY_VALUE = "body_value:\"... this is the textual description of the item ...\"";
	static final String VALUE_BODY_VALUE = "body_value:\"trombone\"";
	static final String VALUE_BODY_LINK_RELATION = "link_relation:\"isSimilarTo\"";
	static final String VALUE_BODY_LINK_RESOURCE_URI = "link_resource_uri:\"https://www.wikidata.org/wiki/Q762\"";
	static final String VALUE_BODY_SPECIFIC_RESOURCE = "body_uri:\"http://www.geonames.org/2988507\""; // source
	static final String VALUE_BODY_FULL_TEXT_RESOURCE = "body_value:\"... complete transcribed text in HTML ...\"";

	
	@Test
	public void createGeoTag() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(TAG_GEOTAG);
		
		Annotation inputAnno = parseTag(requestBody);
		
		// create indexed tag
		Annotation createdAnno = createTag(requestBody);
		
		assertTrue(BodyInternalTypes.isGeoTagBody(createdAnno.getBody().getInternalType()));
		//validate the reflection of input in output!
		validateOutputAgainstInput(createdAnno, inputAnno);
		
		// search for indexed id and textual values
		Annotation retrievedAnno = searchLastCreated(VALUE_ID+"\""+createdAnno.getIdentifier()+"\"");
		String VALUE_TARGET = "target_uri:\"" + configuration.getAnnoItemDataEndpoint() + "/09102/_UEDIN_214\"";
		retrievedAnno = searchLastCreated(VALUE_TARGET);
		//assertNo
		
		// validate fields
		assertTrue(retrievedAnno.getMotivation().equals(MotivationTypes.TAGGING.name().toLowerCase()));
		assertEquals(retrievedAnno.getBody().getInternalType(), BodyInternalTypes.GEO_TAG.name());
		EdmPlace placeBody = (EdmPlace) ((EdmPlaceBody) retrievedAnno.getBody()).getPlace();
		assertNotNull(placeBody.getLatitude());
		assertTrue(placeBody.getLatitude().equals("48.853415"));
		assertNotNull(placeBody.getLongitude());
		assertTrue(placeBody.getLongitude().equals("-102.348800"));
		Target target = retrievedAnno.getTarget();
		assertNotNull(target.getHttpUri());
		assertTrue(target.getHttpUri().equals(configuration.getAnnoItemDataEndpoint() + "/09102/_UEDIN_214"));

		// remove tag
		deleteAnnotation(createdAnno);
	}

	@Test
	public void createTag() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(TAG_BODY_TEXT);
		
		Annotation inputAnno = parseTag(requestBody);
		
		// create indexed tag
		Annotation createdAnno = createTag(requestBody);
		
		assertTrue(BodyInternalTypes.isTagBody(createdAnno.getBody().getInternalType()));
		//validate the reflection of input in output!
		validateOutputAgainstInput(createdAnno, inputAnno);
		
		// search for indexed id and textual values
		Annotation retrievedAnno = searchLastCreated(VALUE_ID+"\""+createdAnno.getIdentifier()+"\"");
		retrievedAnno = searchLastCreated(VALUE_BODY_VALUE);
		String VALUE_TARGET_TAG = "target_uri:\""+ configuration.getAnnoItemDataEndpoint() + "/000002/_UEDIN_214\"";
		retrievedAnno = searchLastCreated(VALUE_TARGET_TAG);
		
		// validate fields
		assertTrue(retrievedAnno.getMotivation().equals(MotivationTypes.TAGGING.name().toLowerCase()));
		assertEquals(retrievedAnno.getBody().getInternalType(), BodyInternalTypes.TAG.name());
		TagBody tagBody = ((TagBody) retrievedAnno.getBody());
		assertNotNull(tagBody.getValue());
		assertTrue(tagBody.getValue().equals("trombone"));
		Target target = retrievedAnno.getTarget();
		assertNotNull(target.getHttpUri());
		assertTrue(target.getHttpUri().equals(configuration.getAnnoItemDataEndpoint() + "/000002/_UEDIN_214"));

		// remove tag
		deleteAnnotation(createdAnno);
	}
	
	@Test
	public void createTagText() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(DESCRIBING_WEB_RESOURCE);
		
		// create indexed tag
		Annotation createdAnno = createTag(requestBody);
		
		assertTrue(BodyInternalTypes.isTextualBody(createdAnno.getBody().getInternalType()));
		
		// search for indexed id and textual values
		Annotation retrievedAnno = searchLastCreated(VALUE_ID+"\""+createdAnno.getIdentifier()+"\"");
		retrievedAnno = searchLastCreated(VALUE_DESCRIBING_BODY_VALUE);
		String VALUE_DESCRIBING_TARGET_SCOPE = "target_uri:\"" + configuration.getAnnoItemDataEndpoint() + "/07931/diglit_uah_m1\"";
		retrievedAnno = searchLastCreated(VALUE_DESCRIBING_TARGET_SCOPE);
		
		// validate fields
		assertTrue(retrievedAnno.getMotivation().equals(MotivationTypes.DESCRIBING.name().toLowerCase()));
		assertEquals(retrievedAnno.getBody().getInternalType(), BodyInternalTypes.TEXT.name());
		TextBody textBody = ((TextBody) retrievedAnno.getBody());
		assertNotNull(textBody.getValue());
		assertTrue(textBody.getValue().equals("... this is the textual description of the item ..."));
		Target target = retrievedAnno.getTarget();
		assertNotNull(target.getSource());
		assertTrue(target.getSource().equals("http://www.europeana1914-1918.eu/attachments/2020601/20841.235882.full.jpg"));
		assertNotNull(target.getScope());
		assertTrue(target.getScope().equals(configuration.getAnnoItemDataEndpoint() + "/07931/diglit_uah_m1"));

		// remove tag
		deleteAnnotation(createdAnno);
	}
	
	@Test
	public void createLink() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(LINK_STANDARD);
		
		// create indexed tag
		Annotation createddAnno = createLink(requestBody);
		
		// search for indexed id and textual values
		Annotation retrievedAnno = searchLastCreated(VALUE_ID+"\""+createddAnno.getIdentifier()+"\"");
		String VALUE_TARGET_LINK = "target_uri:\"" + configuration.getAnnoItemDataEndpoint() + "/2020601/https___1914_1918_europeana_eu_contributions_19584\"";
		retrievedAnno = searchLastCreated(VALUE_TARGET_LINK);
		
		// validate fields
		assertTrue(retrievedAnno.getMotivation().equals(MotivationTypes.LINKING.name().toLowerCase()));
		Target target = retrievedAnno.getTarget();
		assertNotNull(target.getValues());
		assertTrue(target.getValues().contains(configuration.getAnnoItemDataEndpoint() + "/2020601/https___1914_1918_europeana_eu_contributions_19584"));

		// remove tag
		deleteAnnotation(createddAnno);
	}
	
	@Test
	public void createSemanticLink() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(LINK_SEMANTIC);
		
		// create indexed tag
		Annotation createdAnno = createLink(requestBody);
		
		// search for indexed id and textual values
		Annotation retrievedAnno = searchLastCreated(VALUE_ID+"\""+createdAnno.getIdentifier()+"\"");
		String VALUE_TARGET_LINK_SEMANTIC = "target_uri:\"" + configuration.getAnnoItemDataEndpoint() + "/2048410/item_I5DUPVW2Q5HT2OQFSVXV7VYODA5P32P6\"";
		retrievedAnno = searchLastCreated(VALUE_TARGET_LINK_SEMANTIC);
		
		// validate fields
		assertTrue(retrievedAnno.getMotivation().equals(MotivationTypes.LINKING.name().toLowerCase()));
		Target target = retrievedAnno.getTarget();
		assertNotNull(target.getHttpUri());
		assertTrue(target.getHttpUri().equals(configuration.getAnnoItemDataEndpoint() + "/2048410/item_I5DUPVW2Q5HT2OQFSVXV7VYODA5P32P6"));

		// remove tag
		deleteAnnotation(createdAnno);
	}
	
	@Test
	public void createGraph() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(LINK_SEMANTIC);
		
		// create indexed tag
		Annotation createdAnno = createLink(requestBody);
		
		// search for indexed id and textual values
		Annotation retrievedAnno = searchLastCreated(VALUE_ID+"\""+createdAnno.getIdentifier()+"\"");
		retrievedAnno = searchLastCreated(VALUE_BODY_LINK_RESOURCE_URI);
		retrievedAnno = searchLastCreated(VALUE_BODY_LINK_RELATION);
		String VALUE_TARGET_LINK_SEMANTIC = "target_uri:\"" + configuration.getAnnoItemDataEndpoint() + "/2048410/item_I5DUPVW2Q5HT2OQFSVXV7VYODA5P32P6\"";
		retrievedAnno = searchLastCreated(VALUE_TARGET_LINK_SEMANTIC);
		
		// validate fields
		assertTrue(retrievedAnno.getMotivation().equals(MotivationTypes.LINKING.name().toLowerCase()));
		assertEquals(retrievedAnno.getBody().getInternalType(), BodyInternalTypes.GRAPH.name());
		Graph graphBody = ((GraphBody) retrievedAnno.getBody()).getGraph();
		assertNotNull(graphBody.getNode());
		assertTrue(graphBody.getNode().getHttpUri().equals("https://www.wikidata.org/wiki/Q762"));
		assertTrue(graphBody.getRelationName().equals("isSimilarTo"));
		assertTrue(graphBody.getResourceUri().equals(configuration.getAnnoItemDataEndpoint() + "/2048410/item_I5DUPVW2Q5HT2OQFSVXV7VYODA5P32P6"));
		Target target = retrievedAnno.getTarget();
		assertNotNull(target.getHttpUri());
		assertTrue(target.getHttpUri().equals(configuration.getAnnoItemDataEndpoint() + "/2048410/item_I5DUPVW2Q5HT2OQFSVXV7VYODA5P32P6"));

		// remove tag
		deleteAnnotation(createdAnno);
	}
	
	@Test
	public void createSemanticTagSpecific() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(SEMANTICTAG_SPECIFIC_STANDARD);
		
		Annotation inputAnno = parseTag(requestBody);
		
		// create indexed tag
		Annotation createdAnno = createTag(requestBody);
		
		assertTrue(BodyInternalTypes.isSemanticTagBody(createdAnno.getBody().getInternalType()));
		//validate the reflection of input in output!
		validateOutputAgainstInput(createdAnno, inputAnno);
		
		// search for indexed id and textual values
		Annotation retrievedAnno = searchLastCreated(VALUE_ID+"\""+createdAnno.getIdentifier()+"\"");
		retrievedAnno = searchLastCreated(VALUE_BODY_SPECIFIC_RESOURCE);
		String VALUE_TARGET = "target_uri:\"" + configuration.getAnnoItemDataEndpoint() + "/09102/_UEDIN_214\"";
		retrievedAnno = searchLastCreated(VALUE_TARGET);
		
		// validate fields
		assertTrue(retrievedAnno.getMotivation().equals(MotivationTypes.TAGGING.name().toLowerCase()));
		assertEquals(retrievedAnno.getBody().getInternalType(), BodyInternalTypes.SEMANTIC_TAG.name());
		TagBody tagBody = ((SemanticTagBody) retrievedAnno.getBody());
		assertNotNull(tagBody.getHttpUri());
		assertTrue(tagBody.getHttpUri().equals("http://sws.geonames.org/2988506"));
		assertNotNull(tagBody.getLanguage());
		assertTrue(tagBody.getLanguage().equals("en"));
		Target target = retrievedAnno.getTarget();
		assertNotNull(target.getHttpUri());
		assertTrue(target.getHttpUri().equals(configuration.getAnnoItemDataEndpoint() + "/09102/_UEDIN_214"));

		// remove tag
		deleteAnnotation(createdAnno);
	}
	

}
