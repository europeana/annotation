package eu.europeana.annotation.client.integration.webanno.tag;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.Test;

import eu.europeana.annotation.definitions.model.Annotation;

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
}
