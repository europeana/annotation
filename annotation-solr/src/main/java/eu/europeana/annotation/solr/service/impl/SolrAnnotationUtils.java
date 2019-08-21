package eu.europeana.annotation.solr.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.body.Body;
import eu.europeana.annotation.definitions.model.body.GraphBody;
import eu.europeana.annotation.definitions.model.body.SkosConceptBody;
import eu.europeana.annotation.definitions.model.body.impl.PlainTagBody;
import eu.europeana.annotation.definitions.model.factory.impl.BodyObjectFactory;
import eu.europeana.annotation.definitions.model.moderation.Summary;
import eu.europeana.annotation.definitions.model.resource.SpecificResource;
import eu.europeana.annotation.definitions.model.search.Query;
import eu.europeana.annotation.definitions.model.search.result.FacetFieldView;
import eu.europeana.annotation.definitions.model.search.result.ResultSet;
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

	private final Logger logger = LogManager.getLogger(getClass());

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
			solrQuery.setFacetMinCount(1);
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

	public SolrAnnotation copyIntoSolrAnnotation(Annotation annotation, boolean withMultilingual, Summary summary) {
    SolrAnnotation solrAnnotationImpl = new SolrAnnotationImpl(annotation, summary);
		return solrAnnotationImpl;
	}

	/**
	 * This method converts a multilingual part of the Annotation Body in a
	 * multilingual value that is conform for Solr. E.g. 'en' in
	 * 'EN_multilingual'
	 * @deprecated - update when requirements for multilingual bodies are available 
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

	/**
	 * @deprecated - update when requirements for multilingual bodies are available 
	 * @param body
	 * @param bodyRes
	 */
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
	 * @deprecated - update when requirements for multilingual bodies are available 
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
	
	protected void processSolrBeanProperties(SolrAnnotation solrAnnotation) {
		
		solrAnnotation.setMotivationKey(solrAnnotation.getMotivationType().getJsonValue());

		processBody(solrAnnotation);
		
		processTargetUris(solrAnnotation);
		
	}

	protected void processBody(SolrAnnotation solrAnnotation) {
		Body body = solrAnnotation.getBody();
		if(body == null)
			return;
		
		switch (BodyInternalTypes.valueOf(body.getInternalType())){
			case TEXT:
				solrAnnotation.setBodyValue(extractTextValues(body));
			break;
		case GEO_TAG:
			//no text payload specified yet 
//			solrAnnotation.setBodyValue(extractTextValues(body));
			break;
		case GRAPH:
			GraphBody gb = (GraphBody) body;
			processGraphBody(solrAnnotation, gb);
			break;
		case LINK:
			//no body or Graph
			break;
		case SEMANTIC_LINK:
			//not specified yet
			break;
		case SEMANTIC_TAG:
			solrAnnotation.setBodyUris(extractTargetUriValues(body)); 
			break;
		case TAG:
			solrAnnotation.setBodyValue(extractTextValues(body));
			break;
		default:
			break;
			
				
		}
	}

	protected void processGraphBody(SolrAnnotation solrAnnotation, GraphBody gb) {
		solrAnnotation.setLinkRelation(gb.getGraph().getRelationName());
		if(gb.getGraph().getNodeUri() != null)
			solrAnnotation.setLinkResourceUri(gb.getGraph().getNodeUri());
		else if(gb.getGraph().getNode() != null){
			String linkedResourceUri = gb.getGraph().getNode().getHttpUri();
			solrAnnotation.setLinkResourceUri(linkedResourceUri);
		}
	}

	protected String extractTextValues(Body body) {
		String value = "";
		if (body.getValue() != null)
			value += body.getValue();
		else if (body.getValues() != null)
			value += Arrays.toString(body.getValues().toArray());
		return value;
	}

	protected void processTargetUris(SolrAnnotation solrAnnotation) {
		
		SpecificResource internetResource = solrAnnotation.getTarget();
		//extract URIs for target_uri field
		List<String> targetUris = extractTargetUriValues(internetResource);
		solrAnnotation.setTargetUris(targetUris);
		
		//Extract URIs for target_record_id
		List<String> recordIds = extractRecordIds(targetUris);
		//specific resource - scope
		if(internetResource.getScope() != null)
			addRecordIdToList(internetResource.getScope(), recordIds);
			
		solrAnnotation.setTargetRecordIds(recordIds);
	}

	protected List<String> extractTargetUriValues(SpecificResource specificResource) {
		List<String> resourceUrls = null;
		//internet resource
		if (specificResource.getValues() != null && !specificResource.getValues().isEmpty())
			resourceUrls = specificResource.getValues();
		else if(specificResource.getValue() != null)
			resourceUrls = Arrays.asList(new String[] { specificResource.getValue() });
		//specific resource - source 
		else if(specificResource.getSource() != null)
			resourceUrls = Arrays.asList(new String[] { specificResource.getSource() });
		
		return resourceUrls;
	}
	
	List<String> extractRecordIds(List<String> targetUrls) {

		List<String> recordIds = new ArrayList<String>(targetUrls.size());
		for (int i = 0; i < targetUrls.size(); i++)
			addRecordIdToList(targetUrls.get(i), recordIds);	
		
		return recordIds;
	}

	void addRecordIdToList(String target, List<String> recordIds) {
		addToRecordIds(recordIds, target,  WebAnnotationFields.MARKUP_ITEM);	
		addToRecordIds(recordIds, target,  WebAnnotationFields.MARKUP_RECORD);
	}

	private void addToRecordIds(List<String> recordIds, String target, String markup) {
		String recordId = null;
		int pos = target.indexOf(markup);
		if (pos > 0) 
			recordId = target.substring(pos + markup.length() -1);// do not eliminate last /
		
		if (recordId != null)
			recordIds.add(recordId);
	}

}
