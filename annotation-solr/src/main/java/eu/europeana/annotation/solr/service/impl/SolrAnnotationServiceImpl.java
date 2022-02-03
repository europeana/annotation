package eu.europeana.annotation.solr.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient.RemoteSolrException;
import org.apache.solr.client.solrj.request.json.JsonQueryRequest;
import org.apache.solr.client.solrj.request.json.TermsFacetMap;
import org.apache.solr.client.solrj.response.PivotField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.NamedList;
import org.springframework.stereotype.Component;
import eu.europeana.annotation.config.AnnotationConfiguration;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.moderation.ModerationRecord;
import eu.europeana.annotation.definitions.model.moderation.Summary;
import eu.europeana.annotation.definitions.model.search.Query;
import eu.europeana.annotation.definitions.model.search.result.ResultSet;
import eu.europeana.annotation.definitions.model.view.AnnotationView;
import eu.europeana.annotation.definitions.model.vocabulary.BodyInternalTypes;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.definitions.model.vocabulary.fields.WebAnnotationModelFields;
import eu.europeana.annotation.solr.exceptions.AnnotationServiceException;
import eu.europeana.annotation.solr.model.internal.SolrAnnotation;
import eu.europeana.annotation.solr.service.SolrAnnotationService;
import eu.europeana.annotation.solr.vocabulary.SolrAnnotationConstants;
import eu.europeana.annotation.solr.vocabulary.SolrSyntaxConstants;

@Component
public class SolrAnnotationServiceImpl extends SolrAnnotationUtils implements SolrAnnotationService {

    @Resource
    SolrClient solrClient;
    @Resource
    AnnotationConfiguration configuration;

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
    public QueryResponse getStatisticsByField(String fieldName) throws AnnotationServiceException {
		final TermsFacetMap topCategoriesFacet = new TermsFacetMap(fieldName);
		final JsonQueryRequest request = new JsonQueryRequest()
		    .setQuery("*:*")
		    .setLimit(0)
		    .withFacet(fieldName, topCategoriesFacet);
		// Query the server
		try {
		    getLogger().debug("Getting the annotations statstics with the json nested facets for the facet field: {}.", fieldName);
		    QueryResponse queryResponse = request.process(solrClient);
		    return queryResponse;
		} catch (SolrServerException | IOException e) {
		    throw new AnnotationServiceException("Unexpected exception occured when getting the annotations statistics", e);
		}
    }
    
