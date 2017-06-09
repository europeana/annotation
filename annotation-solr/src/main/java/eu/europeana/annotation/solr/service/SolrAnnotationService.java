package eu.europeana.annotation.solr.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServerException;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.moderation.Summary;
import eu.europeana.annotation.definitions.model.search.Query;
import eu.europeana.annotation.definitions.model.search.SearchProfiles;
import eu.europeana.annotation.definitions.model.search.result.ResultSet;
import eu.europeana.annotation.definitions.model.view.AnnotationView;
import eu.europeana.annotation.solr.exceptions.AnnotationServiceException;

public interface SolrAnnotationService {

	
	/**
	 * This method stores a SolrAnnotation object in SOLR.
	 * @param anno
	 * @return 
	 */

	public void store(Annotation anno) throws AnnotationServiceException ;


	/**
	 * This method stores a list of SolrAnnotation objects in SOLR.
	 * @param anno
	 * @throws IOException 
	 * @throws SolrServerException 
	 */
	public void store(List<? extends Annotation> annos) throws AnnotationServiceException, SolrServerException, IOException ;
	
	/**
	 * This method stores a SolrAnnotation object in SOLR.
	 * @param anno
	 * @param doCommit commit
	 */
	public void store(Annotation anno, boolean doCommit) throws AnnotationServiceException ;	
	
	/**
	 * This method updates a SolrAnnotation object in SOLR.
	 * @param anno
	 */
	public void update(Annotation anno) throws AnnotationServiceException ;
		
	/**
	 * @param anno
	 * @param summary
	 * @throws AnnotationServiceException
	 */
	public boolean update(Annotation anno, Summary summary) throws AnnotationServiceException;

	/**
	 * This method removes a SolrAnnotation object from SOLR.
	 * @param solrAnnotation
	 */
	public void delete(AnnotationId annoId) throws AnnotationServiceException;
	
	/**
	 * This method removes a SolrAnnotation object from SOLR.
	 * @param solrAnnotation
	 */
	public void delete(String annoUrl) throws AnnotationServiceException;
	
	/**
	 * Return all solr entries
	 * @return
	 * @throws AnnotationServiceException
	 */
	public ResultSet<? extends AnnotationView> getAll() throws AnnotationServiceException;

	/**
	 * This method retrieves available Annotations by searching the given term in all solr fields.
	 * @param fieldName The SOLR field name
	 * @return
	 * @throws AnnotationServiceException 
	 */
	public ResultSet<? extends AnnotationView> search(String searchTerm) throws AnnotationServiceException;
	
	
	/**
	 * This method retrieves available Annotations by searching the given id in all solr fields.
	 * @param id The SOLR id
	 * @return
	 * @throws AnnotationServiceException 
	 */
	public AnnotationView searchById(String id) throws AnnotationServiceException;

	
	/**
	 * This method retrieves available Annotations by searching all terms provided with the given object into the corresponding solr fields .
	 * @param profile see {@link SearchProfiles}
	 * @param fieldName The SOLR field name
	 * @return
	 * @throws AnnotationServiceException 
	 */
	public ResultSet<? extends AnnotationView> search(Query searchQuery) throws AnnotationServiceException;
	
	
	/**
	 * This method retrieves available Annotations by searching the given term in label field.
	 * @param profile see {@link SearchProfiles}
	 * @param fieldName The SOLR field name
	 * @return
	 * @throws AnnotationServiceException 
	 */
	public ResultSet<? extends AnnotationView> searchByLabel(String searchTerm) throws AnnotationServiceException;
	

	/**
	 * This method retrieves available Annotations by searching for given map key and value.
	 * @deprecated TODO:rename & redirect
	
	 * @param searchKey
	 * @param searchValue
	 * @param profile see {@link SearchProfiles}
	 * @return
	 * @throws AnnotationServiceException
	 */
	public ResultSet<? extends AnnotationView> searchByMapKey(String searchKey, String searchValue)  throws AnnotationServiceException;
		
	/**
	 * This method retrieves available Annotations by searching for given field name and value.
	 * @param field The field name
	 * @param searchValue
	 * @param profile see {@link SearchProfiles}
	 * @return
	 * @throws AnnotationServiceException
	 */
	public ResultSet<? extends AnnotationView> searchByField(String field, String searchValue)  throws AnnotationServiceException;

	/**
	 * This method searches in all fields that are defined in schema for that purpose.
	 * @param text
	 * @return
	 * @throws AnnotationServiceException
	 */
	public ResultSet<? extends AnnotationView> searchByTerm(String text) throws AnnotationServiceException;
	
	/**
	 * This method supports faceting for Annotation.
	 * @param query
	 * @param qf
	 * @param queries
	 * @return
	 * @throws AnnotationServiceException
	 * @deprecated update name and parameter names accordingly
	 */
	Map<String, Integer> queryFacetSearch(String query, String[] qf, List<String> queries) throws AnnotationServiceException;
	
	/**
	 * This method retrieves available Annotations by searching for given term, row start position and rows limit.
	 * @param term
	 * @param start
	 * @param rows
	 * @param profile see {@link SearchProfiles}
	 * @return found rows
	 * @throws AnnotationServiceException
	 */
	public ResultSet<? extends AnnotationView> search(String term, String start, String rows) throws AnnotationServiceException;
	
}
