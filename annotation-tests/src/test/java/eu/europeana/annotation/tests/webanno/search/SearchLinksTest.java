package eu.europeana.annotation.tests.webanno.search;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.target.Target;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.tests.AnnotationTestUtils;
import eu.europeana.annotation.tests.config.AnnotationTestsConfiguration;

/**
 * This class implements tests for searching annotations with motivation linking
 * 
 * @author GrafR
 *
 */
public class SearchLinksTest extends BaseSearchTest {

	@Test
	public void searchLink() throws Exception {
		
		String requestBody = AnnotationTestUtils.getJsonStringInput(LINK_STANDARD);
		
		// create indexed tag
		Annotation createdAnno = createLink(requestBody);
		createdAnnotations.add(createdAnno.getIdentifier());
		
		// search for indexed id and textual values
		Annotation retrievedAnno = searchLastCreated(VALUE_ID+"\""+createdAnno.getIdentifier()+"\"");
		validateLink(retrievedAnno);
		String VALUE_TARGET_LINK_URI = AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint() + "/2020601/https___1914_1918_europeana_eu_contributions_19584";
		String VALUE_SEARCH_TARGET_LINK = "target_uri:\""+ VALUE_TARGET_LINK_URI +"\"";
		retrievedAnno = searchLastCreated(VALUE_SEARCH_TARGET_LINK);
		validateLink(retrievedAnno);
	}

	/**
	 * Validate link fields after search.
	 * 
	 * @param storedAnno
	 * @throws IOException 
	 */
	private void validateLink(Annotation storedAnno) throws IOException {
		assertTrue(storedAnno.getMotivation().equals(MotivationTypes.LINKING.name().toLowerCase()));
		Target target = storedAnno.getTarget();
		assertNotNull(target.getValues());
		String VALUE_TARGET_LINK_URI = AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint() + "/2020601/https___1914_1918_europeana_eu_contributions_19584";
		assertTrue(target.getValues().contains(VALUE_TARGET_LINK_URI));
	}
	
	@Test
	public void searchSemanticLink() throws Exception {
		
		String requestBody = AnnotationTestUtils.getJsonStringInput(LINK_SEMANTIC);
		
		// create indexed tag
		Annotation createdAnno = createLink(requestBody);
		createdAnnotations.add(createdAnno.getIdentifier());
		
		// search for indexed id and textual values
		Annotation retrievedAnno = searchLastCreated(VALUE_ID+"\""+createdAnno.getIdentifier()+"\"");
		validateSemanticLink(retrievedAnno);
		String VALUE_TARGET_LINK_SEMANTIC_URI = AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint() + "/2048410/item_I5DUPVW2Q5HT2OQFSVXV7VYODA5P32P6";
		String VALUE_SEARCH_TARGET_LINK_SEMANTIC = "target_uri:\""+ VALUE_TARGET_LINK_SEMANTIC_URI +"\"";
		retrievedAnno = searchLastCreated(VALUE_SEARCH_TARGET_LINK_SEMANTIC);
		validateSemanticLink(retrievedAnno);
	}

	/**
	 * Validate semantic link fields after search.
	 * 
	 * @param storedAnno
	 * @throws IOException 
	 */
	private void validateSemanticLink(Annotation storedAnno) throws IOException {
		assertTrue(storedAnno.getMotivation().equals(MotivationTypes.LINKING.name().toLowerCase()));
		Target target = storedAnno.getTarget();
		assertNotNull(target.getHttpUri());
		String VALUE_TARGET_LINK_SEMANTIC_URI = AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint() + "/2048410/item_I5DUPVW2Q5HT2OQFSVXV7VYODA5P32P6";
		assertTrue(target.getHttpUri().equals(VALUE_TARGET_LINK_SEMANTIC_URI));
	}

}
