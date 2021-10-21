package eu.europeana.annotation.solr.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient.RemoteSolrException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.springframework.stereotype.Component;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.moderation.ModerationRecord;
import eu.europeana.annotation.definitions.model.moderation.Summary;
import eu.europeana.annotation.definitions.model.search.Query;
import eu.europeana.annotation.definitions.model.search.result.ResultSet;
import eu.europeana.annotation.definitions.model.view.AnnotationView;
import eu.europeana.annotation.solr.exceptions.AnnotationServiceException;
import eu.europeana.annotation.solr.model.internal.SolrAnnotation;
import eu.europeana.annotation.solr.service.SolrAnnotationService;
import eu.europeana.annotation.solr.vocabulary.SolrAnnotationConstants;
import eu.europeana.annotation.solr.vocabulary.SolrSyntaxConstants;

@Component
public class SolrAnnotationServiceImpl extends SolrAnnotationUtils implements SolrAnnotationService {

    @Resource
    SolrClient solrClient;

    public void setSolrClient(SolrClient solrServer) {
	this.solrClient = solrServer;
    }

    public SolrClient getSolrClient() {
	return solrClient;
    }

    @Override
    public boolean store(Annotation anno) throws AnnotationServiceException {
	if (anno.isDisabled()) {
	    getLogger().warn("Annotation with the following id was not stored in solr index, annotation diabled: "
		    + anno.toString());
	    return false;
	}
	store(anno, true);
	return true;
    }

    @Override
    public void store(List<? extends Annotation> annos)
	    throws AnnotationServiceException, SolrServerException, IOException {
	for (Annotation anno : annos)
	    store(anno, false);
	solrClient.commit();
    }

    @Override
    public void store(Annotation anno, boolean doCommit) throws AnnotationServiceException {
	try {
	    getLogger().debug("store: {}", anno);
	    SolrAnnotation indexedAnno = null;

	    if (anno instanceof SolrAnnotation)
		indexedAnno = (SolrAnnotation) anno;
	    else {
		indexedAnno = copyIntoSolrAnnotation(anno, null);
	    }
	    
	    UpdateResponse rsp = solrClient.addBean(indexedAnno);
	    getLogger().trace("store response: {}", rsp);
	    if (doCommit)
		solrClient.commit();
	} catch (SolrServerException ex) {
	    throw new AnnotationServiceException(
		    "Unexpected Solr server exception occured when storing annotations for: " + anno.getAnnotationId(),
		    ex);
	} catch (IOException ex) {
	    throw new AnnotationServiceException(
		    "Unexpected IO exception occured when storing annotations for: " + anno.getAnnotationId(), ex);
	}

    }

    @Override
    public ResultSet<? extends AnnotationView> search(String term) throws AnnotationServiceException {

	ResultSet<? extends AnnotationView> res = null;

	/**
	 * Construct a SolrQuery
	 */
	SolrQuery query = new SolrQuery(term);
	getLogger().debug("query: {}", query);

	/**
	 * Query the server
	 */
	try {
	    QueryResponse rsp = solrClient.query(query);
	    getLogger().trace("query response: {}",  rsp);
	    res = buildResultSet(rsp);
	} catch (SolrServerException | IOException e) {
	    throw new AnnotationServiceException("Unexpected exception occured when searching annotations for: " + term,
		    e);
	}

	return res;
    }

    @Override
    public ResultSet<? extends AnnotationView> search(String term, String start, String limit)
	    throws AnnotationServiceException {

	ResultSet<? extends AnnotationView> res = null;

	String msg = term + "' and start: '" + start + "' and rows: '" + limit + "'.";

	/**
	 * Construct a SolrQuery
	 */
	SolrQuery query = new SolrQuery(term);
	try {
	    if (StringUtils.isNotEmpty(start))
		query.setStart(Integer.parseInt(start));
	    if (StringUtils.isNotEmpty(limit))
		query.setRows(Integer.parseInt(limit));
	} catch (Exception e) {
	    throw new AnnotationServiceException("Unexpected exception occured when searching annotations for: " + msg,
		    e);
	}
	getLogger().debug("limited query:{} ", query);

	/**
	 * Query the server
	 */
	try {
	    QueryResponse rsp = solrClient.query(query);
	    getLogger().trace("query response: {}", rsp);
	    res = buildResultSet(rsp);
	} catch (SolrServerException | IOException e) {
	    throw new AnnotationServiceException("Unexpected exception occured when searching annotations for: " + term,
		    e);
	}

	return res;
    }

