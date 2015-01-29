package eu.europeana.annotation.solr.service.impl;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;


//import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;

import eu.europeana.annotation.solr.exceptions.AnnotationServiceException;
import eu.europeana.annotation.solr.model.internal.SolrAnnotation;
import eu.europeana.annotation.solr.model.internal.SolrAnnotationConst;
import eu.europeana.annotation.solr.model.internal.SolrAnnotationImpl;
import eu.europeana.annotation.solr.service.SolrAnnotationService;
import eu.europeana.corelib.logging.Logger;


public class SolrAnnotationServiceImpl implements SolrAnnotationService {

	SolrServer solrServer;

	private static final Logger log = Logger.getLogger(SolrAnnotationServiceImpl.class);
	
	public void setSolrServer(SolrServer solrServer) {
		this.solrServer = solrServer;
	}

	@Override
	public void store(SolrAnnotation solrAnnotation)  throws AnnotationServiceException {
	    try {
	    	log.info("store: " + solrAnnotation.toString());
	        UpdateResponse rsp = solrServer.addBean(solrAnnotation);
	        log.info("store response: " + rsp.toString());
	        solrServer.commit();
	    } catch (SolrServerException ex) {
			throw new AnnotationServiceException("Unexpected Solr server exception occured when storing annotations for: " + 
					solrAnnotation.getAnnotationIdString(), ex);
	    } catch (IOException ex) {
			throw new AnnotationServiceException("Unexpected IO exception occured when storing annotations for: " + 
					solrAnnotation.getAnnotationIdString(), ex);
	    }		
	}

	@Override
	public List<? extends SolrAnnotation> search(String term) throws AnnotationServiceException {
			
		List<? extends SolrAnnotation> res = null;
		
		log.info("search Annotation by term: " + term);
		
	    /**
	     * Construct a SolrQuery 
	     */
	    SolrQuery query = new SolrQuery(term);
		log.info("query: " + query.toString());	    
	    
	    /**
	     * Query the server 
	     */
	    try {
	    	QueryResponse rsp =  solrServer.query( query );
			log.info("query response: " + rsp.toString());
			res = setAnnotationType(rsp);
		} catch (SolrServerException e) {
			throw new AnnotationServiceException("Unexpected exception occured when searching annotations for: " + term, e);
        }
	    
	    return res;
	}

	private List<? extends SolrAnnotation> setAnnotationType(QueryResponse rsp) {
		List<? extends SolrAnnotation> res;
		/**
		 * Set annotation type from annotation_type.
		 */
		List<SolrAnnotationImpl> annotationList = rsp.getBeans(SolrAnnotationImpl.class);
		Iterator<SolrAnnotationImpl> iter = annotationList.iterator();
		while (iter.hasNext()) {
			SolrAnnotationImpl annotation = iter.next();
			annotation.setType(annotation.getAnnotationType());
		}
//	    	res = rsp.getBeans(SolrAnnotationImpl.class);
		res = annotationList;
		return res;
	}

	@Override
	public List<? extends SolrAnnotation> getAll() throws AnnotationServiceException {
			
		List<? extends SolrAnnotation> res = null;
			
	    /**
	     * Construct a SolrQuery 
	     */
	    SolrQuery query = new  SolrQuery(SolrAnnotationConst.ALL_SOLR_ENTRIES);
	    
	    /**
	     * Query the server 
	     */
	    try {
	    	QueryResponse rsp =  solrServer.query( query );
			res = setAnnotationType(rsp);
		} catch (SolrServerException e) {
			throw new AnnotationServiceException("Unexpected exception occured when searching all annotations", e);
        }
	    
	    return res;
	}

	@Override
	public List<? extends SolrAnnotation> searchById(String id) throws AnnotationServiceException {
			
		List<? extends SolrAnnotation> res = null;
		
		log.info("search by id: " + id);
		
	    /**
	     * Construct a SolrQuery 
	     */
	    SolrQuery query = new  SolrQuery();
//	    query.setQuery(id);
//	    query.setFields(SolrAnnotationConst.ANNOTATION_ID_STR);
	    
	    String queryStr = "";
	    
	    if (id != null) {
	    	queryStr = SolrAnnotationConst.ANNOTATION_ID_STR + ":" + id;
	    }
	    log.info("queryStr: " + queryStr);
	    query.setQuery(queryStr);
	    
	    query.setFields(
	    		SolrAnnotationConst.LABEL, 
	    		SolrAnnotationConst.LANGUAGE, 
	    		SolrAnnotationConst.RESOURCE_ID, 
	    		SolrAnnotationConst.ANNOTATION_ID_STR
	    		);
	    
	    /**
	     * Query the server 
	     */
	    try {
	    	QueryResponse rsp =  solrServer.query( query );
			res = setAnnotationType(rsp);
		} catch (SolrServerException e) {
			throw new AnnotationServiceException("Unexpected exception occured when searching annotations for id: " + id, e);
        }
	    
	    return res;
	}

