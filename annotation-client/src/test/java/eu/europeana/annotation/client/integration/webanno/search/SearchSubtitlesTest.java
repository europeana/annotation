package eu.europeana.annotation.client.integration.webanno.search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.Test;

import eu.europeana.annotation.client.AnnotationSearchApiImpl;
import eu.europeana.annotation.client.integration.webanno.tag.BaseTaggingTest;
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
import eu.europeana.annotation.definitions.model.view.AnnotationView;
import eu.europeana.annotation.definitions.model.vocabulary.BodyInternalTypes;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

/**
 * This class implements tests for searching textual values in 
 * different body types.
 * 
 * @author GrafR
 *
 */
public class SearchSubtitlesTest extends BaseSearchTest {

	
	@Test
	public void searchSubtitles() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
	    
		String requestBody = getJsonStringInput(SUBTITLE_MINIMAL);
		MotivationTypes motivationType = MotivationTypes.SUBTITLING;
		Annotation inputAnno = parseAnnotation(requestBody, motivationType);
		
		// create indexed subtitle
		Annotation storedAnno = createTestAnnotation(SUBTITLE_MINIMAL, null);
		
		assertTrue(BodyInternalTypes.isFullTextResourceTagBody(storedAnno.getBody().getInternalType()));
		//validate the reflection of input in output!
		validateOutputAgainstInput(storedAnno, inputAnno);
		
		// search for indexed id and textual values
		//TODO: restore after updating schema
		//AnnotationPage annoPage = searchByBodyValue(VALUE_SEARCH_BODY_VALUE_IT, SearchProfiles.STANDARD, TOTAL_BY_ID_FOUND);
		AnnotationPage annoPage = searchByBodyValue("con il grande finale", SearchProfiles.STANDARD, "1", TOTAL_BY_ID_FOUND);
		Annotation retrievedAnnotation = (Annotation)annoPage.getAnnotations().get(0);
		assertEquals(storedAnno.getAnnotationId(), retrievedAnnotation.getAnnotationId());
		validateSubtitle(retrievedAnnotation);
//		searchByBodyValue(VALUE_SEARCH_TARGET, TOTAL_BY_ID_FOUND);
//		validateSubtitle(storedAnno);

		// remove tag
		deleteAnnotation(storedAnno);
	}

	/**
	 * Validate full text resource tag fields after search.
	 * 
	 * @param storedAnno
	 */
	private void validateSubtitle(Annotation storedAnno) {
		assertTrue(storedAnno.getMotivation().equals(MotivationTypes.SUBTITLING.name().toLowerCase()));
		assertEquals(storedAnno.getBody().getInternalType(), BodyInternalTypes.FULL_TEXT_RESOURCE.name());
		FullTextResourceBody textBody = ((FullTextResourceBody) storedAnno.getBody());
		assertNotNull(textBody.getValue());
		assertTrue(textBody.getValue().contains("con il grande finale"));
		Target target = storedAnno.getTarget();
		assertNotNull(target.getSource());
		assertTrue(target.getSource().equals("http://www.euscreen.eu/item.html?id=EUS_D61E8DF003E30114621A92ABDE846AD7"));
		assertNotNull(target.getScope());
		assertTrue(target.getScope().equals("http://data.europeana.eu/item/2051933/data_euscreenXL_EUS_D61E8DF003E30114621A92ABDE846AD7"));
	}
	

}
