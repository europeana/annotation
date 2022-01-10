package eu.europeana.annotation.statistics.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.json.BucketJsonFacet;

import eu.europeana.annotation.definitions.model.vocabulary.AnnotationScenarioTypes;
import eu.europeana.annotation.solr.exceptions.AnnotationServiceException;
import eu.europeana.annotation.solr.service.SolrAnnotationService;
import eu.europeana.annotation.solr.vocabulary.SolrAnnotationConstants;
import eu.europeana.annotation.statistics.model.AnnotationMetric;
import eu.europeana.annotation.statistics.model.AnnotationStatistics;

public class AnnotationStatisticsService {

	@Resource
	SolrAnnotationService solrService;
	
    private final Logger logger = LogManager.getLogger(getClass());

    public Logger getLogger() {
      return logger;
    }

    public void getAnnotationsStatistics(AnnotationMetric annoMetric) throws AnnotationServiceException {
    	//getting the annotations statistics for the scenarios
    	QueryResponse annoFacetStatsJson = solrService.getAnnotationStatistics(SolrAnnotationConstants.SCENARIO); 
    	AnnotationStatistics annotationStatisticsScenarios = getStatisticsPerScenario(annoFacetStatsJson.getJsonFacetingResponse().getBucketBasedFacets(SolrAnnotationConstants.SCENARIO).getBuckets());
    	annoMetric.setAnnotationStatisticsScenarios(annotationStatisticsScenarios);
    	
    	//getting the annotations statistics for the users
    	annoFacetStatsJson = solrService.getAnnotationStatistics(SolrAnnotationConstants.CREATOR_URI);
    	Map<String, Long> annotationStatisticsUsers = facetBucketsToMap (annoFacetStatsJson.getJsonFacetingResponse().getBucketBasedFacets(SolrAnnotationConstants.CREATOR_URI).getBuckets());
    	annoMetric.setAnnotationStatisticsUsers(annotationStatisticsUsers);

    	//getting the annotations statistics for the clients
    	annoFacetStatsJson = solrService.getAnnotationStatistics(SolrAnnotationConstants.GENERATOR_URI);
    	Map<String, Long> annotationStatisticsClients = facetBucketsToMap(annoFacetStatsJson.getJsonFacetingResponse().getBucketBasedFacets(SolrAnnotationConstants.GENERATOR_URI).getBuckets());
    	annoMetric.setAnnotationStatisticsClients(annotationStatisticsClients);

    }
  
    private AnnotationStatistics getStatisticsPerScenario (List<BucketJsonFacet> facetJsonBuckets) {
        AnnotationStatistics annoStats = new AnnotationStatistics();  
    	for (BucketJsonFacet facetJsonBucket : facetJsonBuckets) {
    	  switch (facetJsonBucket.getVal().toString()) {
            case AnnotationScenarioTypes.GEO_TAG:
              annoStats.setGeoTag(facetJsonBucket.getCount());
              break;
            case AnnotationScenarioTypes.TRANSCRIPTION:
              annoStats.setTranscription(facetJsonBucket.getCount());
              break;
            case AnnotationScenarioTypes.OBJECT_LINK:
              annoStats.setObjectLink(facetJsonBucket.getCount());
              break;
            case AnnotationScenarioTypes.SEMANTIC_TAG:
              annoStats.setSemanticTag(facetJsonBucket.getCount());
              break;
            case AnnotationScenarioTypes.SUBTITLE:
              annoStats.setSubtitle(facetJsonBucket.getCount());
              break;

            default:
              getLogger().debug("Scenario not supported in statistics service: {}", facetJsonBucket.getVal());
              break;
          }  
    	}
    	return annoStats;
    }
    
    private Map<String, Long> facetBucketsToMap (List<BucketJsonFacet> facetJsonBuckets) {
      if(facetJsonBuckets == null) {
        return null;
      }
      return facetJsonBuckets.stream().collect(
          Collectors.toMap(f -> f.getVal().toString(), f -> Long.valueOf(f.getCount())));
 //     res = new Map<String, Long>();
//      for (BucketJsonFacet facetJsonBucket : facetJsonBuckets) {  	
//    		annoStatsClientsUsers.put(facetJsonBucket.getVal().toString(), facetJsonBucket.getCount());
//    	}
    }
}