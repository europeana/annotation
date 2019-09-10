package eu.europeana.annotation.mongo.model;

import eu.europeana.annotation.definitions.model.ObjectTag;
import eu.europeana.annotation.definitions.model.vocabulary.AnnotationTypes;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

public class PersistentObjectDescribingImpl extends PersistentAnnotationImpl implements ObjectTag{
	
	
	public PersistentObjectDescribingImpl(){
		super();
		setInternalType(AnnotationTypes.OBJECT_DESCRIBING.name());
		setMotivation(MotivationTypes.DESCRIBING.getOaType());

	}
	

}
