package eu.europeana.annotation.definitions.model.impl;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

public class BaseHighlightingAnnotation extends AbstractAnnotation implements Annotation {

	public BaseHighlightingAnnotation(){
		super();
		motivation = MotivationTypes.HIGHLIGHTING.getOaType();
	}

}
