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
	
	public Annotation updateAnnotation(Annotation newAnnotation);
	
	public void deleteAnnotation(String resourceId,
			int annotationNr);
	
	public Annotation getAnnotationById(String europeanaId, int annotationNr);
	
	/**
	 * Search for annotations by the given text query.
	 * @param europeanaId
	 * @param query
	 * @return
	 * @throws AnnotationServiceException 
	 */
	public List<? extends Annotation> searchAnnotations(String query) throws AnnotationServiceException;
	
	/**
	 * Search for tags by the given text query.
	 * @param resourceId
	 * @param query
	 * @return
	 * @throws TagServiceException 
	 */
	public List<? extends TagResource> searchTags(String query) throws TagServiceException;

	/**
	 * This method is used for query faceting.
	 * @param qf
	 * @param queries
	 * @return
	 * @throws AnnotationServiceException 
	 */
	public Map<String, Integer> searchAnnotations(String [] qf, List<String> queries) throws AnnotationServiceException;
	
}
