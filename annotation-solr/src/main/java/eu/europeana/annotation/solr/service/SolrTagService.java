package eu.europeana.annotation.solr.service;

import java.util.List;

import eu.europeana.annotation.solr.exceptions.TagServiceException;
import eu.europeana.annotation.solr.model.internal.SolrTag;

public interface SolrTagService {

	
	/**
	 * This method stores a SolrTag object in SOLR.
	 * @param solrTag
	 */
	public void store(SolrTag solrTag) throws TagServiceException ;
	
	
	/**
	 * This method updates a SolrTag object in SOLR.
	 * @param solrTag
	 */
	public void update(SolrTag solrTag) throws TagServiceException ;
	
	
	/**
	 * This method removes a SolrTag object from SOLR.
	 * @param solrTag
	 */
	public void delete(SolrTag solrTag) throws TagServiceException;
	
	
	/**
	 * This method retrieves available tags by searching the given term in all solr fields.
	 * @param fieldName The SOLR field name
	 * @return
	 * @throws TagServiceException 
	 */
	public List<? extends SolrTag> search(String searchTerm) throws TagServiceException;
	
	
	/**
	 * This method retrieves available tags by searching all terms provided with the given object into the corresponding solr fields .
	 * @param fieldName The SOLR field name
	 * @return
	 * @throws TagServiceException 
	 */
	public List<? extends SolrTag> search(SolrTag queryObject) throws TagServiceException;
	
	
	/**
	 * This method retrieves available by searching the given term in label field.
	 * @param fieldName The SOLR field name
	 * @return
	 * @throws TagServiceException 
	 */
	public List<? extends SolrTag> searchByLabel(String searchTerm) throws TagServiceException;
	
	/**
	 * This method performs check whether given Tag is stored in Solr.
	 * If not the method stores the Tag.
	 * @param queryObject
	 * @throws TagServiceException
	 */
	public void findOrStore(SolrTag queryObject)  throws TagServiceException;
		
}
