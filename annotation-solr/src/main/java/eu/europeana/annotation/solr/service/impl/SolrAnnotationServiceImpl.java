package eu.europeana.annotation.solr.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.BaseHttpSolrClient.RemoteSolrException;
import org.apache.solr.client.solrj.request.json.JsonQueryRequest;
import org.apache.solr.client.solrj.request.json.TermsFacetMap;
import org.apache.solr.client.solrj.response.PivotField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import eu.europeana.annotation.config.AnnotationConfiguration;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.moderation.ModerationRecord;
import eu.europeana.annotation.definitions.model.moderation.Summary;
import eu.europeana.annotation.definitions.model.search.Query;
import eu.europeana.annotation.definitions.model.search.result.ResultSet;
import eu.europeana.annotation.definitions.model.utils.AnnotationIdHelper;
import eu.europeana.annotation.definitions.model.view.AnnotationView;
import eu.europeana.annotation.definitions.model.vocabulary.BodyInternalTypes;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.definitions.model.vocabulary.fields.WebAnnotationModelFields;
import eu.europeana.annotation.solr.exceptions.AnnotationServiceException;
import eu.europeana.annotation.solr.model.internal.SolrAnnotation;
import eu.europeana.annotation.solr.service.SolrAnnotationService;
import eu.europeana.annotation.solr.vocabulary.SolrAnnotationConstants;
import eu.europeana.annotation.solr.vocabulary.SolrSyntaxConstants;

@Service(AnnotationConfiguration.BEAN_SOLR_ANNO_SERVICE)
@PropertySource(
    value = {"classpath:config/annotation.properties", "classpath:config/annotation.user.properties"},
    ignoreResourceNotFound = true)
public class SolrAnnotationServiceImpl extends SolrAnnotationUtils implements SolrAnnotationService {

    @Autowired
    @Qualifier(AnnotationConfiguration.BEAN_ANNO_SOLR_CLIENT)
    SolrClient solrClient;
    
    @Value("${solr.stats.facets:10}")
    private int solrStatsFacets;
    
    @Value("${annotation.data.endpoint:}")
    private String annotationDataEndpoint;

    
//    @Resource
//    AnnotationConfiguration configuration;

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
	    throws AnnotationServiceException {
      try {
    	for (Annotation anno : annos)
    	    store(anno, false);
    	solrClient.commit();
      }
      catch (SolrServerException | RemoteSolrException | IOException ex) {
        throw new AnnotationServiceException(
            "Unexpected Solr server exception occured when storing a list of annotations. " + hideSolrServerBaseUrl(ex.getMessage()),
            ex);
      } 
    }

    @Override
    public void store(Annotation anno, boolean doCommit) throws AnnotationServiceException {
	try {
	    getLogger().debug("store: {}", anno);
	    SolrAnnotation indexedAnno = null;

	    if (anno instanceof SolrAnnotation)
		indexedAnno = (SolrAnnotation) anno;
	    else {
		indexedAnno = copyIntoSolrAnnotation(anno, null, annotationDataEndpoint);
	    }
	    
	    UpdateResponse rsp = solrClient.addBean(indexedAnno);
	    getLogger().trace("store response: {}", rsp);
	    if (doCommit)
		solrClient.commit();
	} catch (SolrServerException | RemoteSolrException | IOException ex) {
	    throw new AnnotationServiceException(
		    "Unexpected Solr server exception occured when storing annotations for: " + anno.getIdentifier() + ". " + hideSolrServerBaseUrl(ex.getMessage()),
		    ex);
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
	} 
	catch (SolrServerException | RemoteSolrException | IOException ex) {
      throw new AnnotationServiceException(
          "Unexpected Solr server exception occured when searching annotations for: " + term + ". " + hideSolrServerBaseUrl(ex.getMessage()),
          ex);
    }
	return res;
    }

