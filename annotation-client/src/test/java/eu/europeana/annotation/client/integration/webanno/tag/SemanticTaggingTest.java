package eu.europeana.annotation.client.integration.webanno.tag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.Test;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.BodyInternalTypes;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

public class SemanticTaggingTest extends BaseTaggingTest {

	
	@Test
	public void createSemanticTagSimpleMinimal() throws IOException, JsonParseException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {

		Annotation anno = createAndValidateTag(SEMANTICTAG_SIMPLE_MINIMAL);
		System.out.println(anno.getBody().getInternalType());
	}

	@Test
	public void createSemanticTagSimpleStandard() throws IOException, JsonParseException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {

		Annotation anno = createAndValidateTag(SEMANTICTAG_SIMPLE_STANDARD);
		System.out.println(anno.getBody().getInternalType());

	}

	@Test
	public void createSemanticTagSpecificMinimal() throws IOException, JsonParseException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {

		Annotation anno = createAndValidateTag(SEMANTICTAG_SPECIFIC_MINIMAL);
		System.out.println(anno.getBody().getInternalType());

	}

	@Test
	public void createSemanticTagSpecificStandard() throws IOException, JsonParseException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {

		Annotation anno = createAndValidateTag(SEMANTICTAG_SPECIFIC_STANDARD);
		System.out.println(anno.getBody().getInternalType());

	}

	@Test
	public void createSemanticTagWebResource() throws IOException, JsonParseException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {

		Annotation storedAnno = createTag(SEMANTICTAG_WEB_RESOURCE, false);
		System.out.println(storedAnno.getBody().getInternalType());
		assertTrue(storedAnno.getMotivation().equals(MotivationTypes.TAGGING.name().toLowerCase()));
		assertTrue(storedAnno.getTarget().getSource() != null);
		assertEquals(storedAnno.getBody().getInternalType(), BodyInternalTypes.SEMANTIC_TAG.name());

	}

}
