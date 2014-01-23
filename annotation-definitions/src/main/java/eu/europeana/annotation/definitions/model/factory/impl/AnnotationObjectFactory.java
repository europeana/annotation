package eu.europeana.annotation.definitions.model.factory.impl;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.AnnotationTypes;
import eu.europeana.annotation.definitions.model.factory.AbstractAnnotationFactory;
import eu.europeana.annotation.definitions.model.impl.ImageAnnotationImpl;
import eu.europeana.annotation.definitions.model.impl.SemanticTagImpl;

public class AnnotationObjectFactory extends AbstractAnnotationFactory{

	private static AbstractAnnotationFactory singleton;

	//force singleton usage
	private AnnotationObjectFactory(){};
	
	public static AbstractAnnotationFactory getInstance() {

		if (singleton == null) {
			synchronized (AnnotationObjectFactory.class) {
				singleton = new AnnotationObjectFactory();
			}
		}

		return singleton;

	}

	public Class<? extends Annotation> getAnnotationClass(
			AnnotationTypes annotationType) {
		Class<? extends Annotation> ret = null;

		switch (annotationType) {
		case IMAGE_ANNOTATION:
			ret = ImageAnnotationImpl.class;
			break;
		case SEMANTIC_TAG:
			ret = SemanticTagImpl.class;
			break;
		default:
			throw new RuntimeException(
					"The given type is not supported by the web model");
		}

		return ret;
	}
}
