package eu.europeana.annotation.client;


public interface EuropeanaLdApi {

	public String createAnnotationLd(String motivation, String provider, Long annotationNr, String europeanaLdString);

	/**
	 * This method supports apikey parameter
	 * @param motivation
	 * @param provider
	 * @param annotationNr
	 * @param europeanaLdStr
	 * @param apikey
	 * @return
	 */
	public String createAnnotationLd(String motivation, String provider, Long annotationNr, String europeanaLdStr, String apikey);
	
	public String getAnnotationLd(String provider, Long annotationNr);

	/**
	 * This method supports apikey parameter
	 * @param provider
	 * @param annotationNr
	 * @param apikey
	 * @return
	 */
	public String getAnnotationLd(String provider, Long annotationNr, String apikey);

	public String searchLd(String target, String resourceId);

}
