package eu.europeana.annotation.solr.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
//import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.search.Query;
import eu.europeana.annotation.definitions.model.search.result.ResultSet;
import eu.europeana.annotation.definitions.model.view.AnnotationView;
import eu.europeana.annotation.solr.exceptions.AnnotationServiceException;
import eu.europeana.annotation.solr.model.internal.SolrAnnotation;
import eu.europeana.annotation.solr.service.SolrAnnotationService;
import eu.europeana.annotation.solr.vocabulary.SolrAnnotationConst;
import eu.europeana.annotation.solr.vocabulary.SolrAnnotationFields;

public class SolrAnnotationServiceImpl extends SolrAnnotationUtils implements SolrAnnotationService {

	SolrServer solrServer;

	public void setSolrServer(SolrServer solrServer) {
		this.solrServer = solrServer;
	}

	@Override
	public void store(Annotation anno) throws AnnotationServiceException {
		try {
			getLogger().info("store: " + anno.toString());
			SolrAnnotation indexedAnno = null;
			
			if(anno instanceof SolrAnnotation) 
				indexedAnno = (SolrAnnotation) anno;
			else{
				boolean withMultilingual = false;
				indexedAnno = copyIntoSolrAnnotation(anno, withMultilingual);
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
	}

	private void processSolrBeanProperties(SolrAnnotation solrAnnotation) {
		// <!-- @Field("annotationIdUrl") -->
		solrAnnotation.setAnnotationIdUrl(solrAnnotation.getAnnotationId().toHttpUrl());

		// <!-- @Field("internalTypeKey") -->
		// TODO: remove not needed field
		// solrAnnotation.setInternalTypeKey(solrAnnotation.getInternalType());

		// <!-- @Field("bodyInternalTypeKey") -->
		
		// <!-- @Field("targetInternalTypeKey") -->
		solrAnnotation.setTargetInternalTypeKey(solrAnnotation.getTarget().getInternalType());

		// <!-- @Field("targetUrls") -->
		// <!-- @Field("targetRecordIds") -->
		//if target URLs were not extracted yet 
		if (solrAnnotation.getTargetUrls() == null) {
			List<String> targetUrls = null;

			if (solrAnnotation.getTarget().getValues() != null && !solrAnnotation.getTarget().getValues().isEmpty())
				targetUrls = solrAnnotation.getTarget().getValues();
			else
				targetUrls = Arrays.asList(new String[] { solrAnnotation.getTarget().getValue() });

			solrAnnotation.setTargetUrls(targetUrls);

			List<String> recordIds = extractRecordIds(targetUrls);
			solrAnnotation.setTargetRecordIds(recordIds);
		}

		// <!-- @Field("motivationKey") -->
		solrAnnotation.setMotivationKey(solrAnnotation.getMotivationType().name());

		// <!-- @Field("body_tag_id") -->
		// solrAnnotation.setBodyTagId(solrAnnotation.getBody().get);

		// <!-- @Field("bodyValue") -->
		if (solrAnnotation.getBody() != null){
			solrAnnotation.setBodyValue(solrAnnotation.getBody().getValue());
			
			//TODO: solved in copy annotation
			//solrAnnotation.setBodyInternalTypeKey(solrAnnotation.getBody().getInternalType());

		}

	}

	private List<String> extractRecordIds(List<String> targetUrls) {

		List<String> recordIds = new ArrayList<String>(targetUrls.size());
		String target;
		for (int i = 0; i < targetUrls.size(); i++) {
			target = targetUrls.get(i);
			
			addToRecordIds(recordIds, target,  WebAnnotationFields.MARKUP_ITEM);	
			addToRecordIds(recordIds, target,  WebAnnotationFields.MARKUP_RECORD);	
		}

		return recordIds;
	}

	protected void addToRecordIds(List<String> recordIds, String target, String markup) {
		String recordId = extractRecordId(target, markup); 
		if (recordId != null)
			recordIds.add(recordId);
	}

	protected String extractRecordId(String target, String markup) {
		int pos;
		pos = target.indexOf(markup);
		if (pos > 0) 
			return target.substring(pos + markup.length());
		return null;
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

	// private List<? extends AnnotationView> setAnnotationType(QueryResponse
	// rsp) {
	// List<? extends Annotation> res;
	// /**
	// * Set annotation type from annotation_type.
	// */
	// List<SolrAnnotationImpl> annotationList =
	// rsp.getBeans(SolrAnnotationImpl.class);
	// Iterator<SolrAnnotationImpl> iter = annotationList.iterator();
	// while (iter.hasNext()) {
	// Annotation annotation = iter.next();
	// annotation.setType(annotation.getInternalTypeKey());
	// if (annotation.getBody() != null) {
	//
	// Map<String, String> multilingualMap =
	// annotation.getBody().getMultilingual();
	// Map<String, String> solrMultilingualMap = new HashMap<String, String>();
	// for (Map.Entry<String, String> entry : multilingualMap.entrySet()) {
	// String key = entry.getKey();
	//// if (key.contains(SolrAnnotationConst.UNDERSCORE +
	// SolrAnnotationConst.MULTILINGUAL)) {
	//// key = key.replace(SolrAnnotationConst.UNDERSCORE +
	// SolrAnnotationConst.MULTILINGUAL, "").toLowerCase();
	//// }
	// solrMultilingualMap.put(key, entry.getValue());
	// }
	// if (solrMultilingualMap.size() > 0)
	// annotation.getBody().setMultilingual(solrMultilingualMap);
	// }
	// }
	//// res = rsp.getBeans(SolrAnnotationImpl.class);
	// res = annotationList;
	// return res;
	// }

	@Override
	public ResultSet<? extends AnnotationView> getAll() throws AnnotationServiceException {

		ResultSet<? extends AnnotationView> res = null;

		/**
		 * Construct a SolrQuery
		 */
		SolrQuery query = new SolrQuery(SolrAnnotationConst.ALL);

		/**
		 * Query the server
		 */
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

		/**
		 * Construct a SolrQuery
		 */
		SolrQuery query = new SolrQuery();
		query.setParam(SolrAnnotationFields.ANNOTATION_ID_URL, new String[] { annoIdUrl });
		// setFieldList(query);

		getLogger().debug("query: " + query);

		/**
		 * Query the server
		 */
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

	protected void setFieldList(SolrQuery query) {
		System.out.println("Warn no fieldlist set");
		// if(SearchProfiles.)

		// String fields = get(CommonParams.FL,"*");

		// query.setFields(
		// SolrAnnotationFields.LABEL.getSolrAnnotationField(),
		// SolrAnnotationFields.LANGUAGE.getSolrAnnotationField(),
		// SolrAnnotationFields.RESOURCE_ID.getSolrAnnotationField(),
		// SolrAnnotationFields.ANNOTATION_ID_STR.getSolrAnnotationField(),
		// SolrAnnotationFields.ANNOTATED_BY.getSolrAnnotationField(),
		// SolrAnnotationFields.ANNOTATION_TYPE.getSolrAnnotationField()
		// );
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

		res = search(SolrAnnotationConst.ALL_SOLR_ENTRIES);

		return res;
	}

	// @Override
	// public ResultSet<? extends AnnotationView> search(Annotation queryObject)
	// throws AnnotationServiceException {
	//
	// ResultSet<? extends AnnotationView> res = null;
	// SolrQuery query = buildSearchQuery(queryObject);
	//
	// /**
	// * Query the server
	// */
	// try {
	// getLogger().info("search obj: " + queryObject);
	// QueryResponse rsp = solrServer.query( query );
	// res = setAnnotationType(rsp);
	// getLogger().debug("search obj res size: " + res.getResultSize());
	// } catch (SolrServerException e) {
	// throw new AnnotationServiceException("Unexpected exception occured when
	// searching annotations for solrAnnotation: " +
	// queryObject.toString(), e);
	// }
	//
	// return res;
	// }

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

	protected SolrQuery buildSearchQuery(AnnotationView queryObject) {
		/**
		 * Construct a SolrQuery
		 */
		SolrQuery query = new SolrQuery();
		String queryStr = "";

		// TODO: proper implementation

		// if (queryObject.getLabel() != null) {
		// queryStr = queryStr +
		// SolrAnnotationFields.LABEL.getSolrAnnotationField()
		// + ":" + queryObject.getLabel();
		// }
		// if (queryObject.getLanguage() != null) {
		// queryStr = queryStr + " AND " +
		// SolrAnnotationFields.LANGUAGE.getSolrAnnotationField()
		// + ":" + queryObject.getLanguage();
		// }
		getLogger().info("queryStr: " + queryStr);
		query.setQuery(queryStr);
		// setFieldList(query, profile);
		return query;
	}

	@Override
	public ResultSet<? extends AnnotationView> searchByLabel(String searchTerm) throws AnnotationServiceException {

		ResultSet<? extends AnnotationView> res = null;

		/**
		 * Construct a SolrQuery
		 */
		SolrQuery query = new SolrQuery();
		query.setQuery(searchTerm);
		// setFieldList(query, profile);

		// query.setFields(SolrAnnotationFields.LABEL.getSolrAnnotationField());

		/**
		 * Query the server
		 */
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

		/**
		 * Construct a SolrQuery
		 */
		SolrQuery query = new SolrQuery();
		query.setQuery(searchKey + ":" + searchValue);
		// setFieldList(query, profile);

		/**
		 * Query the server
		 */
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

		/**
		 * Construct a SolrQuery
		 */
		SolrQuery query = new SolrQuery();
		query.setQuery(field + SolrAnnotationConst.DELIMETER + searchValue);

		/**
		 * Query the server
		 */
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
		getLogger().info("update log: " + anno.toString());
		delete(anno.getAnnotationId());
		Annotation indexedAnnotation = copyIntoSolrAnnotation(anno);
		store(indexedAnnotation);
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
		deleteByQuery(SolrAnnotationConst.ALL_SOLR_ENTRIES);
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
		solrQuery.setTimeAllowed(SolrAnnotationConst.TIME_ALLOWED);
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
			getLogger().info("delete response: " + rsp.toString());
			solrServer.commit();
		} catch (SolrServerException ex) {
			throw new AnnotationServiceException(
					"Unexpected solr server exception occured when deleting annotations for: " + annoUrl, ex);
		} catch (IOException ex) {
			throw new AnnotationServiceException(
					"Unexpected IO exception occured when deleting annotations for: " + annoUrl, ex);
		}

	}

}
