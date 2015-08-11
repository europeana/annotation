package eu.europeana.annotation.definitions.model.target.impl;

import eu.europeana.annotation.definitions.model.target.Target;
import eu.europeana.annotation.definitions.model.vocabulary.TargetTypes;

public class ImageTarget extends BaseTarget implements Target {

	public ImageTarget(){
		super();
		setTypeEnum(TargetTypes.IMAGE);
	}

	
}
