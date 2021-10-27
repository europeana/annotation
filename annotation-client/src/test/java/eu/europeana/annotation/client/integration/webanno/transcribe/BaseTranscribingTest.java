package eu.europeana.annotation.client.integration.webanno.transcribe;

import org.apache.stanbol.commons.exception.JsonParseException;

import eu.europeana.annotation.client.integration.webanno.BaseWebAnnotationProtocolTest;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

public class BaseTranscribingTest extends BaseWebAnnotationProtocolTest {

	public static final String TRANSCRIPTION_WITH_RIGHTS = "/transcription/transcription-with-rights.json";
	public static final String TRANSCRIPTION_WITHOUT_RIGHTS = "/transcription/transcription-without-rights.json";
	public static final String TRANSCRIPTION_WITHOUT_LANG = "/transcription/transcription-without-language.json";
	public static final String TRANSCRIPTION_WITHOUT_VALUE = "/transcription/transcription-without-value.json";
	
	protected Annotation parseTranscription(String jsonString) throws JsonParseException {
		MotivationTypes motivationType = MotivationTypes.TRANSCRIBING;
		return parseAnnotation(jsonString, motivationType);		
	}

}
