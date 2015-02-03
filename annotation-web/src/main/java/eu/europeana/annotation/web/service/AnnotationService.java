package eu.europeana.annotation.web.service;

import java.util.List;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.solr.model.internal.SolrTag;

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
	 */
	public List<? extends Annotation> getAnnotationByQuery(String europeanaId, String query);
	
	/**
	 * Search for tags by the given text query.
	 * @param resourceId
	 * @param query
	 * @return
	 */
	public List<? extends SolrTag> getTagByQuery(String resourceId, String query);

}
