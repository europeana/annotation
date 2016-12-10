package eu.europeana.annotation.solr.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.body.Body;
import eu.europeana.annotation.definitions.model.body.SkosConceptBody;
import eu.europeana.annotation.definitions.model.body.impl.PlainTagBody;
import eu.europeana.annotation.definitions.model.factory.impl.BodyObjectFactory;
import eu.europeana.annotation.definitions.model.moderation.Summary;
import eu.europeana.annotation.definitions.model.search.Query;
import eu.europeana.annotation.definitions.model.search.result.FacetFieldView;
import eu.europeana.annotation.definitions.model.search.result.ResultSet;
import eu.europeana.annotation.definitions.model.utils.TypeUtils;
import eu.europeana.annotation.definitions.model.view.AnnotationView;
import eu.europeana.annotation.definitions.model.vocabulary.BodyInternalTypes;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.solr.model.internal.SolrAnnotation;
import eu.europeana.annotation.solr.model.internal.SolrAnnotationImpl;
import eu.europeana.annotation.solr.model.internal.SolrTag;
import eu.europeana.annotation.solr.model.internal.SolrTagImpl;
import eu.europeana.annotation.solr.model.view.AnnotationViewAdapter;
import eu.europeana.annotation.solr.model.view.FacetFieldAdapter;
import eu.europeana.annotation.solr.vocabulary.SolrAnnotationConstants;

public class SolrAnnotationUtils {

	private final Logger logger = Logger.getLogger(getClass());

	public Logger getLogger() {
		return logger;
	}

	protected SolrQuery toSolrQuery(Query searchQuery) {

		SolrQuery solrQuery = new SolrQuery();

		solrQuery.setQuery(searchQuery.getQuery());

		if (searchQuery.getFilters() != null)
			solrQuery.addFilterQuery(searchQuery.getFilters());

		if (searchQuery.getFacetFields() != null) {
			solrQuery.setFacet(true);
			solrQuery.addFacetField(searchQuery.getFacetFields());
			solrQuery.setFacetLimit(SolrAnnotationConstants.DEFAULT_FACET_LIMIT);
		}

		if (searchQuery.getSort() != null) {
			solrQuery.setSort(searchQuery.getSort(), SolrQuery.ORDER.valueOf(searchQuery.getSortOrder()));
		}

		solrQuery.setFields(searchQuery.getViewFields());
		
//		searchQuery.setStart(page>0? page -1: page);
//		searchQuery.setRows(Math.min(rows, Query.MAX_PAGE_SIZE));
		solrQuery.setStart(searchQuery.getPageNr() * searchQuery.getPageSize());
		solrQuery.setRows(searchQuery.getPageSize());

		return solrQuery;
	}

	public SolrAnnotation copyIntoSolrAnnotation(Annotation annotation) {

		return copyIntoSolrAnnotation(annotation, false, null);
	}

	public SolrAnnotation copyIntoSolrAnnotation(Annotation annotation, boolean withMultilingual) {

		return copyIntoSolrAnnotation(annotation, withMultilingual, null);
	}

	public SolrAnnotation copyIntoSolrAnnotation(Annotation annotation, boolean withMultilingual, Summary summary) {

		SolrAnnotation solrAnnotationImpl = new SolrAnnotationImpl();
		// solrAnnotationImpl.setType(annotation.getType());
		// TODO: update this to store the internal type instead of the oa type
		// in the index
		// TODO: add the internal type solr configs
		solrAnnotationImpl.setInternalType(annotation.getInternalType());
		solrAnnotationImpl.setInternalTypeKey(annotation.getInternalType());
		// solrAnnotationImpl.setInternalType(annotation.getInternalType());
		solrAnnotationImpl.setCreator(annotation.getCreator());
		Body body = annotation.getBody();

		if (body != null) {
			solrAnnotationImpl.setBody(body);
			solrAnnotationImpl.setBodyValue(body.getValue());
			solrAnnotationImpl.setBodyTagId(solrAnnotationImpl.getBodyTagId());
			solrAnnotationImpl.setBodyInternalTypeKey(body.getInternalType());
			if (withMultilingual)
				body = convertToSolrMultilingual(body);
		}

		solrAnnotationImpl.setCreated(annotation.getCreated());
		solrAnnotationImpl.setCreatorString(annotation.getCreator().getName());
		solrAnnotationImpl.setTarget(annotation.getTarget());
		solrAnnotationImpl.setAnnotationId(annotation.getAnnotationId());

		// solrAnnotationImpl.setLanguage(body.getLanguage());
		solrAnnotationImpl.setMotivation(annotation.getMotivation());
		solrAnnotationImpl.setGenerated(annotation.getGenerated());
		if(annotation.getGenerator() != null){
			solrAnnotationImpl.setGenerator(annotation.getGenerator());
			solrAnnotationImpl.setGeneratorId(annotation.getGenerator().getHttpUrl());
			solrAnnotationImpl.setGeneratorName(annotation.getGenerator().getName());
		}
		
		solrAnnotationImpl.setStyledBy(annotation.getStyledBy());
		solrAnnotationImpl.setAnnotationIdUrl(annotation.getAnnotationId().toHttpUrl());

		solrAnnotationImpl.setSameAs(solrAnnotationImpl.getSameAs());
		// TODO: add the equivalent to solr configs
		solrAnnotationImpl.setSameAs(solrAnnotationImpl.getEquivalentTo());
		solrAnnotationImpl.setUpdatedTimestamp(annotation.getLastUpdate().getTime());
		solrAnnotationImpl.setCreatedTimestamp(annotation.getCreated().getTime());
		solrAnnotationImpl.setGeneratedTimestamp(annotation.getGenerated().getTime());

		if (summary != null) {
			solrAnnotationImpl.setModerationScore((long) summary.getScore());
		} else {
			solrAnnotationImpl.setModerationScore((long) 0);
		}

		return solrAnnotationImpl;

	}