    @Override
    public ResultSet<? extends AnnotationView> getAll() throws AnnotationServiceException {

	ResultSet<? extends AnnotationView> res = null;

	// Construct a SolrQuery
	SolrQuery query = new SolrQuery(SolrSyntaxConstants.ALL);

	// Query the server
	try {
	    QueryResponse rsp = solrClient.query(query);
	    res = buildResultSet(rsp);
	} catch (SolrServerException | IOException e) {
	    throw new AnnotationServiceException("Unexpected exception occured when searching all annotations", e);
	}

	return res;
    }

    @Override
    public AnnotationView searchById(String annoIdUrl) throws AnnotationServiceException {

	ResultSet<? extends AnnotationView> rs;

	if(getLogger().isDebugEnabled()) {
	    getLogger().debug("search by id: " + annoIdUrl);
	}

	// Construct a SolrQuery
	SolrQuery query = new SolrQuery();
	query.setParam(SolrAnnotationConstants.ANNO_URI, new String[] { annoIdUrl });
	// setFieldList(query);
	// Query the server
	try {
	    QueryResponse rsp = solrClient.query(query);
	    rs = buildResultSet(rsp);
	} catch (SolrServerException | IOException e) {
	    throw new AnnotationServiceException(
		    "Unexpected exception occured when searching annotations for id: " + annoIdUrl, e);
	}

	if (rs.getResultSize() == 0)
	    return null;
	if (rs.getResultSize() != 1)
	    throw new AnnotationServiceException("Expected one result but found: " + rs.getResultSize());

	return rs.getResults().get(0);

    }

    @Override
    public ResultSet<? extends AnnotationView> searchByTerm(String text) throws AnnotationServiceException {

	ResultSet<? extends AnnotationView> res = null;

	/**
	 * Construct a SolrQuery
	 */
	SolrQuery query = new SolrQuery();

	String queryStr = "";

	if (text != null) {
	    // queryStr = "*:" + id;
	    queryStr = text;
	}
	getLogger().debug("queryStr: {}", queryStr);
	query.setQuery(queryStr);

	/**
	 * Query the server
	 */
	try {
	    QueryResponse rsp = solrClient.query(query);
	    res = buildResultSet(rsp);
	} catch (SolrServerException | IOException e) {
	    throw new AnnotationServiceException(
		    "Unexpected exception occured when searching annotations for id: " + text, e);
	}

	return res;
    }

    public ResultSet<? extends AnnotationView> searchAll() throws AnnotationServiceException {

	ResultSet<? extends AnnotationView> res = null;

	res = search(SolrSyntaxConstants.ALL_SOLR_ENTRIES);

	return res;
    }

    @Override
    public ResultSet<? extends AnnotationView> search(Query searchQuery) throws AnnotationServiceException {

	ResultSet<? extends AnnotationView> res = null;
	SolrQuery query = toSolrQuery(searchQuery);

	/**
	 * Query the server
	 */
	try {
	    getLogger().debug("search obj: {}",  searchQuery);
	    QueryResponse rsp = solrClient.query(query);
	    res = buildResultSet(rsp);
	    getLogger().trace("search obj res size: {}", res.getResultSize());
	} catch (SolrServerException | IOException | RemoteSolrException e) {
	    throw new AnnotationServiceException(
		    "Unexpected exception occured (might be due to an inadequate server URL, port, collection name, etc.) when searching annotations for solrAnnotation: "
			    + searchQuery.toString() + ". " + e.getMessage(),
		    e);
	}
	return res;
    }

    @Override
    public ResultSet<? extends AnnotationView> searchByLabel(String searchTerm) throws AnnotationServiceException {

	ResultSet<? extends AnnotationView> res = null;

	// Construct a SolrQuery
	SolrQuery query = new SolrQuery();
	query.setQuery(searchTerm);
	// setFieldList(query, profile);

	// query.setFields(SolrAnnotationFields.LABEL.getSolrAnnotationField());

	// Query the server
	try {
	    getLogger().debug("searchByLabel search query: {}", query);
	    QueryResponse rsp = solrClient.query(query);
	    res = buildResultSet(rsp);
	} catch (SolrServerException | IOException e) {
	    throw new AnnotationServiceException(
		    "Unexpected exception occured when searching annotations for label: " + searchTerm, e);
	}

	return res;
    }

//    @Override
//    public ResultSet<? extends AnnotationView> searchByMapKey(String searchKey, String searchValue)
//	    throws AnnotationServiceException {
//
//	ResultSet<? extends AnnotationView> res = null;
//
//	// Construct a SolrQuery
//	SolrQuery query = new SolrQuery();
//	query.setQuery(searchKey + ":" + searchValue);
//	// setFieldList(query, profile);
//
//	// Query the server
//	try {
//	    getLogger().trace("searchByMapKey search query: " + query.toString());
//	    QueryResponse rsp = solrClient.query(query);
//	    res = buildResultSet(rsp);
//	} catch (SolrServerException | IOException e) {
//	    throw new AnnotationServiceException("Unexpected exception occured when searching annotations for map key: "
//		    + searchKey + " and value: " + searchValue, e);
//	}
//
//	return res;
//    }

