package eu.europeana.annotation.tests.webanno.transcribe;

import org.apache.stanbol.commons.exception.JsonParseException;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.tests.AnnotationTestUtils;
import eu.europeana.annotation.tests.BaseAnnotationTest;

public class BaseTranscribingTest extends BaseAnnotationTest {

	public static final String TRANSCRIPTION_WITH_RIGHTS = "/transcription/transcription-with-rights.json";
	public static final String TRANSCRIPTION_WITHOUT_RIGHTS = "/transcription/transcription-without-rights.json";
	public static final String TRANSCRIPTION_WITHOUT_LANG = "/transcription/transcription-without-language.json";
	public static final String TRANSCRIPTION_WITHOUT_VALUE = "/transcription/transcription-without-value.json";
	
	protected Annotation parseTranscription(String jsonString) throws JsonParseException {
		MotivationTypes motivationType = MotivationTypes.TRANSCRIBING;
		return AnnotationTestUtils.parseAnnotation(jsonString, motivationType);		
	}

}
