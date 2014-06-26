package eu.europeana.annotation.definitions.model.body.impl;

import eu.europeana.annotation.definitions.model.vocabulary.BodyTypes;

public class SemanticLinkBody extends BaseBody {

	public SemanticLinkBody(){
		super();
		setBodyType(BodyTypes.SEMANTIC_LINK);
	}
}
