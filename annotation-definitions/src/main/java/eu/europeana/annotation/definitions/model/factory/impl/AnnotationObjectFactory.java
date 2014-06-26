package eu.europeana.annotation.definitions.model.factory.impl;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.factory.AbstractAnnotationFactory;
import eu.europeana.annotation.definitions.model.impl.BaseImageAnnotation;
import eu.europeana.annotation.definitions.model.impl.BaseImageTag;
import eu.europeana.annotation.definitions.model.impl.BaseObjectTag;
import eu.europeana.annotation.definitions.model.vocabulary.AnnotationTypes;

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
		case OBJECT_TAG:
			ret = BaseObjectTag.class;
			break;
		case IMAGE_TAG:
			ret = BaseImageTag.class;
			break;
		case IMAGE_ANNOTATION:
			ret = BaseImageAnnotation.class;
			break;
		default:
			throw new RuntimeException(
					"The given type is not supported by the web model");
		}

		return ret;
	}
}
