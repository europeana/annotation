package eu.europeana.annotation.client.integration.webanno.search;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.Test;
import eu.europeana.annotation.client.config.ClientConfiguration;
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
		Annotation createdAnno = createLink(requestBody);
		addCreatedAnnotation(createdAnno);
		
		// search for indexed id and textual values
		Annotation retrievedAnno = searchLastCreated(VALUE_ID+"\""+createdAnno.getIdentifier()+"\"");
		validateLink(retrievedAnno);
		String VALUE_TARGET_LINK_URI = ClientConfiguration.getInstance().getPropAnnotationItemDataEndpoint() + "/2020601/https___1914_1918_europeana_eu_contributions_19584";
		String VALUE_SEARCH_TARGET_LINK = "target_uri:\""+ VALUE_TARGET_LINK_URI +"\"";
		retrievedAnno = searchLastCreated(VALUE_SEARCH_TARGET_LINK);
		validateLink(retrievedAnno);
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
		String VALUE_TARGET_LINK_URI = ClientConfiguration.getInstance().getPropAnnotationItemDataEndpoint() + "/2020601/https___1914_1918_europeana_eu_contributions_19584";
		assertTrue(target.getValues().contains(VALUE_TARGET_LINK_URI));
	}
	
	@Test
	public void searchSemanticLink() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(LINK_SEMANTIC);
		
		// create indexed tag
		Annotation createdAnno = createLink(requestBody);
		
		// search for indexed id and textual values
		Annotation retrievedAnno = searchLastCreated(VALUE_ID+"\""+createdAnno.getIdentifier()+"\"");
		validateSemanticLink(retrievedAnno);
		String VALUE_TARGET_LINK_SEMANTIC_URI = ClientConfiguration.getInstance().getPropAnnotationItemDataEndpoint() + "/2048410/item_I5DUPVW2Q5HT2OQFSVXV7VYODA5P32P6";
		String VALUE_SEARCH_TARGET_LINK_SEMANTIC = "target_uri:\""+ VALUE_TARGET_LINK_SEMANTIC_URI +"\"";
		retrievedAnno = searchLastCreated(VALUE_SEARCH_TARGET_LINK_SEMANTIC);
		validateSemanticLink(retrievedAnno);

		// remove tag
		removeAnnotation(createdAnno.getIdentifier());
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
		String VALUE_TARGET_LINK_SEMANTIC_URI = ClientConfiguration.getInstance().getPropAnnotationItemDataEndpoint() + "/2048410/item_I5DUPVW2Q5HT2OQFSVXV7VYODA5P32P6";
		assertTrue(target.getHttpUri().equals(VALUE_TARGET_LINK_SEMANTIC_URI));
	}

}
