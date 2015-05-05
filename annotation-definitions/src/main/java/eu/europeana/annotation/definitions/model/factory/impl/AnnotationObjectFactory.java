package eu.europeana.annotation.definitions.model.factory.impl;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.factory.AbstractModelObjectFactory;
import eu.europeana.annotation.definitions.model.impl.BaseImageAnnotation;
import eu.europeana.annotation.definitions.model.impl.BaseImageTag;
import eu.europeana.annotation.definitions.model.impl.BaseObjectLinking;
import eu.europeana.annotation.definitions.model.impl.BaseObjectTag;
import eu.europeana.annotation.definitions.model.vocabulary.AnnotationTypes;

public class AnnotationObjectFactory 
//    implements AnnotationPart
	//extends AbstractAnnotationFactory
	extends AbstractModelObjectFactory<Annotation, AnnotationTypes>{

	private static AnnotationObjectFactory singleton;

	// force singleton usage
	private AnnotationObjectFactory() {
	};

	public static AnnotationObjectFactory getInstance() {

		if (singleton == null) {
			synchronized (AnnotationObjectFactory.class) {
				singleton = new AnnotationObjectFactory();
			}
		}

		return singleton;

	}
	
	@Override
	public Annotation createObjectInstance(Enum<AnnotationTypes> modelObjectType) {
		Annotation res = super.createObjectInstance(modelObjectType);
		res.setInternalType(modelObjectType.name());
		return res;
	}
	
	@Override
	public Class<? extends Annotation> getClassForType(
			Enum<AnnotationTypes> modelType) {
		
		Class<? extends Annotation> ret = null;
		AnnotationTypes annotationType = AnnotationTypes.valueOf(modelType.name());
		
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
		case OBJECT_LINKING:
			ret = BaseObjectLinking.class;
			break;
		default:
			throw new RuntimeException(
					"The given type is not supported by the web model");
		}

		return ret;
	}


	@Override
	public Class<AnnotationTypes> getEnumClass() {
		return AnnotationTypes.class;
	}
}