    @Override
    @Deprecated
    /**
     * use {@link #search(Query)} instead
     */
    public ResultSet<? extends AnnotationView> search(String term, String start, String limit)
	    throws AnnotationServiceException {

	ResultSet<? extends AnnotationView> res = null;

	/**
	 * Construct a SolrQuery
	 */
	SolrQuery query = new SolrQuery(term);

	if (StringUtils.isNotEmpty(start))
	query.setStart(Integer.parseInt(start));
    if (StringUtils.isNotEmpty(limit))
	query.setRows(Integer.parseInt(limit));

    getLogger().debug("limited query:{} ", query);

	/**
	 * Query the server
	 */
	try {
	    QueryResponse rsp = solrClient.query(query);
	    getLogger().trace("query response: {}", rsp);
	    res = buildResultSet(rsp);
	}
	catch (SolrServerException | RemoteSolrException | IOException ex) {
      throw new AnnotationServiceException(
          "Unexpected Solr server exception occured when searching annotations for: " + term + ". " + hideSolrServerBaseUrl(ex.getMessage()),
          ex);
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
	}
	catch (SolrServerException | RemoteSolrException | IOException ex) {
      throw new AnnotationServiceException(
          "Unexpected Solr server exception occured when searching annotations with query: " + query.toString() + ". " + hideSolrServerBaseUrl(ex.getMessage()),
          ex);
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
	}
	catch (SolrServerException | RemoteSolrException | IOException ex) {
        throw new AnnotationServiceException(
            "Unexpected Solr server exception occured when searching annotations for: " + annoIdUrl + ". " + hideSolrServerBaseUrl(ex.getMessage()),
            ex);
    } 
	
	if (rs.getResultSize() == 0)
	    return null;
	if (rs.getResultSize() != 1)
	    throw new AnnotationServiceException("Expected one result from Solr but found: " + rs.getResultSize());

	return rs.getResults().get(0);

    }

