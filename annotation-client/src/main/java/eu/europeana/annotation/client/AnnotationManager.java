package eu.europeana.annotation.client;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.ImageAnnotation;

public interface AnnotationManager {

	public Annotation createAnnotation(Annotation annotation);

	public ImageAnnotation createImageAnnotation(ImageAnnotation annotation);

	//public SemanticTag createSemanticTag(SemanticTag annotation);

}
