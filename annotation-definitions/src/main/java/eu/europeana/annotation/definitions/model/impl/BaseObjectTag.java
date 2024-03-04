package eu.europeana.annotation.definitions.model.impl;

import eu.europeana.annotation.definitions.model.ObjectTag;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

public class BaseObjectTag extends AbstractAnnotation implements ObjectTag {

	public BaseObjectTag() {
		super();
		motivation = MotivationTypes.TAGGING.getOaType();
	}
	
}
