package eu.europeana.annotation.tests.webanno.link;

import org.junit.jupiter.api.Test;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.tests.AnnotationTestUtils;

public class EdmRelationLinkingTest extends BaseLinkingTest {

	@Test
	public void createIsSimilarToLink() throws Exception {
		String requestBody = AnnotationTestUtils.getJsonStringInput(LINK_EDM_IS_SIMMILAR_TO);
		Annotation inputAnno = AnnotationTestUtils.parseAnnotation(requestBody, MotivationTypes.LINKING);
		Annotation storedAnno = createLink(requestBody);
		createdAnnotations.add(storedAnno.getIdentifier());
		//validate the reflection of input in output!
		AnnotationTestUtils.validateOutputAgainstInput(storedAnno, inputAnno);
	}
	
	@Test
	public void createIsSimilarToMinimalLink() throws Exception {
		String requestBody = AnnotationTestUtils.getJsonStringInput(LINK_EDM_IS_SIMMILAR_TO_MINIMAL);
		Annotation inputAnno = AnnotationTestUtils.parseAnnotation(requestBody, MotivationTypes.LINKING);
		Annotation storedAnno = createLink(requestBody);
		createdAnnotations.add(storedAnno.getIdentifier());
		//validate the reflection of input in output!
		AnnotationTestUtils.validateOutputAgainstInput(storedAnno, inputAnno);
	}
}
