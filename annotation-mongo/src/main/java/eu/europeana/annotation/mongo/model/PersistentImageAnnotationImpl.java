package eu.europeana.annotation.mongo.model;

import eu.europeana.annotation.definitions.model.ImageAnnotation;
import eu.europeana.annotation.definitions.model.vocabulary.AnnotationTypes;

public class PersistentImageAnnotationImpl extends PersistentAnnotationImpl implements ImageAnnotation{
	
	
	public PersistentImageAnnotationImpl(){
		super();
		setType(AnnotationTypes.IMAGE_ANNOTATION.name());
	}

	@Override
	public String getImageUrl() {
		
		return getTarget().getHttpUri();
	}

}
