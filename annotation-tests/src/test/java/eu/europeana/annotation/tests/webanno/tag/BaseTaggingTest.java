package eu.europeana.annotation.tests.webanno.tag;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.tests.AbstractIntegrationTest;
import eu.europeana.annotation.tests.utils.AnnotationTestUtils;

@SpringBootTest
@AutoConfigureMockMvc
public class BaseTaggingTest extends AbstractIntegrationTest {

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
