package eu.europeana.annotation.mongo.model;

import eu.europeana.annotation.definitions.model.ObjectTag;
import eu.europeana.annotation.definitions.model.vocabulary.AnnotationTypes;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

public class PersistentCaptionImpl extends PersistentAnnotationImpl implements ObjectTag{
	
	
	public PersistentCaptionImpl(){
		super();
		setInternalType(AnnotationTypes.OBJECT_CAPTION.name());
		setMotivation(MotivationTypes.CAPTIONING.getOaType());

	}
	

}
