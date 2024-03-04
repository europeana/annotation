package eu.europeana.annotation.definitions.model.impl;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

public class BaseCaptionAnnotation extends AbstractAnnotation implements Annotation {

	public BaseCaptionAnnotation(){
		super();
		motivation = MotivationTypes.CAPTIONING.getOaType();
	}

}
