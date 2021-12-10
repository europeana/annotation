package eu.europeana.annotation.statistics.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

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

    public void getAnnotationsStatistics(AnnotationMetric annoMetric) throws AnnotationServiceException {
    	//getting the annotations statistics for the scenarios
    	QueryResponse annoFacetStatsJson = solrService.getAnnotationStatisticsJsonFacets(SolrAnnotationConstants.SCENARIO); 
    	AnnotationStatistics annotationStatisticsScenarios = new AnnotationStatistics();	
    	setAnnotationStatisticsScenariosFromJsonBuckets(annotationStatisticsScenarios, annoFacetStatsJson.getJsonFacetingResponse().getBucketBasedFacets(SolrAnnotationConstants.SCENARIO).getBuckets());
    	annoMetric.setAnnotationStatisticsScenarios(annotationStatisticsScenarios);
    	
    	//getting the annotations statistics for the users
    	annoFacetStatsJson = solrService.getAnnotationStatisticsJsonFacets(SolrAnnotationConstants.CREATOR_URI);
    	Map<String, Long> annotationStatisticsUsers = new HashMap<String, Long>();
    	setAnnotationStatisticsClientsUSersFromJsonBuckets(annotationStatisticsUsers, annoFacetStatsJson.getJsonFacetingResponse().getBucketBasedFacets(SolrAnnotationConstants.CREATOR_URI).getBuckets());
    	annoMetric.setAnnotationStatisticsUsers(annotationStatisticsUsers);

    	//getting the annotations statistics for the clients
    	annoFacetStatsJson = solrService.getAnnotationStatisticsJsonFacets(SolrAnnotationConstants.GENERATOR_URI);
    	Map<String, Long> annotationStatisticsClients = new HashMap<String, Long>();
    	setAnnotationStatisticsClientsUSersFromJsonBuckets(annotationStatisticsClients, annoFacetStatsJson.getJsonFacetingResponse().getBucketBasedFacets(SolrAnnotationConstants.GENERATOR_URI).getBuckets());
    	annoMetric.setAnnotationStatisticsClients(annotationStatisticsClients);

    }
  
    private void setAnnotationStatisticsScenariosFromJsonBuckets (AnnotationStatistics annoStats, List<BucketJsonFacet> facetJsonBuckets) {
    	for (BucketJsonFacet facetJsonBucket : facetJsonBuckets) {  
        	if(facetJsonBucket.getVal().toString().equalsIgnoreCase(AnnotationScenarioTypes.GEO_TAG)) {
        		annoStats.setGeoTag(facetJsonBucket.getCount());
        	}
        	if(facetJsonBucket.getVal().toString().equalsIgnoreCase(AnnotationScenarioTypes.TRANSCRIPTION)) {
        		annoStats.setTranscription(facetJsonBucket.getCount());
        	}
        	if(facetJsonBucket.getVal().toString().equalsIgnoreCase(AnnotationScenarioTypes.OBJECT_LINK)) {
        		annoStats.setObjectLink(facetJsonBucket.getCount());
        	}
        	if(facetJsonBucket.getVal().toString().equalsIgnoreCase(AnnotationScenarioTypes.SEMANTIC_TAG)) {
        		annoStats.setSemanticTag(facetJsonBucket.getCount());
        	}
        	if(facetJsonBucket.getVal().toString().equalsIgnoreCase(AnnotationScenarioTypes.SUBTITLE)) {
        		annoStats.setSubtitle(facetJsonBucket.getCount());
        	}
    	}
    }
    
    private void setAnnotationStatisticsClientsUSersFromJsonBuckets (Map<String, Long> annoStatsClientsUsers, List<BucketJsonFacet> facetJsonBuckets) {
    	for (BucketJsonFacet facetJsonBucket : facetJsonBuckets) {  	
    		annoStatsClientsUsers.put(facetJsonBucket.getVal().toString(), facetJsonBucket.getCount());
    	}
    }
}