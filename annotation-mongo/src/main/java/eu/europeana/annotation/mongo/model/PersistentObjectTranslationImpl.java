package eu.europeana.annotation.mongo.model;

import eu.europeana.annotation.definitions.model.ObjectTag;
import eu.europeana.annotation.definitions.model.vocabulary.AnnotationTypes;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

public class PersistentObjectTranslationImpl extends PersistentAnnotationImpl implements ObjectTag{
	
	
	public PersistentObjectTranslationImpl(){
		super();
		setInternalType(AnnotationTypes.OBJECT_TRANSLATION.name());
		setMotivation(MotivationTypes.TRANSLATING.getOaType());

	}
	

}
