package eu.europeana.annotation.web.service.controller;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.ImageAnnotation;
import eu.europeana.annotation.definitions.model.SemanticTag;
import eu.europeana.annotation.definitions.model.factory.impl.AnnotationObjectFactory;
import eu.europeana.annotation.definitions.model.impl.AbstractAnnotation;
import eu.europeana.annotation.mongo.factory.PersistentAnnotationFactory;
import eu.europeana.annotation.mongo.model.PersistentAnnotationImpl;
import eu.europeana.annotation.mongo.model.internal.PersistentAnnotation;

public class AnnotationControllerHelper {

	
	protected AbstractAnnotation copyIntoWebAnnotation(Annotation annotation,
			String apiKey) {
		
		AbstractAnnotation to = (AbstractAnnotation) (AnnotationObjectFactory.getInstance()
				.createAnnotationInstance(annotation.getType()));

		to.setAnnotationNr(annotation.getAnnotationNr());
		to.setCreator(annotation.getCreator());
		to.setEuropeanaId(annotation.getEuropeanaId());
		to.setType(annotation.getType());

		if (annotation instanceof SemanticTag) {
			((SemanticTag) to).setLabel(((SemanticTag) annotation).getLabel());
			((SemanticTag) to).setNamedEntityIdList(((SemanticTag) annotation)
					.getNamedEntityIdList());
			((SemanticTag) to)
					.setNamedEntityLabelList(((SemanticTag) annotation)
							.getNamedEntityLabelList());
		}

		if (annotation instanceof ImageAnnotation) {
			((ImageAnnotation) to).setImageUrl(((ImageAnnotation) annotation)
					.getImageUrl());
			((ImageAnnotation) to).setText(((ImageAnnotation) annotation)
					.getText());
			((ImageAnnotation) to).setShape(((ImageAnnotation) annotation)
					.getShape());
		}

		return to;
	}
	
	
	protected PersistentAnnotation copyIntoPersistantAnnotation(Annotation annotation,
			String apiKey) {
		
		PersistentAnnotationImpl to = (PersistentAnnotationImpl) (PersistentAnnotationFactory.getInstance()
				.createAnnotationInstance(annotation.getType()));

		//do not set annotatation number, it is automatically generated
		//to.setAnnotationNr(annotation.getAnnotationNr());
		to.setCreator(annotation.getCreator());
		to.setEuropeanaId(annotation.getEuropeanaId());
		to.setType(annotation.getType());

		if (annotation instanceof SemanticTag) {
			((SemanticTag) to).setLabel(((SemanticTag) annotation).getLabel());
			((SemanticTag) to).setNamedEntityIdList(((SemanticTag) annotation)
					.getNamedEntityIdList());
			((SemanticTag) to)
					.setNamedEntityLabelList(((SemanticTag) annotation)
							.getNamedEntityLabelList());
		}

		if (annotation instanceof ImageAnnotation) {
			((ImageAnnotation) to).setImageUrl(((ImageAnnotation) annotation)
					.getImageUrl());
			((ImageAnnotation) to).setText(((ImageAnnotation) annotation)
					.getText());
			((ImageAnnotation) to).setShape(((ImageAnnotation) annotation)
					.getShape());
		}

		return to;
	}
}
