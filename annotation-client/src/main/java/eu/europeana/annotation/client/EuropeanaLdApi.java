package eu.europeana.annotation.client;


public interface EuropeanaLdApi {

	public String createAnnotationLd(String provider, Long annotationNr, String europeanaLdString);
//	public Annotation createAnnotationLd(String provider, Long annotationNr, String europeanaLdString);

//	public Annotation getAnnotationLd(String provider, Long annotationNr);
	public String getAnnotationLd(String provider, Long annotationNr);

//	public Annotation searchLd(String target, String resourceId);
	public String searchLd(String target, String resourceId);

}
