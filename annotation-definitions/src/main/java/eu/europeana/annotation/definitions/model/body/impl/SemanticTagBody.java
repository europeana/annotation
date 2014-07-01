package eu.europeana.annotation.definitions.model.body.impl;

import eu.europeana.annotation.definitions.model.vocabulary.BodyTypes;

public class SemanticTagBody extends PlainTagBody{

	public SemanticTagBody(){
		super();
		setBodyTypeEnum(BodyTypes.SEMANTIC_TAG);
	}
}
