package eu.europeana.annotation.solr.service.impl;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;

import eu.europeana.annotation.solr.exceptions.TagServiceException;
import eu.europeana.annotation.solr.model.internal.SolrAnnotationConst;
import eu.europeana.annotation.solr.model.internal.SolrTag;
import eu.europeana.annotation.solr.model.internal.SolrTagImpl;
import eu.europeana.annotation.solr.service.SolrTagService;

public class SolrTagServiceImpl implements SolrTagService {

	SolrServer solrServer;

	private static final Logger log = Logger.getLogger(SolrTagServiceImpl.class);
	
	public void setSolrServer(SolrServer solrServer) {
		this.solrServer = solrServer;
	}

	@Override
	public void store(SolrTag solrTag)  throws TagServiceException {
	    try {
	    	log.info("store: " + solrTag.toString());
	        UpdateResponse rsp = solrServer.addBean(solrTag);
	        log.info("store response: " + rsp.toString());
	        solrServer.commit();
	    } catch (SolrServerException ex) {
			throw new TagServiceException("Unexpected IO exception occured when storing tags for: " + solrTag.getId(), ex);
	    } catch (IOException ex) {
			throw new TagServiceException("Unexpected IO exception occured when storing tags for: " + solrTag.getId(), ex);
	    }		
	}

	@Override
	public List<? extends SolrTag> search(String term) throws TagServiceException {
			
		List<? extends SolrTag> res = null;
		
	    /**
	     * Construct a SolrQuery 
	     */
	    SolrQuery query = new  SolrQuery(term);
	    
	    /**
	     * Query the server 
	     */
	    try {
	    	QueryResponse rsp =  solrServer.query( query );
	    	res = rsp.getBeans(SolrTagImpl.class);
		} catch (SolrServerException e) {
			throw new TagServiceException("Unexpected exception occured when searching tags for: " + term, e);
        }
	    
	    return res;
	}

	public List<? extends SolrTag> searchAll() throws TagServiceException {
			
		List<? extends SolrTag> res = null;
		
	    res = search(SolrAnnotationConst.ALL_SOLR_ENTRIES);
	    
	    return res;
	}

	@Override
	public void findOrStore(SolrTag queryObject)  throws TagServiceException {
		List<? extends SolrTag> tagList = search(queryObject);
		if (tagList.size() == 0) {
			store(queryObject);
		}
	}

	@Override
	public List<? extends SolrTag> search(SolrTag queryObject)  throws TagServiceException {

		List<? extends SolrTag> res = null;
		
	    /**
	     * Construct a SolrQuery 
	     */
	    SolrQuery query = new SolrQuery();
	    log.info("label: " + queryObject.getLabel());
	    log.info("creator: " + queryObject.getCreator());
	    log.info("language: " + queryObject.getLanguage());
//	    query.set(SolrAnnotationConst.LABEL,queryObject.getLabel());
//	    query.setQuery("\"" + SolrAnnotationConst.LABEL + ":" + queryObject.getLabel() + "\"");
//	    query.setQuery(SolrAnnotationConst.LABEL + ":" + queryObject.getLabel());

//	    query.setParam(SolrAnnotationConst.LABEL,queryObject.getLabel());
//	    query.setParam(SolrAnnotationConst.CREATOR,queryObject.getCreator());
//	    query.setParam(SolrAnnotationConst.LANGUAGE,queryObject.getLanguage());
//	    query.setQuery("EN");
//	    query.setQuery(queryObject.getLanguage());
//	    query.setQuery(queryObject.getCreator());
	    ///query.setQuery(queryObject.getLabel());
//	    query.addFilterQuery("language:EN");
//	    query.setFields("language");
	    ///query.setFields(SolrAnnotationConst.LABEL);
//	    query.setFields(SolrAnnotationConst.CREATOR);
//	    query.setFields(SolrAnnotationConst.LANGUAGE);
	    
	    String queryStr = "";

	    if (queryObject.getLabel() != null) {
	    	queryStr = queryStr + SolrAnnotationConst.LABEL + ":" + queryObject.getLabel();
	    }
	    if (queryObject.getLanguage() != null) {
	    	queryStr = queryStr + " AND " + SolrAnnotationConst.LANGUAGE + ":" + queryObject.getLanguage();
	    }
	    if (queryObject.getCreator() != null) {
	    	queryStr = queryStr + " AND " + SolrAnnotationConst.CREATOR + ":" + queryObject.getCreator();
	    }
	    log.info("queryStr: " + queryStr);
	    query.setQuery(queryStr);
	    
	    query.setFields(
	    		SolrAnnotationConst.LABEL, 
	    		SolrAnnotationConst.LANGUAGE, 
	    		SolrAnnotationConst.CREATOR
	    		);
	    
	    log.info("search obj: " + queryObject);
	    
	    /**
	     * Query the server 
	     */
	    try {
	    	QueryResponse rsp = solrServer.query( query );
	    	res = rsp.getBeans(SolrTagImpl.class);
	    	log.info("search obj res size: " + res.size());
		} catch (SolrServerException e) {
			throw new TagServiceException("Unexpected exception occured when searching tags for solrTag: " + 
					queryObject.toString(), e);
        }
	    
	    return res;
	}

