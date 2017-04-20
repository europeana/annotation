package eu.europeana.annotation.solr.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
//import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
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

	SolrServer solrServer;

	public void setSolrServer(SolrServer solrServer) {
		this.solrServer = solrServer;
	}

	@Override
	public boolean store(Annotation anno) throws AnnotationServiceException {
		if(anno.isDisabled()){
			getLogger().warn("Annotation with the following id was not stored in solr index, annotation diabled: " + anno.toString());
			return false;
		}
		
		try {
			getLogger().debug("store: " + anno.toString());
			SolrAnnotation indexedAnno = null;
			
			if(anno instanceof SolrAnnotation) 
				indexedAnno = (SolrAnnotation) anno;
			else{
				boolean withMultilingual = false;
				indexedAnno = copyIntoSolrAnnotation(anno, withMultilingual, null);
			}
			
			processSolrBeanProperties(indexedAnno);

			UpdateResponse rsp = solrServer.addBean(indexedAnno);
			getLogger().info("store response: " + rsp.toString());
			solrServer.commit();
		} catch (SolrServerException ex) {
			throw new AnnotationServiceException(
					"Unexpected Solr server exception occured when storing annotations for: " + anno.getAnnotationId(),
					ex);
		} catch (IOException ex) {
			throw new AnnotationServiceException(
					"Unexpected IO exception occured when storing annotations for: " + anno.getAnnotationId(), ex);
		}
		
		return true;
	}

	
	@Override
	public ResultSet<? extends AnnotationView> search(String term) throws AnnotationServiceException {

		ResultSet<? extends AnnotationView> res = null;

		getLogger().info("search Annotation by term: " + term);

		/**
		 * Construct a SolrQuery
		 */
		SolrQuery query = new SolrQuery(term);
		getLogger().info("query: " + query.toString());

		/**
		 * Query the server
		 */
		try {
			QueryResponse rsp = solrServer.query(query);
			getLogger().info("query response: " + rsp.toString());
			res = buildResultSet(rsp);
		} catch (SolrServerException e) {
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
		getLogger().info("search Annotation by term: '" + msg);

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
		getLogger().info("limited query: " + query.toString());

		/**
		 * Query the server
		 */
		try {
			QueryResponse rsp = solrServer.query(query);
			getLogger().info("query response: " + rsp.toString());
			res = buildResultSet(rsp);
		} catch (SolrServerException e) {
			throw new AnnotationServiceException("Unexpected exception occured when searching annotations for: " + term,
					e);
		}

		return res;
	}

	@Override
	public ResultSet<? extends AnnotationView> getAll() throws AnnotationServiceException {

		ResultSet<? extends AnnotationView> res = null;

		//Construct a SolrQuery
		SolrQuery query = new SolrQuery(SolrSyntaxConstants.ALL);

		//Query the server
		try {
			QueryResponse rsp = solrServer.query(query);
			res = buildResultSet(rsp);
		} catch (SolrServerException e) {
			throw new AnnotationServiceException("Unexpected exception occured when searching all annotations", e);
		}

		return res;
	}

	@Override
	public AnnotationView searchById(String annoIdUrl) throws AnnotationServiceException {

		ResultSet<? extends AnnotationView> rs;

		getLogger().info("search by id: " + annoIdUrl);

		//Construct a SolrQuery
		SolrQuery query = new SolrQuery();
		query.setParam(SolrAnnotationConstants.ANNO_URI, new String[] { annoIdUrl });
		// setFieldList(query);

		getLogger().debug("query: " + query);

		//Query the server
		try {
			QueryResponse rsp = solrServer.query(query);
			rs = buildResultSet(rsp);
		} catch (SolrServerException e) {
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

		getLogger().info("search by id: " + text);

		/**
		 * Construct a SolrQuery
		 */
		SolrQuery query = new SolrQuery();

		String queryStr = "";

		if (text != null) {
			// queryStr = "*:" + id;
			queryStr = text;
		}
		getLogger().info("queryStr: " + queryStr);
		query.setQuery(queryStr);

		/**
		 * Query the server
		 */
		try {
			QueryResponse rsp = solrServer.query(query);
			res = buildResultSet(rsp);
		} catch (SolrServerException e) {
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
			getLogger().info("search obj: " + searchQuery);
			QueryResponse rsp = solrServer.query(query);
			res = buildResultSet(rsp);
			getLogger().debug("search obj res size: " + res.getResultSize());
		} catch (SolrServerException e) {
			throw new AnnotationServiceException(
					"Unexpected exception occured when searching annotations for solrAnnotation: "
							+ searchQuery.toString(),
					e);
		}

		return res;
	}

	@Override
	public ResultSet<? extends AnnotationView> searchByLabel(String searchTerm) throws AnnotationServiceException {

		ResultSet<? extends AnnotationView> res = null;

		//Construct a SolrQuery
		SolrQuery query = new SolrQuery();
		query.setQuery(searchTerm);
		// setFieldList(query, profile);

		// query.setFields(SolrAnnotationFields.LABEL.getSolrAnnotationField());

		//Query the server
		try {
			getLogger().info("searchByLabel search query: " + query.toString());
			QueryResponse rsp = solrServer.query(query);
			res = buildResultSet(rsp);
		} catch (SolrServerException e) {
			throw new AnnotationServiceException(
					"Unexpected exception occured when searching annotations for label: " + searchTerm, e);
		}

		return res;
	}

	@Override
	public ResultSet<? extends AnnotationView> searchByMapKey(String searchKey, String searchValue)
			throws AnnotationServiceException {

		ResultSet<? extends AnnotationView> res = null;

		//Construct a SolrQuery
		SolrQuery query = new SolrQuery();
		query.setQuery(searchKey + ":" + searchValue);
		// setFieldList(query, profile);

		//Query the server
		try {
			getLogger().info("searchByMapKey search query: " + query.toString());
			QueryResponse rsp = solrServer.query(query);
			res = buildResultSet(rsp);
		} catch (SolrServerException e) {
			throw new AnnotationServiceException("Unexpected exception occured when searching annotations for map key: "
					+ searchKey + " and value: " + searchValue, e);
		}

		return res;
	}

	@Override
	public ResultSet<? extends AnnotationView> searchByField(String field, String searchValue)
			throws AnnotationServiceException {

		ResultSet<? extends AnnotationView> res = null;

		//Construct a SolrQuery
		SolrQuery query = new SolrQuery();
		query.setQuery(field + SolrSyntaxConstants.DELIMETER + searchValue);

		//Query the server
		try {
			getLogger().info("searchByField search query: " + query.toString());
			QueryResponse rsp = solrServer.query(query);
			res = buildResultSet(rsp);
		} catch (SolrServerException e) {
			throw new AnnotationServiceException("Unexpected exception occured when searching annotations for field: "
					+ field + " and value: " + searchValue, e);
		}

		return res;
	}

	@Override
	public void update(Annotation anno) throws AnnotationServiceException {
		update(anno, null);
	}

	public boolean update(Annotation anno, Summary summary) throws AnnotationServiceException {
		getLogger().debug("update solr annotation: " + anno.toString());
		boolean ret = false;
		
		delete(anno.getAnnotationId());
		//index annotation only if not disabled
		//disabled annotations 
		if(!anno.isDisabled()){
			Annotation indexedAnnotation = copyIntoSolrAnnotation(anno, false, summary);
			ret = store(indexedAnnotation);
		}
		return ret;
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
			getLogger().info("deleteByQuery: " + query);
			UpdateResponse rsp = solrServer.deleteByQuery(query);
			getLogger().info("delete response: " + rsp.toString());
			solrServer.commit();
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
	public Map<String, Integer> queryFacetSearch(String query, String[] qf, List<String> queries)
			throws AnnotationServiceException {
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(query);
		if (qf != null) {
			solrQuery.addFilterQuery(qf);
		}
		solrQuery.setRows(0);
		solrQuery.setFacet(true);
		solrQuery.setTimeAllowed(SolrSyntaxConstants.TIME_ALLOWED);
		for (String queryFacet : queries) {
			solrQuery.addFacetQuery(queryFacet);
		}
		QueryResponse response;
		Map<String, Integer> queryFacets = null;
		try {
			if (getLogger().isDebugEnabled()) {
				getLogger().debug("Solr query is: " + solrQuery.toString());
			}
			response = solrServer.query(solrQuery);
			getLogger().info("queryFacetSearch" + response.getElapsedTime());
			queryFacets = response.getFacetQuery();
		} catch (Exception e) {
			getLogger().error("Exception: " + e.getClass().getCanonicalName() + " " + e.getMessage() + " for query "
					+ solrQuery.toString());
			e.printStackTrace();
		}

		return queryFacets;
	}

	@Override
	public void delete(String annoUrl) throws AnnotationServiceException {
		try {
			getLogger().info("delete annotation with ID: " + annoUrl);
			UpdateResponse rsp = solrServer.deleteById(annoUrl);
			getLogger().trace("delete response: " + rsp.toString());
			solrServer.commit();
		} catch (SolrServerException ex) {
			throw new AnnotationServiceException(
					"Unexpected solr server exception occured when deleting annotations for: " + annoUrl, ex);
		} catch (IOException ex) {
			throw new AnnotationServiceException(
					"Unexpected IO exception occured when deleting annotations for: " + annoUrl, ex);
		}catch (Throwable th) {
			throw new AnnotationServiceException(
					"Unexpected exception occured when deleting annotations for: " + annoUrl, th);
		}
	}
	
	public void index(ModerationRecord moderationRecord) {
		
	}

}
