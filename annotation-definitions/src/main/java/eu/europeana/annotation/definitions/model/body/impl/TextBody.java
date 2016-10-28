package eu.europeana.annotation.definitions.model.body.impl;

import eu.europeana.annotation.definitions.model.vocabulary.BodyInternalTypes;

public class TextBody extends BaseBody {

	public TextBody(){
		super();
		setInternalType(BodyInternalTypes.TEXT.name());
		//setTypeEnum(BodyTypes.TEXT);
	}
}
