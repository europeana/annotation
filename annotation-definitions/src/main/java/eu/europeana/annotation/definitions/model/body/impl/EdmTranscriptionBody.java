package eu.europeana.annotation.definitions.model.body.impl;

import eu.europeana.annotation.definitions.model.body.TranscriptionBody;
import eu.europeana.annotation.definitions.model.vocabulary.BodyInternalTypes;

public class EdmTranscriptionBody extends BaseBody implements TranscriptionBody {

	
	public EdmTranscriptionBody(){
		super();
		setInternalType(BodyInternalTypes.TRANSCRIBING.name());
	}

}
