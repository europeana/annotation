package eu.europeana.annotation.web.service;

import java.util.List;
import java.util.Map;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.resource.TagResource;
import eu.europeana.annotation.solr.exceptions.AnnotationServiceException;
import eu.europeana.annotation.solr.exceptions.TagServiceException;

public interface AnnotationService {

	public String getComponentName();
	
	/**
	 * This method retrieves all not disabled annotations.
	 * @param resourceId
	 * @return the list of not disabled annotations
	 */
	public List<? extends Annotation> getAnnotationList (String resourceId);
	
	/**
	 * This method retrieves all not disabled annotations for given provider.
	 * @param resourceId
	 * @param provider
	 * @return the list of not disabled annotations
	 */
	public List<? extends Annotation> getAnnotationListByProvider (String resourceId, String provider);
	
	/**
	 * This method retrieves annotations applying filters.
	 * @param resourceId
	 * @param startOn
	 * @param limit
	 * @param isDisabled
	 * @return the list of annotations
	 */
	public List<? extends Annotation> getFilteredAnnotationList (
			String resourceId, String startOn, String limit, boolean isDisabled);
	
	/**
	 * This method creates Annotation object from a JsonLd string.
	 * @param annotationJsonLdStr
	 * @return Annotation object
	 */
	public Annotation parseAnnotation(String annotationJsonLdStr);

	/**
	 * This method stores Annotation object in database and in Solr.
	 * @param annotation
	 * @return Annotation object
	 */
	public Annotation storeAnnotation(Annotation annotation);

	/**
	 * This method stores Annotation object in database and in Solr if 'indexing' is true.
	 * @param annotation
	 * @param indexing
	 * @return Annotation object
	 */
	public Annotation storeAnnotation(Annotation annotation, boolean indexing);

	public Annotation updateAnnotation(Annotation newAnnotation);
	
	/**
	 * This method deletes annotation by annotationId values.
	 * @param resourceId
	 * @param provider
	 * @param annotationNr
	 */
	public void deleteAnnotation(String resourceId, String provider, int annotationNr);

	/**
	 * This method forces reindexing of the annotation, which means 
	 * deletion in solr/annotation if exists and creation of new entry 
	 * in solr/annotation.
	 * @param resourceId
	 * @param provider
	 * @param annotationNr
	 */
	public void indexAnnotation(String resourceId, String provider, int annotationNr);
	
	/**
	 * This method sets 'disable' field to true in database and removes the annotation 
	 * from the solr/annotation.
	 * @param resourceId
	 * @param provider
	 * @param annotationNr
	 * @return disabled Annotation
	 */
	public Annotation disableAnnotation(String resourceId, String provider, int annotationNr);
	
	/**
	 * This method returns annotation object for given annotationId that
	 * comprises europeanaId, provider and annotationNr.
	 * @param europeanaId
	 * @param provider
	 * @param annotationNr
	 * @return annotation object
	 */
	public Annotation getAnnotationById(String europeanaId, String provider, int annotationNr);
	
	/**
	 * Search for annotations by the given text query.
	 * @param query
	 * @return
	 * @throws AnnotationServiceException 
	 */
	public List<? extends Annotation> searchAnnotations(String query) throws AnnotationServiceException;
	
	/**
	 * Search for annotations by the given text query, row start position and rows limit. 	 
	 * @param query
	 * @param startOn
	 * @param limit
	 * @return
	 * @throws AnnotationServiceException 
	 */
	public List<? extends Annotation> searchAnnotations(String query, String startOn, String limit) 
			throws AnnotationServiceException;
	
	/**
	 * Search for tags by the given text query.
	 * @param query
	 * @return
	 * @throws TagServiceException 
	 */
	public List<? extends TagResource> searchTags(String query) throws TagServiceException;

	/**
	 * Search for tags by the given text query, row start position and rows limit.
	 * @param query
	 * @param startOn
	 * @param limit
	 * @return
	 * @throws TagServiceException 
	 */
	public List<? extends TagResource> searchTags(String query, String startOn, String limit) throws TagServiceException;

	/**
	 * This method is used for query faceting.
	 * @param qf
	 * @param queries
	 * @return
	 * @throws AnnotationServiceException 
	 */
	public Map<String, Integer> searchAnnotations(String [] qf, List<String> queries) throws AnnotationServiceException;
	
	/**
	 * This method removes Tag object from database and Solr but only when no annotation uses the tag.
	 * @param tagId
	 */
	public void deleteTag(String tagId);
}
