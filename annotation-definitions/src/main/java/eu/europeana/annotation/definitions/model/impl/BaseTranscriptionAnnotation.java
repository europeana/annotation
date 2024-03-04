package eu.europeana.annotation.definitions.model.impl;

import eu.europeana.annotation.definitions.model.TranscriptionAnnotation;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

public class BaseTranscriptionAnnotation extends AbstractAnnotation implements TranscriptionAnnotation {

	public BaseTranscriptionAnnotation(){
		super();
		motivation = MotivationTypes.TRANSCRIBING.getOaType();

	} 
	
}
