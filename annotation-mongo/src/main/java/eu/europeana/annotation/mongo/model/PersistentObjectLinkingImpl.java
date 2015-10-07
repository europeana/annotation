package eu.europeana.annotation.mongo.model;

import eu.europeana.annotation.definitions.model.ObjectTag;
import eu.europeana.annotation.definitions.model.vocabulary.AnnotationTypes;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

public class PersistentObjectLinkingImpl extends PersistentAnnotationImpl implements ObjectTag{
	
	
	public PersistentObjectLinkingImpl(){
		super();
		setInternalType(AnnotationTypes.OBJECT_LINKING.name());
		setDefaultMotivation();
	}

	@Override
	public void setDefaultMotivation() {
		setMotivation(MotivationTypes.LINKING.getOaType());
	}

}
