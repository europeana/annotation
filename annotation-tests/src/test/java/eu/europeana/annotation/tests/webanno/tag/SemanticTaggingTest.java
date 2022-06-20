package eu.europeana.annotation.tests.webanno.tag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.agent.impl.EdmAgent;
import eu.europeana.annotation.definitions.model.body.impl.EdmAgentBody;
import eu.europeana.annotation.definitions.model.vocabulary.BodyInternalTypes;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

public class SemanticTaggingTest extends BaseTaggingTest {

	protected Logger log = LogManager.getLogger(getClass());
	
	@Test
	public void createSemanticTagSimpleMinimal() throws Exception {

		Annotation anno = createAndValidateTag(SEMANTICTAG_SIMPLE_MINIMAL);
		createdAnnotations.add(anno.getIdentifier());
		log.info(anno.getBody().getInternalType());
	}

	@Test
	public void createSemanticTagSimpleStandard() throws Exception {

		Annotation anno = createAndValidateTag(SEMANTICTAG_SIMPLE_STANDARD);
		createdAnnotations.add(anno.getIdentifier());
		log.info(anno.getBody().getInternalType());
	}

	@Test
	public void createSemanticTagSpecificMinimal() throws Exception {

		Annotation anno = createAndValidateTag(SEMANTICTAG_SPECIFIC_MINIMAL);
		createdAnnotations.add(anno.getIdentifier());
		log.info(anno.getBody().getInternalType());
	}

	@Test
	public void createSemanticTagSpecificStandard() throws Exception {

		Annotation anno = createAndValidateTag(SEMANTICTAG_SPECIFIC_STANDARD);
		createdAnnotations.add(anno.getIdentifier());
		log.info(anno.getBody().getInternalType());
	}

	@Test
	public void createSemanticTagWebResource() throws Exception {

		Annotation storedAnno = createTag(SEMANTICTAG_WEB_RESOURCE, false, true);
		createdAnnotations.add(storedAnno.getIdentifier());
		log.info(storedAnno.getBody().getInternalType());
		assertTrue(storedAnno.getMotivation().equals(MotivationTypes.TAGGING.name().toLowerCase()));
		assertTrue(storedAnno.getTarget().getSource() != null);
		assertEquals(storedAnno.getBody().getInternalType(), BodyInternalTypes.SEMANTIC_TAG.name());
	}

	@Test
	public void createSemanticTagEntity() throws Exception {

		Annotation storedAnno = createTag(SEMANTICTAG_ENTITY, false, true);
		createdAnnotations.add(storedAnno.getIdentifier());
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

	@Test
	public void createSemanticTagAgentEntity() throws Exception {

		Annotation storedAnno = createTag(SEMANTICTAG_AGENT_ENTITY, false, true);
		createdAnnotations.add(storedAnno.getIdentifier());
		log.info(storedAnno.getBody().getInternalType());
		assertTrue(storedAnno.getMotivation().equals(MotivationTypes.TAGGING.name().toLowerCase()));
		assertEquals(storedAnno.getBody().getInternalType(), BodyInternalTypes.AGENT.name());
		EdmAgent agentBody = (EdmAgent) ((EdmAgentBody) storedAnno.getBody()).getAgent();
		assertNotNull(agentBody.getPrefLabel());
		assertTrue(agentBody.getPrefLabel().size() == 43);
		assertNotNull(agentBody.getDateOfBirth());
		assertNotNull(agentBody.getDateOfDeath());
	}

}
