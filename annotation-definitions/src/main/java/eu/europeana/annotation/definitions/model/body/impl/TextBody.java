package eu.europeana.annotation.definitions.model.body.impl;

import eu.europeana.annotation.definitions.model.vocabulary.BodyTypes;

public class TextBody extends BaseBody {

	public TextBody(){
		super();
		setInternalType(BodyTypes.TEXT.name());
		//setTypeEnum(BodyTypes.TEXT);
	}
}
