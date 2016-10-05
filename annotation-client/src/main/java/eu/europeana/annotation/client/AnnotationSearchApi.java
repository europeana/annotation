package eu.europeana.annotation.client;

import java.util.List;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.resource.impl.TagResource;

public interface AnnotationSearchApi {

	/**
	 * Search for annotations by the given text query.
	 * @param query
	 * @return
	 */
	public List<? extends Annotation> searchAnnotations(String query);
	
	/**
	 * Search for annotations by the given text query, row start position and rows limit. 	 
	 * @param query
	 * @param startOn
	 * @param limit
	 * @return
	 */
	public List<? extends Annotation> searchAnnotations(String query, String startOn, String limit, String field, String language);
	
	/**
	 * Search for tags by the given text query.
	 * @param query
	 * @return
	 */
	public List<? extends TagResource> searchTags(String query);

	/**
	 * Search for tags by the given text query, row start position and rows limit.
	 * @param query
	 * @param startOn
	 * @param limit
	 * @return
	 */
	public List<? extends TagResource> searchTags(String query, String startOn, String limit, String field, String language);

}