	@Override
	public List<? extends SolrAnnotation> searchByTerm(String id) throws AnnotationServiceException {
			
		List<? extends SolrAnnotation> res = null;
		
		log.info("search by id: " + id);
		
	    /**
	     * Construct a SolrQuery 
	     */
	    SolrQuery query = new  SolrQuery();
	    
	    String queryStr = "";
	    
	    if (id != null) {
//	    	queryStr = "*:" + id;
	    	queryStr = id;
	    }
	    log.info("queryStr: " + queryStr);
	    query.setQuery(queryStr);
	    
	    query.setFields(
	    		SolrAnnotationConst.LABEL, 
	    		SolrAnnotationConst.ANNOTATION_TYPE, 
	    		SolrAnnotationConst.LANGUAGE, 
	    		SolrAnnotationConst.RESOURCE_ID, 
	    		SolrAnnotationConst.ANNOTATED_BY, 
	    		SolrAnnotationConst.ANNOTATION_ID_STR
	    		);
	    
	    /**
	     * Query the server 
	     */
	    try {
	    	QueryResponse rsp = solrServer.query( query );
			res = setAnnotationType(rsp);
		} catch (SolrServerException e) {
			throw new AnnotationServiceException("Unexpected exception occured when searching annotations for id: " + id, e);
        }
	    
	    return res;
	}

	public List<? extends SolrAnnotation> searchAll() throws AnnotationServiceException {
			
		List<? extends SolrAnnotation> res = null;
		
	    res = search(SolrAnnotationConst.ALL_SOLR_ENTRIES);
	    
	    return res;
	}

	@Override
	public List<? extends SolrAnnotation> search(SolrAnnotation queryObject)  throws AnnotationServiceException {

		List<? extends SolrAnnotation> res = null;
		
	    /**
	     * Construct a SolrQuery 
	     */
	    SolrQuery query = new SolrQuery();
	    log.info("label: " + queryObject.getLabel());
	    log.info("language: " + queryObject.getLanguage());
//	    query.addFilterQuery("cat:electronics","store:amazon.com");
//	    query.setQuery(queryObject.getLabel());
	    String queryStr = "";

//	    Method[] methods = this.getClass().getMethods();
	    //get method that takes a String as argument
//	    Method method = MyObject.class.getMethod("doSomething", String.class);
//	    Object returnValue = method.invoke(null, "parameter-value1");
	    
	    if (queryObject.getLabel() != null) {
	    	queryStr = queryStr + SolrAnnotationConst.LABEL + ":" + queryObject.getLabel();
	    }
	    if (queryObject.getLanguage() != null) {
	    	queryStr = queryStr + " AND " + SolrAnnotationConst.LANGUAGE + ":" + queryObject.getLanguage();
	    }
//	    if (queryObject.getResourceId() != null) {
//	    	queryStr = queryStr + " AND " + SolrAnnotationConst.RESOURCE_ID + ":" + queryObject.getResourceId();
//	    }
//	    if (queryObject.getAnnotationIdString() != null) {
//	    	queryStr = queryStr + " AND " + SolrAnnotationConst.ANNOTATION_ID_STR + ":" + queryObject.getAnnotationIdString();
//	    }
	    log.info("queryStr: " + queryStr);
	    query.setQuery(queryStr);
	    
//	    query.setQuery(SolrAnnotationConst.LABEL + ":" + queryObject.getLabel() + " AND " +
//	    		SolrAnnotationConst.LANGUAGE + ":" + queryObject.getLanguage());
//	    query.addFilterQuery(SolrAnnotationConst.LABEL + ":" + queryObject.getLabel(),
//	    		SolrAnnotationConst.LANGUAGE + ":" + queryObject.getLanguage());
//	    query.setQuery(SolrAnnotationConst.LABEL + ":" + queryObject.getLabel() + "," + SolrAnnotationConst.LANGUAGE + ":" + queryObject.getLanguage());
//	    query.setQuery(SolrAnnotationConst.LABEL + ":" + queryObject.getLabel());
//	    query.setQuery(SolrAnnotationConst.LANGUAGE + ":" + queryObject.getLanguage());
//	    query.setHighlight(true).setHighlightSnippets(1);
//	    query.setParam(SolrAnnotationConst.LABEL, queryObject.getLabel());
//	    query.setParam(SolrAnnotationConst.LANGUAGE, queryObject.getLanguage());
//	    query.setParam(SolrAnnotationConst.RESOURCE_ID, queryObject.getResourceId());
//	    query.setParam(SolrAnnotationConst.ANNOTATION_ID_STR, queryObject.getAnnotationIdString());
	    query.setFields(
	    		SolrAnnotationConst.LABEL, 
	    		SolrAnnotationConst.LANGUAGE, 
	    		SolrAnnotationConst.RESOURCE_ID, 
	    		SolrAnnotationConst.ANNOTATION_ID_STR
	    		);
//	    query.setFields(SolrAnnotationConst.LABEL);
//	    query.setStart(0);    
//	    query.set("defType", "edismax");
	    log.info("search obj: " + queryObject);
	    
	    /**
	     * Query the server 
	     */
	    try {
	    	QueryResponse rsp = solrServer.query( query );
			res = setAnnotationType(rsp);
	    	log.info("search obj res size: " + res.size());
		} catch (SolrServerException e) {
			throw new AnnotationServiceException("Unexpected exception occured when searching annotations for solrAnnotation: " + 
					queryObject.toString(), e);
        }
	    
	    return res;
	}

