package eu.europeana.annotation.definitions.model.body.impl;

import eu.europeana.annotation.definitions.model.body.Body;
import eu.europeana.annotation.definitions.model.vocabulary.BodyInternalTypes;

public class FullTextResourceBody extends SpecificResourceBody implements Body {

	
	public FullTextResourceBody(){
		super();
		setInternalType(BodyInternalTypes.FULL_TEXT_RESOURCE.name());
	}

}
