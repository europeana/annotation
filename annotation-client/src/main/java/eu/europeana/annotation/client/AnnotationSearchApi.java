package eu.europeana.annotation.client;

import eu.europeana.annotation.definitions.model.search.SearchProfiles;
import eu.europeana.annotation.definitions.model.search.result.AnnotationPage;

public interface AnnotationSearchApi {

	/**
	 * Search for annotations by the given text query.
	 * @param query
	 * @return
	 */
	public AnnotationPage searchAnnotations(String query);
	
	/**
	 * Search for annotations by the given text query, row start position and rows limit. 
	 * @param query Query string
	 * @param startOn Start record
	 * @param limit Limit of records in a page
	 * @param field Additional field
	 * @param language Language
	 * @param searchProfile Search profile (MINIMAL, STANDARD, etc.)
	 * @return Annotation page
	 */
	public AnnotationPage searchAnnotations(String query, String startOn, String limit, String field, String language);
	
	/**
	 * Search for annotations by the given text query using a search profile.
	 * @param query Query string
	 * @param startOn Start record
	 * @param limit Limit of records in a page
	 * @param field Additional field
	 * @param language Language
	 * @param searchProfile Search profile (MINIMAL, STANDARD, etc.)
	 * @param language The language passed to the Web API as a parameter
	 * @return Annotation page
	 */
	public AnnotationPage searchAnnotations(String query, String startOn, String limit, String field, String language, SearchProfiles searchProfile, String paramLanguage);
	
	/**
	 * Search for tags by the given text query.
	 * @param query
	 * @return
	 */
//	public List<? extends TagResource> searchTags(String query);

	/**
	 * Search for tags by the given text query, row start position and rows limit.
	 * @param query
	 * @param startOn
	 * @param limit
	 * @return
	 */
//	public List<? extends TagResource> searchTags(String query, String startOn, String limit, String field, String language);

	/**
	 * @param query
	 * @param searchProfile
	 * @param language The language passed to the Web API as a parameter
	 * @return Annotation page
	 */
	AnnotationPage searchAnnotations(String query, SearchProfiles searchProfile, String language);

	AnnotationPage searchAnnotations(String query, String qf, String sort, String sortOrder, String page,
		String pageSize, SearchProfiles searchProfile, String language);




}