    @Override
    @Deprecated
    /**
     * @deprecated use {@link #search(Query)} instead
     */
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
	} 
	catch (SolrServerException | RemoteSolrException | IOException ex) {
      throw new AnnotationServiceException(
          "Unexpected Solr server exception occured when searching annotations for: " + text + ". " + hideSolrServerBaseUrl(ex.getMessage()),
          ex);
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
	}
	catch (SolrServerException | RemoteSolrException | IOException ex) {
      throw new AnnotationServiceException(
          "Unexpected Solr server exception occured when searching annotations for: " + query.toString() + ". " + hideSolrServerBaseUrl(ex.getMessage()),
          ex);
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
	} 
    catch (SolrServerException | RemoteSolrException | IOException ex) {
      throw new AnnotationServiceException(
          "Unexpected Solr server exception occured when searching annotations for: " + query.toString() + ". " + hideSolrServerBaseUrl(ex.getMessage()),
          ex);
    }
	

	return res;
    }



    @Override
    @Deprecated
    /**
     * @deprecated use {@link #search(Query)} instead
     */
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
	} 
    catch (SolrServerException | RemoteSolrException | IOException ex) {
      throw new AnnotationServiceException(
          "Unexpected Solr server exception occured when searching annotations for: " + query.toString() + ". " + hideSolrServerBaseUrl(ex.getMessage()),
          ex);
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
		} 
		catch (SolrServerException | RemoteSolrException | IOException ex) {
	        throw new AnnotationServiceException(
	            "Unexpected Solr server exception occured when getting the annotations statistics for the facet field: " + fieldName + ". " + hideSolrServerBaseUrl(ex.getMessage()), ex);
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
        if(solrStatsFacets > 0) {
          query.setFacetLimit(solrStatsFacets);
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
        }
        catch (SolrServerException | RemoteSolrException | IOException ex) {
          throw new AnnotationServiceException(
              "Unexpected Solr server exception occured when getting the annotations statistics for: " + query.toString() + ". " + hideSolrServerBaseUrl(ex.getMessage()), ex);
        }     
    }
    
    @Override
    public void update(Annotation anno) throws AnnotationServiceException {
	update(anno, null);
    }

    public boolean update(Annotation anno, Summary summary) throws AnnotationServiceException {
	getLogger().debug("update solr annotation: {}", anno);

//	delete(anno.getIdentifier());
	if (anno.isDisabled()) {
	    // index annotation only if not disabled
	    return true;
	} else {
	    Annotation indexedAnnotation = null;
	    if (anno instanceof SolrAnnotation) {
	      indexedAnnotation = (SolrAnnotation) anno;
	    }
	    else {
	      indexedAnnotation = copyIntoSolrAnnotation(anno, summary, annotationDataEndpoint);
	    }
	    return store(indexedAnnotation);
	}
    }

    public void delete(long annotationIdentifier) throws AnnotationServiceException {
	String annoUri = AnnotationIdHelper.buildAnnotationUri(annotationDataEndpoint, annotationIdentifier);
	delete(annoUri);
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
	}
    catch (SolrServerException | RemoteSolrException | IOException ex) {
      throw new AnnotationServiceException(
          "Unexpected Solr server exception occured when deleting annotations for: " + query + ". " + hideSolrServerBaseUrl(ex.getMessage()), ex);
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
	}
	catch (SolrServerException | RemoteSolrException | IOException ex) {
      throw new AnnotationServiceException(
          "Unexpected Solr server exception occured when deleting annotations for: " + annoUrl + ". " + hideSolrServerBaseUrl(ex.getMessage()),
          ex);
    } 
	}

    public void index(ModerationRecord moderationRecord) {

    }
    


	@Override
	public List<String> checkDuplicateAnnotations(Annotation anno, boolean noSelfDupplicate) throws AnnotationServiceException {
		SolrQuery query = null;
		switch (anno.getMotivationType()) {
		case TRANSCRIBING  :
			query=solrUniquenessQueryTranscriptions(anno, noSelfDupplicate);
		    break;
		case CAPTIONING :
			query=solrUniquenessQueryCaptions(anno, noSelfDupplicate);
		    break;
		case SUBTITLING :
			query=solrUniquenessQuerySubtitles(anno, noSelfDupplicate);
		    break;
		case TAGGING :
			if(BodyInternalTypes.isSemanticTagBody(anno.getBody().getInternalType())) {
				query=solrUniquenessQuerySemanticTagging(anno, noSelfDupplicate);
			}
			else if(BodyInternalTypes.isSimpleTagBody(anno.getBody().getInternalType())) {
				query=solrUniquenessQuerySimpleTagging(anno, noSelfDupplicate);
			}
		    break;
//		case LINKING :
//			query=solrUniquenessQueryLinking(anno, noSelfCheck);
//		    break;
	    case LINKFORCONTRIBUTING :
	        query=solrUniquenessQueryLinkForContributing(anno, noSelfDupplicate);
            break;
		default:
		    break;

		}
		
		if(query==null) {
		  return null;
		}

		getLogger().debug("Solr query for checking the duplicate annotations has been created.");
		//getting back only the "anno_id" field
		query.set("fl", "anno_id");

		/**
		 * Query the server
		 */
		QueryResponse rsp=null;
		try {
		    rsp = solrClient.query(query);
		} 
	    catch (SolrServerException | RemoteSolrException | IOException ex) {
	      throw new AnnotationServiceException(
	          "Unexpected Solr server exception occured when searching with the query: " + query.toString() + ". " + hideSolrServerBaseUrl(ex.getMessage()), ex);
	    }		
		
		List<String> responseAnnotationIds = null;
		final SolrDocumentList docs = rsp.getResults();
		if(docs!=null && docs.size()>0) {
			responseAnnotationIds = new ArrayList<String>();
			for (SolrDocument returnedDoc : docs) {
				responseAnnotationIds.add(String.valueOf(returnedDoc.getFieldValue("anno_id")));
			}
		}

		return responseAnnotationIds;
		
	}
	
	private SolrQuery solrUniquenessQueryTranscriptions(Annotation anno, boolean noSelfDupplicate) {
	  SolrQuery query = new SolrQuery();	
      query.setQuery(WebAnnotationModelFields.MOTIVATION + ":\"" + MotivationTypes.TRANSCRIBING.getOaType() + "\"");
      query.addFilterQuery(SolrAnnotationConstants.TARGET_URI + ":\"" + anno.getTarget().getSource() + "\"");
      if(anno.getBody().getLanguage()!=null) {
        query.addFilterQuery(SolrAnnotationConstants.BODY_VALUE_PREFIX + anno.getBody().getLanguage() + ":*"); 
      }
      else {
        query.addFilterQuery(SolrAnnotationConstants.BODY_VALUE_PREFIX + ":*");
      }
      addNotSelfDupplicateFilter(anno, query, noSelfDupplicate);
	  return query;
	}
	
	private SolrQuery solrUniquenessQueryCaptions(Annotation anno, boolean noSelfDupplicate) {
      SolrQuery query = new SolrQuery();
      query.setQuery(WebAnnotationModelFields.MOTIVATION + ":\"" + MotivationTypes.CAPTIONING.getOaType() + "\"");
      query.addFilterQuery(SolrAnnotationConstants.TARGET_URI + ":\"" + anno.getTarget().getSource() + "\"");
      addNotSelfDupplicateFilter(anno, query, noSelfDupplicate);
      return query;
	}

  private void addNotSelfDupplicateFilter(Annotation anno, SolrQuery query, boolean noSelfDupplicate) {
    if(noSelfDupplicate && anno.getIdentifier() > 0) {
      query.addFilterQuery("-" + SolrAnnotationConstants.ANNO_ID + ":" + anno.getIdentifier());
    }
  }
  
  private void addTargetRecordIdFilter(Annotation anno, SolrQuery query) {
    if(anno.getTarget()!=null) {
      // extract URIs for target_uri field
      List<String> targetUris = extractUriValues(anno.getTarget());
      if(targetUris!=null) {
        // Extract URIs for target_record_id
        List<String> recordIds = extractRecordIds(targetUris);
        if(recordIds != null) {
          for(String recordIdElem: recordIds) {
            query.addFilterQuery(SolrAnnotationConstants.TARGET_RECORD_ID + ":\"" + recordIdElem + "\"");
          }
        }  
      }
    }
  }
	
	private SolrQuery solrUniquenessQuerySubtitles(Annotation anno, boolean noSelfDupplicate) {
      SolrQuery query = new SolrQuery();
      query.setQuery(WebAnnotationModelFields.MOTIVATION + ":\"" + MotivationTypes.SUBTITLING.getOaType() + "\"");
      query.addFilterQuery(SolrAnnotationConstants.TARGET_URI + ":\"" + anno.getTarget().getSource() + "\"");
      if(anno.getBody().getLanguage()!=null) {
        query.addFilterQuery(SolrAnnotationConstants.BODY_VALUE_PREFIX + anno.getBody().getLanguage() + ":*"); 
      }
      else {
        query.addFilterQuery(SolrAnnotationConstants.BODY_VALUE_PREFIX + ":*");
      }
      addNotSelfDupplicateFilter(anno, query, noSelfDupplicate);
      return query;
	}
	
	private SolrQuery solrUniquenessQuerySemanticTagging(Annotation anno, boolean noSelfDupplicate) {
      SolrQuery query = new SolrQuery();
      query.setQuery(WebAnnotationModelFields.MOTIVATION + ":\"" + MotivationTypes.TAGGING.getOaType() + "\"");
      
      addTargetRecordIdFilter(anno, query);   
      
      List<String> bodyUris = extractUriValues(anno.getBody());
      for (int i=0; i<bodyUris.size(); i++) { 
        query.addFilterQuery(SolrAnnotationConstants.BODY_URI + ":\"" + bodyUris.get(i) + "\"");
      }
      addNotSelfDupplicateFilter(anno, query, noSelfDupplicate);
      return query;
	}
	
	private SolrQuery solrUniquenessQuerySimpleTagging(Annotation anno, boolean noSelfDupplicate) {
      SolrQuery query = new SolrQuery();
      query.setQuery(WebAnnotationModelFields.MOTIVATION + ":\"" + MotivationTypes.TAGGING.getOaType() + "\"");
      
      addTargetRecordIdFilter(anno, query);
      
      String bodyMultilingualValue = extractMultilingualValue(anno.getBody());
      if(anno.getBody().getLanguage()!=null) {
        query.addFilterQuery(SolrAnnotationConstants.BODY_VALUE_PREFIX + anno.getBody().getLanguage() + ":\"" + bodyMultilingualValue + "\""); 
      }
      else {
        query.addFilterQuery(SolrAnnotationConstants.BODY_VALUE_PREFIX + ":\"" + bodyMultilingualValue + "\"");
      }
      
      addNotSelfDupplicateFilter(anno, query, noSelfDupplicate);
      return query;
	}

