package eu.europeana.annotation.tests.webanno.tag;

import org.apache.stanbol.commons.exception.JsonParseException;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.tests.AnnotationTestUtils;
import eu.europeana.annotation.tests.BaseAnnotationTest;

public class BaseTaggingTest extends BaseAnnotationTest {

	protected Annotation createAndValidateTag(String inputFile) throws Exception {
		return createTag(inputFile, true, true);
	}
	
	protected Annotation createTag(String inputFile, boolean validate, boolean indexOnCreate) throws Exception {

		log.debug("Input File: " + inputFile);

		String requestBody = AnnotationTestUtils.getJsonStringInput(inputFile);

		Annotation storedAnno = createTestAnnotation(inputFile, indexOnCreate, null);

		Annotation inputAnno = parseTag(requestBody);

		// validate the reflection of input in output!
		if (validate) {
		  AnnotationTestUtils.validateOutputAgainstInput(storedAnno, inputAnno);
		}

		return storedAnno;
	}

	protected Annotation parseTag(String jsonString) throws JsonParseException {
		MotivationTypes motivationType = MotivationTypes.TAGGING;
		return AnnotationTestUtils.parseAnnotation(jsonString, motivationType);
	}
	
}
