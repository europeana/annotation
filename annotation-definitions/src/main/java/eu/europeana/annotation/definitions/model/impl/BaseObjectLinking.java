package eu.europeana.annotation.definitions.model.impl;

import eu.europeana.annotation.definitions.model.ObjectTag;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

public class BaseObjectLinking extends AbstractAnnotation implements ObjectTag {

	public BaseObjectLinking() {
		super();
	}

	@Override
	public void setDefaultMotivation() {
		setMotivation(MotivationTypes.LINKING.getOaType());
	}
	
	
}
