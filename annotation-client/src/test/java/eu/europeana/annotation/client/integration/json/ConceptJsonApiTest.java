package eu.europeana.annotation.client.integration.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import eu.europeana.annotation.client.ConceptJsonApiImpl;
import eu.europeana.annotation.definitions.model.util.AnnotationTestObjectBuilder;
import eu.europeana.annotation.utils.JsonUtils;


//TODO: enabled when concept API is enabled
@Disabled
public class ConceptJsonApiTest extends AnnotationTestObjectBuilder{

	private static String TEST_URI = "concept.eu/new";
	
    private ConceptJsonApiImpl conceptJsonApi;
    
    @BeforeEach
    public void initObjects() {
    	conceptJsonApi = new ConceptJsonApiImpl();
    }

	public static String conceptJson = 
		"{" +
		"\"uri\": \"" + TEST_URI + "\"," +
		"\"type\": \"BASE_CONCEPT\"," +
		"\"notation\": \"skos:notation\"," +
		"\"prefLabel\": {" +
			"\"@id\": \"skos:prefLabel\"," +
			"\"@container\": \"@language\"" +
		"}," +
		"\"altLabel\": {" +
			"\"@id\": \"skos:altLabel\"," +
			"\"@container\": \"@language\"" +
		"}," +
		"\"hiddenLabel\": {" +
			"\"@id\": \"skos:altLabel\"," +
			"\"@container\": \"@language\"" +
		"}," +
		"\"narrower\": \"skos:narrower\"," +
		"\"broader\": \"skos:broader\"," +
		"\"related\": \"skos:related\"" +
		"}";

	@Test
	public void createConcept() {
		
		/**
		 * Create a test concept object.
		 */
		String concept = conceptJsonApi.createConcept(conceptJson);
		assertNotNull(concept);
	}
	
	@Test
	public void getConcept() {
		
		/**
		 * Create a concept object within the test and do not rely on the objects stored in the database.
		 */
		String concept = conceptJsonApi.createConcept(conceptJson);
		assertNotNull(concept);
		
		String resultConcept = conceptJsonApi.getConcept(TEST_URI);
		
		if (resultConcept == null) {
			System.out.println("No objects found in the database, test skipped");
			return;
		}
		
		assertNotNull(resultConcept);
		assertEquals(JsonUtils.extractUriFromConceptJson(resultConcept), TEST_URI);
	}
	
}
