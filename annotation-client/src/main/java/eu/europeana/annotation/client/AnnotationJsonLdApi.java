package eu.europeana.annotation.client;


public interface AnnotationJsonLdApi {

//	public Annotation createAnnotation(String annotationJsonLdString);
	public String createAnnotationLd(String provider, Long annotationNr, String annotationJsonLdString);

	public String getAnnotationLd(String provider, Long annotationNr);

}
