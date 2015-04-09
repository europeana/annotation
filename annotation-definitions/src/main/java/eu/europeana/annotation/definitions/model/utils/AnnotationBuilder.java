package eu.europeana.annotation.definitions.model.utils;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.factory.impl.AnnotationObjectFactory;
import eu.europeana.annotation.definitions.model.impl.AbstractAnnotation;

public class AnnotationBuilder {

	public AbstractAnnotation copyIntoWebAnnotation(Annotation annotation) {

		AbstractAnnotation to = (AbstractAnnotation) AnnotationObjectFactory.getInstance()
				.createModelObjectInstance(annotation.getType());
		
		copyAnnotationId(annotation, to);
		copyAnnotationAttributes(annotation, to);

		return to;
	}

	public void copyAnnotationAttributes(Annotation annotation,
			Annotation to) {
		to.setType(annotation.getType());
		to.setAnnotatedAt(annotation.getAnnotatedAt());
		to.setAnnotatedBy(annotation.getAnnotatedBy());
		to.setBody(annotation.getBody());
		to.setTarget(annotation.getTarget());
		to.setMotivatedBy(annotation.getMotivatedBy());
		to.setSerializedAt(annotation.getSerializedAt());
		to.setSerializedBy(annotation.getSerializedBy());
		to.setStyledBy(annotation.getStyledBy());
		to.setDisabled(annotation.isDisabled());
		to.setSameAs(annotation.getSameAs());
	}

	/**
	 * Copy the annotationId atribute 
	 * @param annotation
	 * @param to
	 */
	void copyAnnotationId(Annotation annotation, Annotation to) {
		to.setAnnotationId(annotation.getAnnotationId());
	}

//	@SuppressWarnings("deprecation")
//	protected PersistentAnnotation copyIntoPersistantAnnotation(
//			Annotation annotation, String apiKey) {
//
//		PersistentAnnotationImpl to = (PersistentAnnotationImpl) (PersistentAnnotationFactory
//				.getInstance().createAnnotationInstance(annotation.getType()));
//
//		copyAnnotationAttributes(annotation, to);
//		return to;
//	}

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
