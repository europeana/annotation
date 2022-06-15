package eu.europeana.annotation.tests.webanno.search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.body.impl.FullTextResourceBody;
import eu.europeana.annotation.definitions.model.body.impl.TextBody;
import eu.europeana.annotation.definitions.model.target.Target;
import eu.europeana.annotation.definitions.model.vocabulary.BodyInternalTypes;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.tests.AnnotationTestUtils;
import eu.europeana.annotation.tests.config.AnnotationTestsConfiguration;

/**
 * This class implements tests for searching annotation with motivation tagging
 * 
 * @author GrafR
 *
 */
public class SearchTextualBodyTest extends BaseSearchTest {

    @Test
    public void searchDescriptionBody() throws Exception {

	String requestBody = AnnotationTestUtils.getJsonStringInput(DESCRIBING_WEB_RESOURCE);

	// create indexed tag
	//TODO: change to create annotation
	Annotation createdAnno = createTag(requestBody);

//	 "scope": "http://data.europeana.eu/item/07931/diglit_uah_m1",
//	    "source": 
//	    "http://www.europeana1914-1918.eu/attachments/2020601/20841.235882.full.jpg"

	assertTrue(BodyInternalTypes.isTextualBody(createdAnno.getBody().getInternalType()));

	// search for indexed id and textual values
	Annotation retrievedAnno = searchLastCreated(
		VALUE_ID + "\"" + createdAnno.getIdentifier() + "\"");
	validateDescriptionBody(retrievedAnno);
	retrievedAnno = searchLastCreated(VALUE_SEARCH_DESCRIBING_BODY_VALUE);
	validateDescriptionBody(retrievedAnno);
	String VALUE_DESCRIBING_TARGET_SCOPE_URI = AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint() + "/07931/diglit_uah_m1";
	String VALUE_SEARCH_DESCRIBING_TARGET_SCOPE = "target_uri:\""+ VALUE_DESCRIBING_TARGET_SCOPE_URI +"\"";
	retrievedAnno = searchLastCreated(VALUE_SEARCH_DESCRIBING_TARGET_SCOPE);
	validateDescriptionBody(retrievedAnno);
	retrievedAnno = searchLastCreated(VALUE_SEARCH_DESCRIBING_TARGET_SOURCE);
	validateDescriptionBody(retrievedAnno);
	
	// remove tag
	removeAnnotationManually(createdAnno.getIdentifier());
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
	assertTrue(target.getScope().equals(AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint() + "/07931/diglit_uah_m1"));
    }

    @Test
    public void searchTranscriptionBody() throws Exception {

	String requestBody = AnnotationTestUtils.getJsonStringInput(TRANSCRIPTION_WITH_RIGHTS);

	Annotation inputAnno = parseTag(requestBody);

	// create indexed tag
	//TODO: change to create annotation
	Annotation createdAnno = createTag(requestBody);

	assertTrue(BodyInternalTypes.isFullTextResourceTagBody(createdAnno.getBody().getInternalType()));
	// validate the reflection of input in output!
	AnnotationTestUtils.validateOutputAgainstInput(createdAnno, inputAnno);

	// search for indexed id and textual values
	Annotation retrievedAnno = searchLastCreated(
		VALUE_ID + "\"" + createdAnno.getIdentifier() + "\"");
	validateTranscriptionBody(retrievedAnno);
	retrievedAnno = searchLastCreated(VALUE_SEARCH_BODY_FULL_TEXT_RESOURCE);
	validateTranscriptionBody(retrievedAnno);
	String VALUE_SEARCH_TARGET_URI = "target_uri:\"" + AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint() + "/07931/diglit_uah_m1\"";
	retrievedAnno = searchLastCreated(VALUE_SEARCH_TARGET_URI);
	validateTranscriptionBody(retrievedAnno);

	// remove tag
	removeAnnotationManually(createdAnno.getIdentifier());
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
	assertTrue(target.getScope().equals(AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint() + "/07931/diglit_uah_m1"));
    }

}
