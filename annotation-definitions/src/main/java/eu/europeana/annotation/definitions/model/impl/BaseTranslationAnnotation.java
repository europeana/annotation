package eu.europeana.annotation.definitions.model.impl;

import eu.europeana.annotation.definitions.model.TranscriptionAnnotation;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

public class BaseTranslationAnnotation extends AbstractAnnotation implements TranscriptionAnnotation {

	public BaseTranslationAnnotation(){
		super();
		setMotivation(MotivationTypes.TRANSLATING.getOaType());
	} 
	
}
