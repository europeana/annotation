package eu.europeana.annotation.definitions.model.utils;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.factory.impl.AnnotationObjectFactory;
import eu.europeana.annotation.definitions.model.impl.AbstractAnnotation;

public class AnnotationBuilder {

	public AbstractAnnotation copyIntoWebAnnotation(Annotation annotation) {

		String internalType = annotation.getInternalType();
		if (internalType == null)
			internalType = annotation.getType();
		AbstractAnnotation to = (AbstractAnnotation) AnnotationObjectFactory.getInstance()
				.createModelObjectInstance(internalType);
		
		copyAnnotationId(annotation, to);
		copyAnnotationAttributes(annotation, to);

		return to;
	}

	public void copyAnnotationAttributes(Annotation annotation,
			Annotation to) {
		to.setType(annotation.getType());
		to.setInternalType(annotation.getInternalType());
		to.setCreated(annotation.getCreated());
		to.setCreator(annotation.getCreator());
		to.setBody(annotation.getBody());
		to.setTarget(annotation.getTarget());
		to.setMotivation(annotation.getMotivation());
		to.setGenerated(annotation.getGenerated());
		to.setGenerator(annotation.getGenerator());
		to.setStyledBy(annotation.getStyledBy());
		to.setDisabled(annotation.getDisabled());
		to.setSameAs(annotation.getSameAs());
		to.setEquivalentTo(annotation.getEquivalentTo());
		to.setStatus(annotation.getStatus());
		to.setLastUpdate(annotation.getLastUpdate());
		to.setCanonical(annotation.getCanonical());
		to.setVia(annotation.getVia());
	}

	/**
	 * Copy the annotationId atribute 
	 * @param annotation
	 * @param to
	 */
	public void copyAnnotationId(Annotation annotation, Annotation to) {
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
