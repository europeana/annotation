package eu.europeana.annotation.mongo.model;

import eu.europeana.annotation.definitions.model.ObjectTag;
import eu.europeana.annotation.definitions.model.vocabulary.AnnotationTypes;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

public class PersistentObjectHighlightingImpl extends PersistentAnnotationImpl implements ObjectTag{
	
	
	public PersistentObjectHighlightingImpl(){
		super();
		setInternalType(AnnotationTypes.OBJECT_HIGHLIGHTING.name());
		setMotivation(MotivationTypes.HIGHLIGHTING.getOaType());
	}

}
