package eu.europeana.annotation.client;

import eu.europeana.annotation.definitions.model.Annotation;

public interface EuropeanaLdApi {

	public String createAnnotationLd(String provider, Long annotationNr, String europeanaLdString);
//	public Annotation createAnnotationLd(String provider, Long annotationNr, String europeanaLdString);

	public Annotation getAnnotationLd(String provider, Long annotationNr);

	public Annotation searchLd(String target, String resourceId);

}
