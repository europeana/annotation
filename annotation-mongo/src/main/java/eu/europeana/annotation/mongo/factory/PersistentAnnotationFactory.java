package eu.europeana.annotation.mongo.factory;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.factory.AbstractAnnotationFactory;
import eu.europeana.annotation.definitions.model.vocabulary.AnnotationTypes;
import eu.europeana.annotation.mongo.model.PersistentCaptionImpl;
import eu.europeana.annotation.mongo.model.PersistentObjectCommentImpl;
import eu.europeana.annotation.mongo.model.PersistentImageAnnotationImpl;
import eu.europeana.annotation.mongo.model.PersistentLinkForContributingImpl;
import eu.europeana.annotation.mongo.model.PersistentObjectDescribingImpl;
import eu.europeana.annotation.mongo.model.PersistentObjectLinkingImpl;
import eu.europeana.annotation.mongo.model.PersistentSubtitleImpl;
import eu.europeana.annotation.mongo.model.PersistentObjectTagImpl;
import eu.europeana.annotation.mongo.model.PersistentObjectTranscriptionImpl;

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
	    case OBJECT_COMMENT:
           ret = PersistentObjectCommentImpl.class;
           break;
		case OBJECT_TAG:
			ret = PersistentObjectTagImpl.class;
			break;
		case OBJECT_LINKING:
			ret = PersistentObjectLinkingImpl.class;
			break;
		case OBJECT_TRANSCRIPTION:
			ret = PersistentObjectTranscriptionImpl.class;
			break;
		case OBJECT_SUBTITLLE:
			ret = PersistentSubtitleImpl.class;
			break;
		case OBJECT_CAPTION:
			ret = PersistentCaptionImpl.class;
			break;
		case OBJECT_DESCRIBING:
			ret = PersistentObjectDescribingImpl.class;
			break;
	    case OBJECT_LINK_FOR_CONTRIBUTING:
            ret = PersistentLinkForContributingImpl.class;
            break;
		default:
			throw new RuntimeException(
					"The given type is not supported by the web model");
		}

		return ret;
	}
}