    @Override
    public ResultSet<? extends AnnotationView> searchByField(String field, String searchValue)
	    throws AnnotationServiceException {

	ResultSet<? extends AnnotationView> res = null;

	// Construct a SolrQuery
	SolrQuery query = new SolrQuery();
	query.setQuery(field + SolrSyntaxConstants.DELIMETER + searchValue);

	// Query the server
	try {
	    getLogger().debug("searchByField search query: {}", query);
	    QueryResponse rsp = solrClient.query(query);
	    res = buildResultSet(rsp);
	} catch (SolrServerException | IOException e) {
	    throw new AnnotationServiceException("Unexpected exception occured when searching annotations for field: "
		    + field + " and value: " + searchValue, e);
	}

	return res;
    }
    
    @Override
    public QueryResponse getAnnotationStatisticsForFacetField (String facetField)
	    throws AnnotationServiceException {
		// Construct a SolrQuery
		SolrQuery query = new SolrQuery();
		query.setQuery("*" + SolrSyntaxConstants.DELIMETER + "*");
		query.addFacetPivotField(facetField+','+SolrAnnotationConstants.SCENARIO);
		query.setFacet(true);
		query.setFacetLimit(10);
		query.setRows(0);		
		// Query the server
		try {
		    getLogger().debug("Getting the annotations statstics for the query: {}", query);
		    QueryResponse rsp = solrClient.query(query);
		    return rsp;
		} catch (SolrServerException | IOException e) {
		    throw new AnnotationServiceException("Unexpected exception occured when getting the annotations statistics", e);
		}
	
    }

    @Override
    public void update(Annotation anno) throws AnnotationServiceException {
	update(anno, null);
    }

    public boolean update(Annotation anno, Summary summary) throws AnnotationServiceException {
	getLogger().debug("update solr annotation: {}", anno);

	delete(anno.getAnnotationId());
	if (anno.isDisabled()) {
	    // index annotation only if not disabled
	    return true;
	} else {
	    Annotation indexedAnnotation = copyIntoSolrAnnotation(anno, summary);
	    return store(indexedAnnotation);
	}
    }

    public void delete(AnnotationId annotationId) throws AnnotationServiceException {
	String annoId = annotationId.toHttpUrl();
	delete(annoId);

    }

    /**
     * This method removes solr annotations by passed query.
     * 
     * @param query
     */
    public void deleteByQuery(String query) throws AnnotationServiceException {
	try {
	    getLogger().debug("deleteByQuery: {}", query);
	    UpdateResponse rsp = solrClient.deleteByQuery(query);
	    getLogger().trace("delete response: {}", rsp);
	    solrClient.commit();
	} catch (SolrServerException ex) {
	    throw new AnnotationServiceException(
		    "Unexpected solr server exception occured when deleting annotations for query: " + query, ex);
	} catch (IOException ex) {
	    throw new AnnotationServiceException(
		    "Unexpected IO exception occured when deleting annotations for: " + query, ex);
	}
    }

    /**
     * This method removes all solr annotations from the solr.
     * 
     * @throws AnnotationServiceException
     */
    protected void cleanUpAll() throws AnnotationServiceException {
	getLogger().info("clean up all solr annotations");
	deleteByQuery(SolrSyntaxConstants.ALL_SOLR_ENTRIES);
    }

    @Override
    public void delete(String annoUrl) throws AnnotationServiceException {
	try {
	    getLogger().debug("delete annotation with ID: {}", annoUrl);
	    solrClient.deleteById(annoUrl);
	    solrClient.commit();
	} catch (SolrServerException ex) {
	    throw new AnnotationServiceException(
		    "Unexpected solr server exception occured when deleting annotations for: " + annoUrl, ex);
	} catch (IOException ex) {
	    throw new AnnotationServiceException(
		    "Unexpected IO exception occured when deleting annotations for: " + annoUrl, ex);
	} catch (Throwable th) {
	    throw new AnnotationServiceException(
		    "Unexpected exception occured when deleting annotations for: " + annoUrl, th);
	}
    }

    public void index(ModerationRecord moderationRecord) {

    }
    


}