    @Override
    public Map<String, Map<String, Long>> getStatisticsByFieldAndScenario (String mainFacetField) throws AnnotationServiceException {

        Map<String,Map<String,Long>> statsPerByFieldAndScenario = new HashMap<String, Map<String, Long>>();
        // Construct a SolrQuery
        SolrQuery query = new SolrQuery();
        query.setQuery("*:*");
        //for nested facets the faceted fields need to be concatenated, otherwise the results simple independent facets
        String nestedFacetsFields = mainFacetField + ',' + SolrAnnotationConstants.SCENARIO;
        query.addFacetPivotField(nestedFacetsFields);
        query.setFacet(true);
        if(getConfiguration().getStatsFacets() > 0) {
          query.setFacetLimit(getConfiguration().getStatsFacets());
        }  
        query.setRows(0);       
        // Query the server
        try {
            getLogger().debug("Getting the annotations statstics for the query: {}", query);
            QueryResponse rsp = solrClient.query(query);
            List<PivotField> nestedFacets = rsp.getFacetPivot().get(nestedFacetsFields);
            for (PivotField mainFacetFieldFacet : nestedFacets) {
                Map<String,Long> statsPerScenario = new HashMap<String, Long>();              
                for (PivotField scenarioFacet : mainFacetFieldFacet.getPivot()) {
                    statsPerScenario.put(scenarioFacet.getValue().toString(), Long.valueOf(scenarioFacet.getCount()));
                }
                statsPerByFieldAndScenario.put(mainFacetFieldFacet.getValue().toString(), statsPerScenario);
            }
            return statsPerByFieldAndScenario;
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
    


	@Override
	public List<String> checkDuplicateAnnotations(Annotation anno) throws AnnotationServiceException {
		SolrQuery query = new SolrQuery();
		String queryStr = "";		
		switch (anno.getMotivationType()) {
		case TRANSCRIBING  :
			queryStr=solrUniquenessQueryTranscriptions(anno);
		    break;
		case CAPTIONING :
			queryStr=solrUniquenessQueryCaptions(anno);
		    break;
		case SUBTITLING :
			queryStr=solrUniquenessQuerySubtitles(anno);
		    break;
		case TAGGING :
			if(BodyInternalTypes.isSemanticTagBody(anno.getBody().getInternalType())) 
				queryStr=solrUniquenessQuerySemanticTagging(anno);
			else if(BodyInternalTypes.isSimpleTagBody(anno.getBody().getInternalType())) 
				queryStr=solrUniquenessQuerySimpleTagging(anno);
		    break;
		case LINKING :
			queryStr=solrUniquenessQueryLinking(anno);
		    break;
	    case LINKFORCONTRIBUTING :
            queryStr=solrUniquenessQueryLinkForContributing(anno);
            break;
		default:
		    break;

		}
		getLogger().debug("Solr query for checking the duplicate annotations, queryStr: {}", queryStr);
		query.setQuery(queryStr);
		//getting back only the "anno_id" field
		query.set("fl", "anno_id");

		/**
		 * Query the server
		 */
		QueryResponse rsp=null;
		try {
		    rsp = solrClient.query(query);
		} catch (SolrServerException | IOException e) {
		    throw new AnnotationServiceException(
			    "Unexpected exception occured when searching annotations with the query: " + query.toString(), e);
		}
		
		List<String> responseAnnotationIds = null;
		final SolrDocumentList docs = rsp.getResults();
		if(docs!=null && docs.size()>0) {
			responseAnnotationIds = new ArrayList<String>();
			for (SolrDocument returnedDoc : docs) {
				responseAnnotationIds.add((String)returnedDoc.getFieldValue("anno_id"));
			}
		}

		return responseAnnotationIds;
		
	}
	
	private String solrUniquenessQueryTranscriptions(Annotation anno) {
		String solrQuery = "(" + WebAnnotationModelFields.MOTIVATION + ":\"" + MotivationTypes.TRANSCRIBING.getOaType() + "\"";
		solrQuery+=" AND " + SolrAnnotationConstants.TARGET_URI + ":\"" + anno.getTarget().getSource() + "\"";
		solrQuery+=" AND " + SolrAnnotationConstants.BODY_VALUE_PREFIX + anno.getBody().getLanguage() + ":*"; 
		solrQuery+=")";
		return solrQuery;
	}
	
	private String solrUniquenessQueryCaptions(Annotation anno) {
		String solrQuery = "(" + WebAnnotationModelFields.MOTIVATION + ":\"" + MotivationTypes.CAPTIONING.getOaType() + "\"";
		solrQuery+=" AND " + SolrAnnotationConstants.TARGET_URI + ":\"" + anno.getTarget().getSource() + "\"";
		solrQuery+=")";
		return solrQuery;
	}
	
	private String solrUniquenessQuerySubtitles(Annotation anno) {
		String solrQuery = "(" + WebAnnotationModelFields.MOTIVATION + ":\"" + MotivationTypes.SUBTITLING.getOaType() + "\"";
		solrQuery+=" AND " + SolrAnnotationConstants.TARGET_URI + ":\"" + anno.getTarget().getSource() + "\"";
		solrQuery+=" AND " + SolrAnnotationConstants.BODY_VALUE_PREFIX + anno.getBody().getLanguage() + ":*"; 
		solrQuery+=")";
		return solrQuery;
	}
	
	private String solrUniquenessQuerySemanticTagging(Annotation anno) {
		String solrQuery = "(" + WebAnnotationModelFields.MOTIVATION + ":\"" + MotivationTypes.TAGGING.getOaType() + "\"";
		solrQuery+=" AND " + SolrAnnotationConstants.TARGET_URI + ":\"" + anno.getTarget().getValue() + "\"";
		
		List<String> bodyUris = extractUriValues(anno.getBody());
	    for (String bodyUri : bodyUris) {
			solrQuery+=" AND " + SolrAnnotationConstants.BODY_URI + ":\"" + bodyUri + "\""; 
	    }

		solrQuery+=")";
		return solrQuery;
	}
	
	private String solrUniquenessQuerySimpleTagging(Annotation anno) {
		String solrQuery = "(" + WebAnnotationModelFields.MOTIVATION + ":\"" + MotivationTypes.TAGGING.getOaType() + "\"";
		solrQuery+=" AND " + SolrAnnotationConstants.TARGET_URI + ":\"" + anno.getTarget().getValue() + "\"";
		solrQuery+=" AND " + SolrAnnotationConstants.BODY_VALUE + ":\"" + anno.getBody().getValue() + "\"";
		solrQuery+=")";
		return solrQuery;
	}
	
	private String solrUniquenessQueryLinking(Annotation anno) {
		String solrQuery = "(" + WebAnnotationModelFields.MOTIVATION + ":\"" + MotivationTypes.LINKING.getOaType() + "\"";
		List<String> targetValues = anno.getTarget().getValues();
		for (String targetValue : targetValues) {
			solrQuery+=" AND " + SolrAnnotationConstants.TARGET_URI + ":\"" + targetValue + "\"";
		}
		solrQuery+=")";
		return solrQuery;
	}
	
	private String solrUniquenessQueryLinkForContributing(Annotation anno) {
      String solrQuery = "(" + WebAnnotationModelFields.MOTIVATION + ":\"" + MotivationTypes.LINKFORCONTRIBUTING.getOaType() + "\"";
      
      List<String> targetValues = anno.getTarget().getValues();
      if(targetValues!=null) {
        for (String targetValue : targetValues) {
            solrQuery+=" AND " + SolrAnnotationConstants.TARGET_URI + ":\"" + targetValue + "\"";
        }
      }
      
      if(anno.getTarget().getValue()!=null) {
        solrQuery+=" AND " + SolrAnnotationConstants.TARGET_URI + ":\"" + anno.getTarget().getValue() + "\"";
      }

      List<String> bodyUris = extractUriValues(anno.getBody());
      if(bodyUris!=null) {
        for (String bodyUri : bodyUris) {
            solrQuery+=" AND " + SolrAnnotationConstants.BODY_URI + ":\"" + bodyUri + "\""; 
        }
      }
      
      if(anno.getBody().getValue()!=null) {
        solrQuery+=" AND " + SolrAnnotationConstants.BODY_VALUE + ":\"" + anno.getBody().getValue() + "\"";
      }
      
      solrQuery+=")";
      return solrQuery;
  }

  public AnnotationConfiguration getConfiguration() {
    return configuration;
  }

}