	@Override
	public List<? extends SolrTag> searchByLabel(String searchTerm)  throws TagServiceException {
		
		List<? extends SolrTag> res = null;
		
	    /**
	     * Construct a SolrQuery 
	     */
//	    SolrQuery query = new SolrQuery().
//	    		setParam(searchTerm);
//	    		setParam(SolrAnnotationConst.LABEL, searchTerm);
	    SolrQuery query = new SolrQuery();
//	    query.set(SolrAnnotationConst.LABEL, searchTerm);
//	    query.setQuery("OK");
	    query.setQuery(searchTerm);
//	    query.addFilterQuery("label:SUPER");
//	    query.addFilterQuery(SolrAnnotationConst.LABEL + ":" + searchTerm);
//	    query.addFilterQuery("label:OK");
	    query.setFields(SolrAnnotationConst.LABEL);
//	    query.setFields("label");
	    
	    /**
	     * Query the server 
	     */
	    try {
	    	QueryResponse rsp =  solrServer.query( query );
	    	res = rsp.getBeans(SolrTagImpl.class);
		} catch (SolrServerException e) {
			throw new TagServiceException("Unexpected exception occured when searching tags for label: " + searchTerm, e);
        }
	    
	    return res;
	}

	@Override
	public void update(SolrTag solrTag) throws TagServiceException {
    	log.info("update: " + solrTag.toString());	    	
    	delete(solrTag);
    	store(solrTag);
	}

	@Override
	public void delete(SolrTag solrTag) throws TagServiceException {
	    try {
	    	log.info("delete: " + solrTag.toString());
	        UpdateResponse rsp = solrServer.deleteById(solrTag.getId());
	        log.info("delete response: " + rsp.toString());
	        solrServer.commit();
	    } catch (SolrServerException ex) {
			throw new TagServiceException("Unexpected solr server exception occured when deleting tags for: " + solrTag.getId(), ex);
	    } catch (IOException ex) {
			throw new TagServiceException("Unexpected IO exception occured when deleting tags for: " + solrTag.getId(), ex);
	    }		
	}
	

	/**
	 * This method removes solr tags by passed query.
	 * @param query
	 */
	public void deleteByQuery(String query) throws TagServiceException{
	    try {
	    	log.info("deleteByQuery: " + query);
	        UpdateResponse rsp = solrServer.deleteByQuery(query);
	        log.info("delete response: " + rsp.toString());
	        solrServer.commit();
	    } catch (SolrServerException ex) {
			throw new TagServiceException("Unexpected solr server exception occured when deleting tags for query: " + query, ex);
	    } catch (IOException ex) {
			throw new TagServiceException("Unexpected IO exception occured when deleting tags for: " + query, ex);
	    }		
	}

	
	/**
	 * This method removes all solr tags from the solr.
	 * @throws TagServiceException
	 */
	public void cleanUpAll() throws TagServiceException {
    	log.info("clean up all solr tags");
    	deleteByQuery(SolrAnnotationConst.ALL_SOLR_ENTRIES);
	}

}
