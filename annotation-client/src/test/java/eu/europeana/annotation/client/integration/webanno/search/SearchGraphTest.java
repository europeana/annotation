package eu.europeana.annotation.client.integration.webanno.search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import eu.europeana.annotation.client.config.ClientConfiguration;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.body.GraphBody;
import eu.europeana.annotation.definitions.model.graph.Graph;
import eu.europeana.annotation.definitions.model.target.Target;
import eu.europeana.annotation.definitions.model.vocabulary.BodyInternalTypes;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

/**
 * This class implements tests for searching textual values for 
 * Graph body type.
 * 
 * @author GrafR
 *
 */
public class SearchGraphTest extends BaseSearchTest {

		
	@Test
	public void searchGraph() throws Exception {
		
		String requestBody = getJsonStringInput(LINK_SEMANTIC);
		
		// create indexed tag
		Annotation createdAnno = createLink(requestBody);
		createdAnnotations.add(createdAnno.getIdentifier());
		
		// search for indexed id and textual values
		Annotation retrievedAnno = searchLastCreated(VALUE_ID+"\""+createdAnno.getIdentifier()+"\"");
		validateGraph(retrievedAnno);
		retrievedAnno = searchLastCreated(VALUE_SEARCH_BODY_LINK_RESOURCE_URI);
		validateGraph(retrievedAnno);
		retrievedAnno = searchLastCreated(VALUE_SEARCH_BODY_LINK_RELATION);
		validateGraph(retrievedAnno);
		String VALUE_TARGET_LINK_SEMANTIC_URI = ClientConfiguration.getInstance().getPropAnnotationItemDataEndpoint() + "/2048410/item_I5DUPVW2Q5HT2OQFSVXV7VYODA5P32P6";
		String VALUE_SEARCH_TARGET_LINK_SEMANTIC = "target_uri:\""+ VALUE_TARGET_LINK_SEMANTIC_URI +"\"";
		retrievedAnno = searchLastCreated(VALUE_SEARCH_TARGET_LINK_SEMANTIC);
		validateGraph(retrievedAnno);
	}

	/**
	 * Validate graph fields after search.
	 * 
	 * @param storedAnno
	 */
	private void validateGraph(Annotation storedAnno) {
		assertEquals(MotivationTypes.LINKING.name().toLowerCase(), storedAnno.getMotivation());
		assertEquals(BodyInternalTypes.GRAPH.name(), storedAnno.getBody().getInternalType());
		Graph graphBody = ((GraphBody) storedAnno.getBody()).getGraph();
		assertNotNull(graphBody.getNode());
		assertEquals(VALUE_BODY_LINK_RESOURCE_URI, graphBody.getNode().getHttpUri());
		assertEquals(VALUE_BODY_LINK_RELATION, graphBody.getRelationName());
		String VALUE_TARGET_LINK_SEMANTIC_URI = ClientConfiguration.getInstance().getPropAnnotationItemDataEndpoint() + "/2048410/item_I5DUPVW2Q5HT2OQFSVXV7VYODA5P32P6";
		assertEquals(VALUE_TARGET_LINK_SEMANTIC_URI, graphBody.getResourceUri());
		Target target = storedAnno.getTarget();
		assertNotNull(target.getHttpUri());
		assertEquals(VALUE_TARGET_LINK_SEMANTIC_URI, target.getHttpUri());
	}
	
}
