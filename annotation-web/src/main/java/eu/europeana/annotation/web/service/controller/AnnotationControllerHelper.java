package eu.europeana.annotation.web.service.controller;

import java.util.ArrayList;
import java.util.List;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.ImageAnnotation;
import eu.europeana.annotation.definitions.model.factory.impl.AnnotationObjectFactory;
import eu.europeana.annotation.definitions.model.impl.AbstractAnnotation;
import eu.europeana.annotation.definitions.model.selector.shape.Point;
import eu.europeana.annotation.mongo.factory.PersistentAnnotationFactory;
import eu.europeana.annotation.mongo.model.PersistentAnnotationImpl;
import eu.europeana.annotation.mongo.model.internal.PersistentAnnotation;
import eu.europeana.annotation.mongo.model.shape.MongoPointImpl;

public class AnnotationControllerHelper {

	protected AbstractAnnotation copyIntoWebAnnotation(Annotation annotation,
			String apiKey) {

		AbstractAnnotation to = (AbstractAnnotation) (AnnotationObjectFactory
				.getInstance().createAnnotationInstance(annotation.getType()));

//		to.setAnnotationNr(annotation.getAnnotationNr());
//		to.setCreator(annotation.getCreator());
//		to.setEuropeanaId(annotation.getResourceId());
//		to.setType(annotation.getType());
//
//		if (annotation instanceof SemanticTag) {
//			((SemanticTag) to).setLabel(((SemanticTag) annotation).getLabel());
//			((SemanticTag) to).setNamedEntityIdList(((SemanticTag) annotation)
//					.getNamedEntityIdList());
//			((SemanticTag) to)
//					.setNamedEntityLabelList(((SemanticTag) annotation)
//							.getNamedEntityLabelList());
//		}

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

	protected PersistentAnnotation copyIntoPersistantAnnotation(
			Annotation annotation, String apiKey) {

		PersistentAnnotationImpl to = (PersistentAnnotationImpl) (PersistentAnnotationFactory
				.getInstance().createAnnotationInstance(annotation.getType()));

//		// do not set annotatation number, it is automatically generated
//		// to.setAnnotationNr(annotation.getAnnotationNr());
//		to.setCreator(annotation.getCreator());
//
//		to.setEuropeanaId(annotation.getResourceId());
//		to.setType(annotation.getType());
//
//		if (annotation instanceof SemanticTag) {
//			((SemanticTag) to).setLabel(((SemanticTag) annotation).getLabel());
//			((SemanticTag) to).setNamedEntityIdList(((SemanticTag) annotation)
//					.getNamedEntityIdList());
//			((SemanticTag) to)
//					.setNamedEntityLabelList(((SemanticTag) annotation)
//							.getNamedEntityLabelList());
//		}

		if (annotation instanceof ImageAnnotation) {
			((ImageAnnotation) to).setImageUrl(((ImageAnnotation) annotation)
					.getImageUrl());
			((ImageAnnotation) to).setText(((ImageAnnotation) annotation)
					.getText());
			copyShapeToPersistant(annotation, to);
		}

		return to;
	}

	private void copyShapeToPersistant(Annotation annotation,
			PersistentAnnotationImpl to) {

		List<Point> mongoShape = new ArrayList<Point>();
		List<Point> webShape = ((ImageAnnotation) annotation).getShape();

		MongoPointImpl mongoPoint;
		for (Point point : webShape) {
			mongoPoint = new MongoPointImpl();
			mongoPoint.setPosX(point.getPosX());
			mongoPoint.setPosY(point.getPosY());
			mongoShape.add(mongoPoint);
		}
		((ImageAnnotation) to).setShape(mongoShape);
	}
}
