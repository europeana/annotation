package eu.europeana.annotation.solr.service;

import java.util.List;

import eu.europeana.annotation.solr.exceptions.AnnotationServiceException;
import eu.europeana.annotation.solr.model.internal.SolrAnnotation;

public interface SolrAnnotationService {

	
	/**
	 * This method stores a SolrAnnotation object in SOLR.
	 * @param solrAnnotation
	 */
	public void store(SolrAnnotation solrAnnotation) throws AnnotationServiceException ;
	
	
	/**
	 * This method updates a SolrAnnotation object in SOLR.
	 * @param solrAnnotation
	 */
	public void update(SolrAnnotation solrAnnotation) throws AnnotationServiceException ;
	
	
	/**
	 * This method removes a SolrAnnotation object from SOLR.
	 * @param solrAnnotation
	 */
	public void delete(SolrAnnotation solrAnnotation) throws AnnotationServiceException;
	
	
	/**
	 * Return all solr entries
	 * @return
	 * @throws AnnotationServiceException
	 */
	public List<? extends SolrAnnotation> getAll() throws AnnotationServiceException;

	/**
	 * This method retrieves available Annotations by searching the given term in all solr fields.
	 * @param fieldName The SOLR field name
	 * @return
	 * @throws AnnotationServiceException 
	 */
	public List<? extends SolrAnnotation> search(String searchTerm) throws AnnotationServiceException;
	
	
	/**
	 * This method retrieves available Annotations by searching the given id in all solr fields.
	 * @param id The SOLR id
	 * @return
	 * @throws AnnotationServiceException 
	 */
	public List<? extends SolrAnnotation> searchById(String id) throws AnnotationServiceException;

	/**
	 * This method retrieves available Annotations by searching all terms provided with the given object into the corresponding solr fields .
	 * @param fieldName The SOLR field name
	 * @return
	 * @throws AnnotationServiceException 
	 */
	public List<? extends SolrAnnotation> search(SolrAnnotation queryObject) throws AnnotationServiceException;
	
	
	/**
	 * This method retrieves available Annotations by searching the given term in label field.
	 * @param fieldName The SOLR field name
	 * @return
	 * @throws AnnotationServiceException 
	 */
	public List<? extends SolrAnnotation> searchByLabel(String searchTerm) throws AnnotationServiceException;
	

	/**
	 * This method retrieves available Annotations by searching for given map key and value.
	 * @param searchKey
	 * @param searchValue
	 * @return
	 * @throws AnnotationServiceException
	 */
	public List<? extends SolrAnnotation> searchByMapKey(String searchKey, String searchValue)  throws AnnotationServiceException;
		
}
