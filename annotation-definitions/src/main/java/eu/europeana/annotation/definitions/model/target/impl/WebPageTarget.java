package eu.europeana.annotation.definitions.model.target.impl;

import eu.europeana.annotation.definitions.model.vocabulary.TargetTypes;

public class WebPageTarget extends BaseTarget {

	public WebPageTarget(){
		super();
		setTypeEnum(TargetTypes.WEB_PAGE);
	}
}
