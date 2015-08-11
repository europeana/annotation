package eu.europeana.annotation.definitions.model.factory;

import eu.europeana.annotation.definitions.exception.AnnotationInstantiationException;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.AnnotationTypes;

public abstract class AbstractBodyFactory {

	public Annotation createAnnotationInstance(String annotationType) {
		return createAnnotationInstance(AnnotationTypes.valueOf(annotationType));
	}

	public Annotation createAnnotationInstance(AnnotationTypes annotationType) {
	
		try {
			Annotation annotation = getAnnotationClass(annotationType).newInstance();
			annotation.setType(annotationType.name());
			return annotation;
		} catch (Exception e) {
			throw new AnnotationInstantiationException(
					annotationType.toString(), e);
		}
	}

	protected abstract Class<? extends Annotation> getAnnotationClass(
			AnnotationTypes annotationType);

	public Class<? extends Annotation> getAnnotationClass(String annotationType) {
		return getAnnotationClass(AnnotationTypes.valueOf(annotationType));
	}

}