	/**
	 * This method converts a multilingual part of the Annotation Body in a
	 * multilingual value that is conform for Solr. E.g. 'en' in
	 * 'EN_multilingual'
	 * 
	 * @param body
	 * @return converted body
	 */
	protected Body convertToSolrMultilingual(Body body) {
		// TODO: update this when semantic tagging specifications are available
		if (!BodyInternalTypes.SEMANTIC_TAG.name().equals(body.getInternalType()))
			return body;

		Body bodyRes = BodyObjectFactory.getInstance().createModelObjectInstance(BodyInternalTypes.SEMANTIC_TAG.name());

		// if (StringUtils.isNotEmpty(body.getType()))
		if (body.getType() != null)
			bodyRes.setType(body.getType());
		if (StringUtils.isNotEmpty(body.getContentType()))
			bodyRes.setContentType(body.getContentType());
		if (StringUtils.isNotEmpty(body.getHttpUri()))
			bodyRes.setHttpUri(body.getHttpUri());
		if (StringUtils.isNotEmpty(body.getLanguage()))
			bodyRes.setLanguage(body.getLanguage());
//		if (StringUtils.isNotEmpty(body.getMediaType()))
//			bodyRes.setMediaType(body.getMediaType());
		if (StringUtils.isNotEmpty(body.getValue()))
			bodyRes.setValue(body.getValue());
		if (StringUtils.isNotBlank(((PlainTagBody) body).getTagId())) {
			((PlainTagBody) bodyRes).setTagId(((PlainTagBody) body).getTagId());
		}

		setMultilingualMap(body, bodyRes);
		return bodyRes;
	}

	protected void setMultilingualMap(Body body, Body bodyRes) {
		if ((body instanceof SkosConceptBody) && (bodyRes instanceof SkosConceptBody)) {
			SkosConceptBody skosBody = (SkosConceptBody) body;
			Map<String, String> multilingualMap = skosBody.getMultilingual();
			Map<String, String> solrMultilingualMap = new HashMap<String, String>();
			for (Map.Entry<String, String> entry : multilingualMap.entrySet()) {
				String key = entry.getKey();
				if (!key.contains(WebAnnotationFields.UNDERSCORE + WebAnnotationFields.MULTILINGUAL)) {
					key = key.toUpperCase() + WebAnnotationFields.UNDERSCORE + WebAnnotationFields.MULTILINGUAL;
				}
				solrMultilingualMap.put(key, entry.getValue());
			}
			if (solrMultilingualMap.size() > 0)
				((SkosConceptBody)bodyRes).setMultilingual(solrMultilingualMap);
		}
	}

	/**
	 * This method converts Body object in SolrTag object.
	 * 
	 * @param tag
	 *            The body object
	 * @return the SolrTag object
	 */
	protected SolrTag copyIntoSolrTag(Body tag) {

		SolrTag res = null;

		tag = convertToSolrMultilingual(tag);
		SolrTagImpl solrTagImpl = new SolrTagImpl();
		if (StringUtils.isNotBlank(((PlainTagBody) tag).getTagId())) {
			solrTagImpl.setId(((PlainTagBody) tag).getTagId());
		}
		// solrTagImpl.setTagType(tag.getType());
		//TODO: replace the following code with a proper implementation 
		//solrTagImpl.setTagType(TypeUtils.getTypeListAsStr(tag.getType()));
		solrTagImpl.setValue(tag.getValue());
		solrTagImpl.setLanguage(tag.getLanguage());
		solrTagImpl.setContentType(tag.getContentType());
		solrTagImpl.setHttpUri(tag.getHttpUri());

		if (tag instanceof SkosConceptBody)
			solrTagImpl.setMultilingual(((SkosConceptBody) tag).getMultilingual());

		res = solrTagImpl;

		return res;
	}

	@SuppressWarnings("unchecked")
	protected <T extends AnnotationView> ResultSet<T> buildResultSet(QueryResponse rsp) {

		ResultSet<T> resultSet = new ResultSet<>();
		List<T> beans = (List<T>) rsp.getBeans(AnnotationViewAdapter.class);
		resultSet.setResults(beans);

		resultSet.setResultSize(rsp.getResults().getNumFound());

		if (rsp.getFacetFields() != null) {
			List<FacetFieldView> facetFields = new ArrayList<>(rsp.getFacetFields().size());
			for (FacetField solrFacetField : rsp.getFacetFields())
				facetFields.add(new FacetFieldAdapter(solrFacetField));

			resultSet.setFacetFields(facetFields);
		}

		if (rsp.getFacetQuery() != null)
			resultSet.setQueryFacets(rsp.getFacetQuery());

		return resultSet;
	}
}
