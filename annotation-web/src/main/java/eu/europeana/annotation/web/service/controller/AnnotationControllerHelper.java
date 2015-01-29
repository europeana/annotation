package eu.europeana.annotation.web.service.controller;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.factory.impl.AnnotationObjectFactory;
import eu.europeana.annotation.definitions.model.impl.AbstractAnnotation;
import eu.europeana.annotation.mongo.factory.PersistentAnnotationFactory;
import eu.europeana.annotation.mongo.model.PersistentAnnotationImpl;
import eu.europeana.annotation.mongo.model.internal.PersistentAnnotation;

public class AnnotationControllerHelper {

	protected AbstractAnnotation copyIntoWebAnnotation(Annotation annotation,
			String apiKey) {

//		AbstractAnnotation to = (AbstractAnnotation) (AnnotationObjectFactory
//				.getInstance().createAnnotationInstance(annotation.getType()));
		AbstractAnnotation to = (AbstractAnnotation) AnnotationObjectFactory.getInstance()
				.createModelObjectInstance(annotation.getType());
		copyAnnotationAttributes(annotation, to);

		return to;
	}

	public AbstractAnnotation copyIntoWebAnnotation(Annotation annotation) {

		AbstractAnnotation to = (AbstractAnnotation) AnnotationObjectFactory.getInstance()
				.createModelObjectInstance(annotation.getType());
		copyAnnotationAttributes(annotation, to);

		return to;
	}

	private void copyAnnotationAttributes(Annotation annotation,
			Annotation to) {
		to.setType(annotation.getType());
		to.setAnnotationId(annotation.getAnnotationId());
		to.setAnnotatedAt(annotation.getAnnotatedAt());
		to.setAnnotatedBy(annotation.getAnnotatedBy());
		to.setBody(annotation.getBody());
		to.setTarget(annotation.getTarget());
		to.setMotivatedBy(annotation.getMotivatedBy());
		to.setSerializedAt(annotation.getSerializedAt());
		to.setSerializedBy(annotation.getSerializedBy());
		to.setStyledBy(annotation.getStyledBy());
	}

	@SuppressWarnings("deprecation")
	protected PersistentAnnotation copyIntoPersistantAnnotation(
			Annotation annotation, String apiKey) {

		PersistentAnnotationImpl to = (PersistentAnnotationImpl) (PersistentAnnotationFactory
				.getInstance().createAnnotationInstance(annotation.getType()));

		copyAnnotationAttributes(annotation, to);
		return to;
	}

	@SuppressWarnings("deprecation")
	public PersistentAnnotation copyIntoPersistantAnnotation(Annotation annotation) {

		PersistentAnnotationImpl to = (PersistentAnnotationImpl) (PersistentAnnotationFactory
				.getInstance().createAnnotationInstance(annotation.getType()));

		copyAnnotationAttributes(annotation, to);
		return to;
	}

//	private void copyShapeToPersistant(Annotation annotation,
//			PersistentAnnotationImpl to) {
//
//		List<Point> mongoShape = new ArrayList<Point>();
//		List<Point> webShape = ((ImageAnnotation) annotation).getShape();
//
//		MongoPointImpl mongoPoint;
//		for (Point point : webShape) {
//			mongoPoint = new MongoPointImpl();
//			mongoPoint.setPosX(point.getPosX());
//			mongoPoint.setPosY(point.getPosY());
//			mongoShape.add(mongoPoint);
//		}
//		((ImageAnnotation) to).setShape(mongoShape);
//	}
}
