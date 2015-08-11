package eu.europeana.annotation.definitions.model.factory.impl;

import eu.europeana.annotation.definitions.exception.AnnotationInstantiationException;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.factory.AbstractModelObjectFactory;
import eu.europeana.annotation.definitions.model.impl.BaseImageAnnotation;
import eu.europeana.annotation.definitions.model.impl.BaseImageTag;
import eu.europeana.annotation.definitions.model.impl.BaseObjectLinking;
import eu.europeana.annotation.definitions.model.impl.BaseObjectTag;
import eu.europeana.annotation.definitions.model.vocabulary.AnnotationTypes;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

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
	
	public Annotation createAnnotationInstance(String motivation) {
		MotivationTypes motivationType = MotivationTypes
				.getType(motivation);
		AnnotationTypes annoType = null;
		switch (motivationType) {
		case TAGGING:
			// TODO when needed make differentiation between simple tagging
			// and semantic tagging
			// i.e. body is a string literal or an object
			annoType = AnnotationTypes.OBJECT_TAG;
			break;
		case LINKING:
			// TODO when needed make differantiation between linking
			// europeana objects and linking europeana object with external
			// web pages
			// i.e. all targets are europeana objects or not ...
			annoType = AnnotationTypes.OBJECT_LINKING;
			break;
		default:
			break;
		}

		if (annoType == null)
			throw new AnnotationInstantiationException(
					"Unsupported Annotation/Motivation Type:" + motivation);

		return createObjectInstance(
				annoType);
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
