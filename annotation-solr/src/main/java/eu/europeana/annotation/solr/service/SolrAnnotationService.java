package eu.europeana.annotation.solr.service;

import java.util.List;
import java.util.Map;

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
		
	/**
	 * This method retrieves available Annotations by searching for given field name and value.
	 * @param field The field name
	 * @param searchValue
	 * @return
	 * @throws AnnotationServiceException
	 */
	public List<? extends SolrAnnotation> searchByField(String field, String searchValue)  throws AnnotationServiceException;

	/**
	 * This method searches in all fields that are defined in schema for that purpose.
	 * @param id
	 * @return
	 * @throws AnnotationServiceException
	 */
	public List<? extends SolrAnnotation> searchByTerm(String id) throws AnnotationServiceException;
	
	/**
	 * This method supports faceting for Annotation.
	 * @param query
	 * @param qf
	 * @param queries
	 * @return
	 * @throws AnnotationServiceException
	 */
	Map<String, Integer> queryFacetSearch(String query, String[] qf, List<String> queries) throws AnnotationServiceException;
	
	/**
	 * This method retrieves available Annotations by searching for given term, row start position and rows limit.
	 * @param term
	 * @param startOn
	 * @param limit
	 * @return found rows
	 * @throws AnnotationServiceException
	 */
	public List<? extends SolrAnnotation> search(String term, String startOn, String limit) throws AnnotationServiceException;
	
}
