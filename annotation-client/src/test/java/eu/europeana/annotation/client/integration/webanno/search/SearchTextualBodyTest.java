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
import eu.europeana.annotation.definitions.model.search.result.AnnotationPage;
import eu.europeana.annotation.definitions.model.target.Target;
import eu.europeana.annotation.definitions.model.vocabulary.BodyInternalTypes;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

/**
 * This class implements tests for searching annotation with motivation tagging
 * 
 * @author GrafR
 *
 */
public class SearchTextualBodyTest extends BaseSearchTest {

    String VALUE_SEARCH_TARGET_URI = "target_uri:\"http://data.europeana.eu/item/07931/diglit_uah_m1\"";

    @Test
    public void searchDescriptionBody() throws IOException, JsonParseException, IllegalAccessException,
	    IllegalArgumentException, InvocationTargetException {

	String requestBody = getJsonStringInput(DESCRIBING_WEB_RESOURCE);

	// create indexed tag
	//TODO: change to create annotation
	Annotation createdAnno = createTag(requestBody);

//	 "scope": "http://data.europeana.eu/item/07931/diglit_uah_m1",
//	    "source": 
//	    "http://www.europeana1914-1918.eu/attachments/2020601/20841.235882.full.jpg"

	assertTrue(BodyInternalTypes.isTextualBody(createdAnno.getBody().getInternalType()));

	// search for indexed id and textual values
	Annotation retrievedAnno = searchLastCreated(
		VALUE_ID + "\"" + createdAnno.getAnnotationId().getIdentifier() + "\"");
	validateDescriptionBody(retrievedAnno);
	retrievedAnno = searchLastCreated(VALUE_SEARCH_DESCRIBING_BODY_VALUE);
	validateDescriptionBody(retrievedAnno);
	retrievedAnno = searchLastCreated(VALUE_SEARCH_DESCRIBING_TARGET_SCOPE);
	validateDescriptionBody(retrievedAnno);
	retrievedAnno = searchLastCreated(VALUE_SEARCH_DESCRIBING_TARGET_SOURCE);
	validateDescriptionBody(retrievedAnno);
	
	// remove tag
	deleteAnnotation(createdAnno);
    }

    /**
     * Validate tag text fields after search.
     * 
     * @param storedAnno
     */
    private void validateDescriptionBody(Annotation storedAnno) {
	assertTrue(storedAnno.getMotivation().equals(MotivationTypes.DESCRIBING.name().toLowerCase()));
	assertEquals(storedAnno.getBody().getInternalType(), BodyInternalTypes.TEXT.name());
	TextBody textBody = ((TextBody) storedAnno.getBody());
	assertNotNull(textBody.getValue());
	assertTrue(textBody.getValue().equals("... this is the textual description of the item ..."));
	Target target = storedAnno.getTarget();
	assertNotNull(target.getSource());
	assertTrue(target.getSource()
		.equals("http://www.europeana1914-1918.eu/attachments/2020601/20841.235882.full.jpg"));
	assertNotNull(target.getScope());
	assertTrue(target.getScope().equals("http://data.europeana.eu/item/07931/diglit_uah_m1"));
    }

    @Test
    public void searchTranscriptionBody() throws IOException, JsonParseException, IllegalAccessException,
	    IllegalArgumentException, InvocationTargetException {

	String requestBody = getJsonStringInput(TRANSCRIPTION_WITH_RIGHTS);

	Annotation inputAnno = parseTag(requestBody);

	// create indexed tag
	//TODO: change to create annotation
	Annotation createdAnno = createTag(requestBody);

	assertTrue(BodyInternalTypes.isFullTextResourceTagBody(createdAnno.getBody().getInternalType()));
	// validate the reflection of input in output!
	validateOutputAgainstInput(createdAnno, inputAnno);

	// search for indexed id and textual values
	Annotation retrievedAnno = searchLastCreated(
		VALUE_ID + "\"" + createdAnno.getAnnotationId().getIdentifier() + "\"");
	validateTranscriptionBody(retrievedAnno);
	retrievedAnno = searchLastCreated(VALUE_SEARCH_BODY_FULL_TEXT_RESOURCE);
	validateTranscriptionBody(retrievedAnno);
	retrievedAnno = searchLastCreated(VALUE_SEARCH_TARGET_URI);
	validateTranscriptionBody(retrievedAnno);

	// remove tag
	deleteAnnotation(createdAnno);
    }

    /**
     * Validate full text resource tag fields after search.
     * 
     * @param storedAnno
     */
    private void validateTranscriptionBody(Annotation storedAnno) {
	assertTrue(storedAnno.getMotivation().equals(MotivationTypes.TRANSCRIBING.name().toLowerCase()));
	assertEquals(storedAnno.getBody().getInternalType(), BodyInternalTypes.FULL_TEXT_RESOURCE.name());
	FullTextResourceBody textBody = ((FullTextResourceBody) storedAnno.getBody());
	assertNotNull(textBody.getValue());
	assertTrue(textBody.getValue().equals("... complete transcribed text in HTML ..."));
	Target target = storedAnno.getTarget();
	assertNotNull(target.getSource());
	assertTrue(target.getSource()
		.equals("http://www.europeana1914-1918.eu/attachments/2020601/20841.235882.full.jpg"));
	assertNotNull(target.getScope());
	assertTrue(target.getScope().equals("http://data.europeana.eu/item/07931/diglit_uah_m1"));
    }

}
