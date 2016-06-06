package eu.europeana.annotation.definitions.model.body.impl;

import eu.europeana.annotation.definitions.model.vocabulary.BodyInternalTypes;

public class SemanticLinkBody extends BaseBody {

	public SemanticLinkBody(){
		super();
		setInternalType(BodyInternalTypes.SEMANTIC_LINK.name());
	}
}
