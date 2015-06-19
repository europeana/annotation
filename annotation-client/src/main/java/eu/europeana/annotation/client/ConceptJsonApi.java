package eu.europeana.annotation.client;


public interface ConceptJsonApi {

	public String createConcept(String conceptJson);

	/**
	 * @param uri
	 * @return
	 */
	public String getConcept(String uri);
	
}
