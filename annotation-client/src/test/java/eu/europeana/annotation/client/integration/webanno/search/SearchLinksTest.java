package eu.europeana.annotation.client.integration.webanno.search;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.Test;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.target.Target;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

/**
 * This class implements tests for searching annotations with motivation linking
 * 
 * @author GrafR
 *
 */
public class SearchLinksTest extends BaseSearchTest {

	@Test
	public void searchLink() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(LINK_STANDARD);
		
		// create indexed tag
		Annotation storedAnno = createLink(requestBody);
		
		// search for indexed id and textual values
		searchByBodyValue(VALUE_ID+"\""+storedAnno.getAnnotationId().getIdentifier()+"\"", TOTAL_BY_ID_FOUND);
		validateLink(storedAnno);
		searchByBodyValue(VALUE_SEARCH_TARGET_LINK, TOTAL_BY_ID_FOUND);
		validateLink(storedAnno);

		// remove tag
		deleteAnnotation(storedAnno);
	}

	/**
	 * Validate link fields after search.
	 * 
	 * @param storedAnno
	 */
	private void validateLink(Annotation storedAnno) {
		assertTrue(storedAnno.getMotivation().equals(MotivationTypes.LINKING.name().toLowerCase()));
		Target target = storedAnno.getTarget();
		assertNotNull(target.getValues());
		assertTrue(target.getValues().contains(VALUE_TARGET_LINK_URI));
	}
	
	@Test
	public void searchSemanticLink() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(LINK_SEMANTIC);
		
		// create indexed tag
		Annotation storedAnno = createLink(requestBody);
		
		// search for indexed id and textual values
		searchByBodyValue(VALUE_ID+"\""+storedAnno.getAnnotationId().getIdentifier()+"\"", TOTAL_BY_ID_FOUND);
		validateSemanticLink(storedAnno);
		searchByBodyValue(VALUE_SEARCH_TARGET_LINK_SEMANTIC, TOTAL_BY_ID_FOUND);
		validateSemanticLink(storedAnno);

		// remove tag
		deleteAnnotation(storedAnno);
	}

	/**
	 * Validate semantic link fields after search.
	 * 
	 * @param storedAnno
	 */
	private void validateSemanticLink(Annotation storedAnno) {
		assertTrue(storedAnno.getMotivation().equals(MotivationTypes.LINKING.name().toLowerCase()));
		Target target = storedAnno.getTarget();
		assertNotNull(target.getHttpUri());
		assertTrue(target.getHttpUri().equals(VALUE_TARGET_LINK_SEMANTIC_URI));
	}

}
