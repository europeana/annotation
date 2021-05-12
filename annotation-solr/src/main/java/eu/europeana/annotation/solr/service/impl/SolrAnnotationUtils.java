package eu.europeana.annotation.solr.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.body.Body;
import eu.europeana.annotation.definitions.model.body.GraphBody;
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
import eu.europeana.annotation.solr.model.view.AnnotationViewAdapter;
import eu.europeana.annotation.solr.model.view.FacetFieldAdapter;
import eu.europeana.annotation.solr.vocabulary.SolrAnnotationConstants;
import eu.europeana.annotation.utils.UriUtils;

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

    public SolrAnnotation copyIntoSolrAnnotation(Annotation annotation, Summary summary) {
	SolrAnnotation solrAnnotationImpl = new SolrAnnotationImpl(annotation, summary);
	processSolrBeanProperties(solrAnnotationImpl);
	return solrAnnotationImpl;
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
	if (body == null)
	    return;

	String textValue = extractTextValues(body);
	switch (BodyInternalTypes.valueOf(body.getInternalType())) {
	case TEXT:
	    solrAnnotation.setBodyValue(textValue);
	    break;
	case GEO_TAG:
	    // no text payload specified yet
//			solrAnnotation.setBodyValue(extractTextValues(body));
	    break;
	case GRAPH:
	    GraphBody gb = (GraphBody) body;
	    processGraphBody(solrAnnotation, gb);
	    break;
	case LINK:
	    // no body or Graph
	    break;
	case SEMANTIC_LINK:
	    // not specified yet
	    break;
	case SEMANTIC_TAG:
	    //
	    solrAnnotation.setBodyUris(extractUriValues(body));
	    break;
	case TAG:
	    solrAnnotation.setBodyValue(textValue);
	    break;

	case FULL_TEXT_RESOURCE:
	case SPECIFIC_RESOURCE:
	    solrAnnotation.setBodyValue(textValue);
	    solrAnnotation.setBodyUris(extractUriValues(body));
	    solrAnnotation.addMultilingualValue(body.getLanguage(), textValue);
	    break;
	case AGENT:
	case VCARD_ADDRESS:

	default:
	    break;

	}
    }

    protected void processGraphBody(SolrAnnotation solrAnnotation, GraphBody gb) {
	solrAnnotation.setLinkRelation(gb.getGraph().getRelationName());
	if (gb.getGraph().getNodeUri() != null)
	    solrAnnotation.setLinkResourceUri(gb.getGraph().getNodeUri());
	else if (gb.getGraph().getNode() != null) {
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
	// extract URIs for target_uri field
	List<String> targetUris = extractUriValues(internetResource);
	solrAnnotation.setTargetUris(targetUris);

	// Extract URIs for target_record_id
	List<String> recordIds = extractRecordIds(targetUris);
	// specific resource - scope
	if (internetResource.getScope() != null)
	    addRecordIdToList(internetResource.getScope(), recordIds);

	solrAnnotation.setTargetRecordIds(recordIds);
    }

    protected List<String> extractUriValues(SpecificResource specificResource) {
	List<String> resourceUrls = new ArrayList<String>();
	
	// linking scenario, target is list of URIs
	if (specificResource.getValues() != null && !specificResource.getValues().isEmpty()) {
	    for (String value : specificResource.getValues()) {
		appendUrlValue(resourceUrls, value);
	    }
	}
	    

	// Regular or Specific resources
	if (specificResource.getValue() != null) {
	    // simple resource
	    appendUrlValue(resourceUrls, specificResource.getValue());
	} 
	
	if (specificResource.getSource() != null) {
	    // specific resource - source
	    appendUrlValue(resourceUrls, specificResource.getSource());
	} 
	
	if (specificResource.getScope() != null) {
	    // specific resource - scope
	    appendUrlValue(resourceUrls, specificResource.getScope());
	}
	
	if(specificResource.getHttpUri() != null) {
	    //internet resource with Id
	    appendUrlValue(resourceUrls, specificResource.getHttpUri());   
	}
	
	return resourceUrls;
    }

    private void appendUrlValue(List<String> resourceUrls, String value) {
	if(UriUtils.isUrl(value)) {
	    resourceUrls.add(value);
	}
    }

    List<String> extractRecordIds(List<String> targetUrls) {

	List<String> recordIds = new ArrayList<String>(targetUrls.size());
	for (int i = 0; i < targetUrls.size(); i++)
	    addRecordIdToList(targetUrls.get(i), recordIds);

	return recordIds;
    }

    void addRecordIdToList(String target, List<String> recordIds) {
	addToRecordIds(recordIds, target, WebAnnotationFields.MARKUP_ITEM);
	addToRecordIds(recordIds, target, WebAnnotationFields.MARKUP_RECORD);
    }

    private void addToRecordIds(List<String> recordIds, String target, String markup) {
	String recordId = null;
	int pos = target.indexOf(markup);
	if (pos > 0)
	    recordId = target.substring(pos + markup.length() - 1);// do not eliminate last /

	if (recordId != null && !recordIds.contains(recordId)) {
	    recordIds.add(recordId);
	}
    }

}
