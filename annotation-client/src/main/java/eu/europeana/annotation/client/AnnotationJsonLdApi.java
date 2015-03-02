package eu.europeana.annotation.client;

import eu.europeana.annotation.definitions.model.Annotation;

public interface AnnotationJsonLdApi {

	public Annotation createAnnotation(String annotationJsonLdString);

}
