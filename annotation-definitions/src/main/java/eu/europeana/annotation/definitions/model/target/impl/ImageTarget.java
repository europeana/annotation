package eu.europeana.annotation.definitions.model.target.impl;

import eu.europeana.annotation.definitions.model.vocabulary.TargetTypes;

public class ImageTarget extends BaseTarget {

	public ImageTarget(){
		super();
		setTargetType(TargetTypes.IMAGE);
	}
}
