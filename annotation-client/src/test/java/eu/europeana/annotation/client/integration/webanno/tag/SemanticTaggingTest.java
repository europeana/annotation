package eu.europeana.annotation.client.integration.webanno.tag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.Test;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.agent.impl.EdmAgent;
import eu.europeana.annotation.definitions.model.body.impl.EdmAgentBody;
import eu.europeana.annotation.definitions.model.vocabulary.BodyInternalTypes;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

public class SemanticTaggingTest extends BaseTaggingTest {

	protected Logger log = LogManager.getLogger(getClass());
	
	@Test
	public void createSemanticTagSimpleMinimal() throws IOException, JsonParseException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {

		Annotation anno = createAndValidateTag(SEMANTICTAG_SIMPLE_MINIMAL);
		log.info(anno.getBody().getInternalType());
	}

	@Test
	public void createSemanticTagSimpleStandard() throws IOException, JsonParseException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {

		Annotation anno = createAndValidateTag(SEMANTICTAG_SIMPLE_STANDARD);
		log.info(anno.getBody().getInternalType());

	}

	@Test
	public void createSemanticTagSpecificMinimal() throws IOException, JsonParseException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {

		Annotation anno = createAndValidateTag(SEMANTICTAG_SPECIFIC_MINIMAL);
		log.info(anno.getBody().getInternalType());

	}

	@Test
	public void createSemanticTagSpecificStandard() throws IOException, JsonParseException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {

		Annotation anno = createAndValidateTag(SEMANTICTAG_SPECIFIC_STANDARD);
		log.info(anno.getBody().getInternalType());

	}

	@Test
	public void createSemanticTagWebResource() throws IOException, JsonParseException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {

		Annotation storedAnno = createTag(SEMANTICTAG_WEB_RESOURCE, false);
		log.info(storedAnno.getBody().getInternalType());
		assertTrue(storedAnno.getMotivation().equals(MotivationTypes.TAGGING.name().toLowerCase()));
		assertTrue(storedAnno.getTarget().getSource() != null);
		assertEquals(storedAnno.getBody().getInternalType(), BodyInternalTypes.SEMANTIC_TAG.name());

	}

	@Test
	public void createSemanticTagEntity() throws IOException, JsonParseException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {

		Annotation storedAnno = createTag(SEMANTICTAG_ENTITY, false);
		log.info(storedAnno.getBody().getInternalType());
		assertTrue(storedAnno.getMotivation().equals(MotivationTypes.TAGGING.name().toLowerCase()));
		assertEquals(storedAnno.getBody().getInternalType(), BodyInternalTypes.AGENT.name());
		EdmAgent agentBody = (EdmAgent) ((EdmAgentBody) storedAnno.getBody()).getAgent();
		assertNotNull(agentBody.getDateOfBirth());
		assertNotNull(agentBody.getDateOfDeath());
		assertNotNull(agentBody.getPlaceOfBirth());
		assertNotNull(agentBody.getPlaceOfDeath());
		assertNotNull(agentBody.getPrefLabel());
	}

}
