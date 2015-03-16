package eu.europeana.annotation.web.service;

import java.util.List;
import java.util.Map;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.resource.TagResource;
import eu.europeana.annotation.solr.exceptions.AnnotationServiceException;
import eu.europeana.annotation.solr.exceptions.TagServiceException;

public interface AnnotationService {

	public String getComponentName();
	
	public List<? extends Annotation> getAnnotationList(String resourceId);
	
	public Annotation createAnnotation(Annotation newAnnotation);
	
	/**
	 * This method creates Annotation object from a JsonLd string.
	 * @param annotationJsonLdStr
	 * @return Annotation object
	 */
	public Annotation createAnnotation(String annotationJsonLdStr);

	public Annotation updateAnnotation(Annotation newAnnotation);
	
	public void deleteAnnotation(String resourceId, int annotationNr);

	/**
	 * This method forces reindexing of the annotation, which means 
	 * deletion in solr/annotation if exists and creation of new entry 
	 * in solr/annotation.
	 * @param resourceId
	 * @param annotationNr
	 */
	public void indexAnnotation(String resourceId, int annotationNr);
	
	/**
	 * This method sets 'disable' field to true in database and removes the annotation 
	 * from the solr/annotation.
	 * @param resourceId
	 * @param annotationNr
	 */
	public void disableAnnotation(String resourceId, int annotationNr);
	
	public Annotation getAnnotationById(String europeanaId, int annotationNr);
	
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