	@Override
	public List<? extends SolrAnnotation> searchByLabel(String searchTerm)  throws AnnotationServiceException {
		
		List<? extends SolrAnnotation> res = null;
		
	    /**
	     * Construct a SolrQuery 
	     */
	    SolrQuery query = new SolrQuery();
	    query.setQuery(searchTerm);
	    query.setFields(SolrAnnotationConst.LABEL);
	    
	    /**
	     * Query the server 
	     */
	    try {
	    	log.info("searchByLabel search query: " + query.toString());
	    	QueryResponse rsp =  solrServer.query( query );
			res = setAnnotationType(rsp);
		} catch (SolrServerException e) {
			throw new AnnotationServiceException("Unexpected exception occured when searching annotations for label: " + 
					searchTerm, e);
        }
	    
	    return res;
	}

	@Override
	public List<? extends SolrAnnotation> searchByMapKey(String searchKey, String searchValue)  throws AnnotationServiceException {
		
		List<? extends SolrAnnotation> res = null;
		
	    /**
	     * Construct a SolrQuery 
	     */
	    SolrQuery query = new SolrQuery();
	    query.setQuery(searchKey + ":" + searchValue);
	    
	    /**
	     * Query the server 
	     */
	    try {
	    	log.info("searchByMapKey search query: " + query.toString());
	    	QueryResponse rsp = solrServer.query( query );
			res = setAnnotationType(rsp);
		} catch (SolrServerException e) {
			throw new AnnotationServiceException("Unexpected exception occured when searching annotations for map key: " + 
					searchKey + " and value: " + searchValue, e);
        }
	    
	    return res;
	}

	@Override
	public void update(SolrAnnotation solrAnnotation) throws AnnotationServiceException {
    	log.info("update log: " + solrAnnotation.toString());	    	
    	delete(solrAnnotation);
    	store(solrAnnotation);
	}

	@Override
	public void delete(SolrAnnotation solrAnnotation) throws AnnotationServiceException {
	    try {
	    	log.info("delete: " + solrAnnotation.toString());
//	        UpdateResponse rsp = solrServer.deleteByQuery(SolrAnnotationConst.ANNOTATION_ID_STR + ":" + solrAnnotation.getAnnotationIdString());
//	        UpdateResponse rsp = solrServer.deleteByQuery(SolrAnnotationConst.ALL_SOLR_ENTRIES);
	        UpdateResponse rsp = solrServer.deleteById(solrAnnotation.getAnnotationIdString());
//	        UpdateResponse rsp = solrServer.deleteByQuery(solrAnnotation.getAnnotationIdString());
//	        UpdateResponse rsp = solrServer.deleteByQuery(solrAnnotation.getResourceId());
//	        UpdateResponse rsp = solrServer.deleteByQuery(SolrAnnotationConst.ANNOTATION_ID_STR + ":" + solrAnnotation.getAnnotationIdString());
	        log.info("delete response: " + rsp.toString());
//	        solrServer.commit(true, true);
	        solrServer.commit();
//	        UpdateRequest req = new UpdateRequest();
//	        req.setAction( UpdateRequest.ACTION.COMMIT, false, false );
//	        req.add( docs );
//	        UpdateResponse rsp2 = req.process( solrServer );
	    } catch (SolrServerException ex) {
			throw new AnnotationServiceException("Unexpected solr server exception occured when deleting annotations for: " + 
					solrAnnotation.getAnnotationIdString(), ex);
	    } catch (IOException ex) {
			throw new AnnotationServiceException("Unexpected IO exception occured when deleting annotations for: " + 
					solrAnnotation.getAnnotationIdString(), ex);
	    }		
	}
	

	/**
	 * This method removes solr annotations by passed query.
	 * @param query
	 */
	public void deleteByQuery(String query) throws AnnotationServiceException{
	    try {
	    	log.info("deleteByQuery: " + query);
	        UpdateResponse rsp = solrServer.deleteByQuery(query);
	        log.info("delete response: " + rsp.toString());
	        solrServer.commit();
	    } catch (SolrServerException ex) {
			throw new AnnotationServiceException("Unexpected solr server exception occured when deleting annotations for query: " + query, ex);
	    } catch (IOException ex) {
			throw new AnnotationServiceException("Unexpected IO exception occured when deleting annotations for: " + query, ex);
	    }		
	}

	
	/**
	 * This method removes all solr annotations from the solr.
	 * @throws AnnotationServiceException
	 */
	public void cleanUpAll() throws AnnotationServiceException {
    	log.info("clean up all solr annotations");
    	deleteByQuery(SolrAnnotationConst.ALL_SOLR_ENTRIES);
	}

}