//	private SolrQuery solrUniquenessQueryLinking(Annotation anno, boolean noSelfCheck) {
//      SolrQuery query = new SolrQuery();
//      query.setQuery("*:*");
//      List<String> targetValues = anno.getTarget().getValues();
//      if(targetValues!=null) {
//        for(String target: targetValues) {
//          query.addFilterQuery(SolrAnnotationConstants.TARGET_URI + ":\"" + target + "\"");
//        }
//      }      
//      query.addFilterQuery(WebAnnotationModelFields.MOTIVATION + ":\"" + MotivationTypes.LINKING.getOaType() + "\"");
//      if(noSelfCheck) {
//        query.addFilterQuery(SolrAnnotationConstants.ANNO_ID + ":\"" + anno.getAnnotationId().getIdentifier() + "\"");
//      }
//      return query;
//	}

	private SolrQuery solrUniquenessQueryLinkForContributing(Annotation anno, boolean noSelfDupplicate) {
      SolrQuery query = new SolrQuery();
      query.setQuery(WebAnnotationModelFields.MOTIVATION + ":\"" + MotivationTypes.LINKFORCONTRIBUTING.getOaType() + "\"");

      addTargetRecordIdFilter(anno, query);

      addNotSelfDupplicateFilter(anno, query, noSelfDupplicate);
      return query;
  }
}
