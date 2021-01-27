package eu.europeana.annotation.mongo.model;

import eu.europeana.annotation.definitions.model.ObjectTag;
import eu.europeana.annotation.definitions.model.vocabulary.AnnotationTypes;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

public class PersistentObjectSubtitleImpl extends PersistentAnnotationImpl implements ObjectTag{
	
	
	public PersistentObjectSubtitleImpl(){
		super();
		setInternalType(AnnotationTypes.OBJECT_SUBTITLLE.name());
		setMotivation(MotivationTypes.SUBTITLING.getOaType());

	}
	

}
