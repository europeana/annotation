package eu.europeana.annotation.mongo.model;

import eu.europeana.annotation.definitions.model.ObjectTag;
import eu.europeana.annotation.definitions.model.vocabulary.AnnotationTypes;

public class PersistentObjectLinkingImpl extends PersistentAnnotationImpl implements ObjectTag{
	
	
	public PersistentObjectLinkingImpl(){
		super();
		setInternalType(AnnotationTypes.OBJECT_LINKING.name());
	}

}
