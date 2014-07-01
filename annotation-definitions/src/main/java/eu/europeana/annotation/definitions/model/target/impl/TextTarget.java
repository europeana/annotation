package eu.europeana.annotation.definitions.model.target.impl;

import eu.europeana.annotation.definitions.model.vocabulary.TargetTypes;

public class TextTarget extends BaseTarget {

	public TextTarget(){
		super();
		setTargetTypeEnum(TargetTypes.TEXT);
	}
}
