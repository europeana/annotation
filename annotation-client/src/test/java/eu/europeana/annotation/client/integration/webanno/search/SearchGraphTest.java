package eu.europeana.annotation.client.integration.webanno.search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.Test;

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
	public void searchGraph() throws IOException, JsonParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String requestBody = getJsonStringInput(LINK_SEMANTIC);
		
		// create indexed tag
		Annotation storedAnno = createLink(requestBody);
		
		// search for indexed id and textual values
		searchByBodyValue(VALUE_ID+"\""+storedAnno.getAnnotationId().getIdentifier()+"\"", TOTAL_BY_ID_FOUND);
		validateGraph(storedAnno);
		searchByBodyValue(VALUE_SEARCH_BODY_LINK_RESOURCE_URI, TOTAL_BY_ID_FOUND);
		validateGraph(storedAnno);
		searchByBodyValue(VALUE_SEARCH_BODY_LINK_RELATION, TOTAL_BY_ID_FOUND);
		validateGraph(storedAnno);
		searchByBodyValue(VALUE_SEARCH_TARGET_LINK_SEMANTIC, TOTAL_BY_ID_FOUND);
		validateGraph(storedAnno);

		// remove tag
		deleteAnnotation(storedAnno);
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
		assertEquals(VALUE_TARGET_LINK_SEMANTIC_URI, graphBody.getResourceUri());
		Target target = storedAnno.getTarget();
		assertNotNull(target.getHttpUri());
		assertEquals(VALUE_TARGET_LINK_SEMANTIC_URI, target.getHttpUri());
	}
	
}
