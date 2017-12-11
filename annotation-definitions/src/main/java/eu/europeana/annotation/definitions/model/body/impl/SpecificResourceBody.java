package eu.europeana.annotation.definitions.model.body.impl;

import eu.europeana.annotation.definitions.model.body.Body;
import eu.europeana.annotation.definitions.model.vocabulary.BodyInternalTypes;

public class SpecificResourceBody extends BaseBody implements Body {

	
	public SpecificResourceBody(){
		super();
		setInternalType(BodyInternalTypes.SPECIFIC_RESOURCE.name());
	}

}
