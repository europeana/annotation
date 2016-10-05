package eu.europeana.annotation.mongo.factory;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.factory.AbstractAnnotationFactory;
import eu.europeana.annotation.definitions.model.vocabulary.AnnotationTypes;
import eu.europeana.annotation.mongo.model.PersistentImageAnnotationImpl;
import eu.europeana.annotation.mongo.model.PersistentObjectLinkingImpl;
import eu.europeana.annotation.mongo.model.PersistentObjectTagImpl;

@SuppressWarnings("deprecation")
public class PersistentAnnotationFactory extends AbstractAnnotationFactory{

	private static PersistentAnnotationFactory singleton;

	//force singleton usage
	private PersistentAnnotationFactory(){};
	
	public static PersistentAnnotationFactory getInstance() {

		if (singleton == null) {
			synchronized (PersistentAnnotationFactory.class) {
				singleton = new PersistentAnnotationFactory();
			}
		}

		return singleton;

	}

	public Class<? extends Annotation> getAnnotationClass(
			AnnotationTypes annotationType) {
		Class<? extends Annotation> ret = null;

		switch (annotationType) {
		case IMAGE_ANNOTATION:
			ret =  PersistentImageAnnotationImpl.class;
			break;
		case OBJECT_TAG:
			ret = PersistentObjectTagImpl.class;
			break;
		case OBJECT_LINKING:
			ret = PersistentObjectLinkingImpl.class;
			break;
		default:
			throw new RuntimeException(
					"The given type is not supported by the web model");
		}

		return ret;
	}
}
