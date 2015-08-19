package eu.europeana.annotation.web.service;

import java.util.List;
import java.util.Map;

import org.apache.stanbol.commons.exception.JsonParseException;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.Provider;
import eu.europeana.annotation.definitions.model.StatusLog;
import eu.europeana.annotation.definitions.model.concept.Concept;
import eu.europeana.annotation.definitions.model.resource.TagResource;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.solr.exceptions.AnnotationServiceException;
import eu.europeana.annotation.solr.exceptions.AnnotationStateException;
import eu.europeana.annotation.solr.exceptions.StatusLogServiceException;
import eu.europeana.annotation.solr.exceptions.TagServiceException;
import eu.europeana.annotation.web.exception.authorization.UserAuthorizationException;
import eu.europeana.annotation.web.exception.request.ParamValidationException;

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
	 * This method retrieves all not disabled annotations for given target.
	 * @param target
	 * @return the list of not disabled annotations
	 */
	public List<? extends Annotation> getAnnotationListByTarget (String target);
	
	/**
	 * This method retrieves all not disabled annotations for given resourceId.
	 * @param resourceId
	 * @return the list of not disabled annotations
	 */
	public List<? extends Annotation> getAnnotationListByResourceId (String resourceId);
	
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
	 * This method creates Europeana Annotation object from a JsonLd string.
	 * @param motivationType 
	 * @param annotationJsonLdStr
	 * @return Annotation object
	 * @throws JsonParseException 
	 */
	public Annotation parseAnnotationLd(MotivationTypes motivationType, String annotationJsonLdStr) throws JsonParseException;

	/**
	 * This method creates Provider object in database.
	 * @param newProvider
	 * @return Provider object
	 */
	public Provider storeProvider(Provider newProvider);

	/**
	 * @param idGeneration
	 * @return
	 */
	public List<? extends Provider> getProviderList(String idGeneration);
	
	/**
	 * @param idGeneration
	 * @param startOn
	 * @param limit
	 * @return
	 */
	public List<? extends Provider> getFilteredProviderList(String idGeneration, String startOn, String limit);		
	
	/**
	 * @param newProvider
	 * @return
	 */
	public Provider updateProvider(Provider newProvider);
	
	/**
	 * @param name
	 * @param idGeneration
	 */
	public void deleteProvider(String name, String idGeneration);

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

	/**
	 * @param newAnnotation
	 * @return
	 */
	public Annotation updateAnnotation(Annotation newAnnotation);
	
	/**
	 * This method deletes annotation by annotationId values.
	 * @param resourceId
	 * @param provider
	 * @param identifier
	 */
	public void deleteAnnotation(String provider, String identifier);
//	public void deleteAnnotation(String resourceId, String provider, Long identifier);

	/**
	 * This method forces reindexing of the annotation, which means 
	 * deletion in solr/annotation if exists and creation of new entry 
	 * in solr/annotation.
	 * @param resourceId
	 * @param provider
	 * @param identifier
	 */
	public void indexAnnotation(String provider, String identifier);
//	public void indexAnnotation(String resourceId, String provider, Long identifier);
	
	/**
	 * This method sets 'disable' field to true in database and removes the annotation 
	 * from the solr/annotation.
	 * @param resourceId
	 * @param provider
	 * @param identifier
	 * @return disabled Annotation
	 */
	public Annotation disableAnnotation(String provider, String identifier);
//	public Annotation disableAnnotation(String resourceId, String provider, Long identifier);
	
	/**
	 * This method returns annotation object for given annotationId that
	 * comprises provider and identifier.
	 * @param provider
	 * @param identifier
	 * @return annotation object
	 */
	public Annotation getAnnotationById(String provider, String identifier);
	
	/**
	 * This method returns annotation object for given annotationId that
	 * comprises provider and identifier.
	 * @param
	 * @return annotation object
	 */
	public Annotation getAnnotationById(AnnotationId annoId);
	
//	/**
//	 * This method returns annotation object for given annotationId that
//	 * comprises europeanaId, provider and identifier.
//	 * @param europeanaId
//	 * @param provider
//	 * @param identifier
//	 * @return annotation object
//	 */
//	public Annotation getAnnotationById(String europeanaId, String provider, Long identifier);
	
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
	//TODO: change parameters to integers
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
	 * Search for annotation status logs by the given text query, row start position and rows limit.
	 * @param query
	 * @param startOn
	 * @param limit
	 * @return
	 * @throws StatusLogServiceException 
	 */
	public List<? extends StatusLog> searchStatusLogs(String query, String startOn, String limit) throws StatusLogServiceException;

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
	
	/**
	 * Check whether annotation for given provider and identifier already exist in database.
	 */
	public boolean existsInDb(AnnotationId annoId); 
	
	/**
	 * Check whether given provider already exists in database.
	 */
	public boolean existsProviderInDb(Provider provider); 
	
	/**
	 * This method updates annotation status.
	 * @param annotation
	 * @return
	 */
	public Annotation updateAnnotationStatus(Annotation annotation);
	
	/**
	 * @param newConcept
	 * @return
	 */
	public Concept storeConcept(Concept newConcept);
		
	/**
	 * @param concept
	 * @return
	 */
	public Concept updateConcept(Concept concept);
	
	/**
	 * @param url
	 */
	public void deleteConcept(String url);
	
	/**
	 * @param url
	 * @return
	 */
	public Concept getConceptByUrl(String url);
	
	
	/**
	 * @param annotation
	 * @return
	 */
	public void logAnnotationStatusUpdate(String user, Annotation annotation);
	
	
	/**
	 * This method is checking the visibility of the annotation stored in the database.
	 * @param annotation The stored annotation object
	 * @param user The name of the current user
	 * @return annotation object if check was successful, exception otherwise
	 * @throws AnnotationStateException
	 */
	public void checkVisibility(Annotation annotation, String user) throws AnnotationStateException;

	/**
	 * this method validates the correctness of the provided annotation id (provider and identifier) 
	 * @param annoId
	 * @throws ParamValidationException 
	 */
	public void validateAnnotationId(AnnotationId annoId) throws ParamValidationException;

	public void validateApiKey(String wskey) throws UserAuthorizationException;


}
