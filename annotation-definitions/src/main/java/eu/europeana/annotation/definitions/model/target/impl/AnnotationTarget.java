package eu.europeana.annotation.definitions.model.target.impl;

import eu.europeana.annotation.definitions.model.vocabulary.TargetTypes;

public class AnnotationTarget extends BaseTarget {

	public AnnotationTarget(){
		super();
		setTypeEnum(TargetTypes.ANNOTATION);
	}
}
