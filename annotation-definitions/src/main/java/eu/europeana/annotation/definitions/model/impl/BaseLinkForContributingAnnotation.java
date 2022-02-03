package eu.europeana.annotation.definitions.model.impl;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

public class BaseLinkForContributingAnnotation extends AbstractAnnotation implements Annotation {

	public BaseLinkForContributingAnnotation(){
		super();
		setMotivation(MotivationTypes.LINKFORCONTRIBUTING.getOaType());
	}	
}
