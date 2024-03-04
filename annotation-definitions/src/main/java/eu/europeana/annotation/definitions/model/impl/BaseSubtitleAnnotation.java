package eu.europeana.annotation.definitions.model.impl;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

public class BaseSubtitleAnnotation extends AbstractAnnotation implements Annotation {

	public BaseSubtitleAnnotation(){
		super();
		motivation = MotivationTypes.SUBTITLING.getOaType();

	} 
	
}
