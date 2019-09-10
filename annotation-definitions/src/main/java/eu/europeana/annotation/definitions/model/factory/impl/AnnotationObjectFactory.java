package eu.europeana.annotation.definitions.model.factory.impl;

import eu.europeana.annotation.definitions.exception.AnnotationInstantiationException;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.factory.AbstractModelObjectFactory;
import eu.europeana.annotation.definitions.model.impl.BaseDescribingAnnotation;
import eu.europeana.annotation.definitions.model.impl.BaseImageAnnotation;
import eu.europeana.annotation.definitions.model.impl.BaseImageTag;
import eu.europeana.annotation.definitions.model.impl.BaseObjectLinking;
import eu.europeana.annotation.definitions.model.impl.BaseObjectTag;
import eu.europeana.annotation.definitions.model.impl.BaseTranscriptionAnnotation;
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
		return createAnnotationInstance(motivationType);
	}

	public Annotation createAnnotationInstance(MotivationTypes motivationType) {
		AnnotationTypes annoType = null;
		switch (motivationType) {
		case TAGGING:
			// TODO when needed make differentiation between simple tagging
			// and semantic tagging
			// i.e. body is a string literal or an object
			//TODO: consider adding AnnotationTypes to MotivationTypes
			annoType = AnnotationTypes.OBJECT_TAG;
			break;
		case LINKING:
			// TODO when needed make differantiation between linking
			// europeana objects and linking europeana object with external
			// web pages
			// i.e. all targets are europeana objects or not ...
			annoType = AnnotationTypes.OBJECT_LINKING;
			break;
		case TRANSCRIBING:
			annoType = AnnotationTypes.OBJECT_TRANSCRIPTION;
			break;
		case DESCRIBING:
			annoType = AnnotationTypes.OBJECT_DESCRIBING;
			break;
		default:
			break;
		}

		if (annoType == null)
			throw new AnnotationInstantiationException(
					"Unsupported Annotation/Motivation Type:" + motivationType);

		Annotation anno = createObjectInstance(
				annoType);
		anno.setMotivation(motivationType.getOaType());
		return anno;
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
		case OBJECT_TRANSCRIPTION:
			ret = BaseTranscriptionAnnotation.class;
			break;
		case OBJECT_DESCRIBING:
			ret = BaseDescribingAnnotation.class;
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
