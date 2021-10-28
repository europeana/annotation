package eu.europeana.annotation.statistics.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.json.BucketJsonFacet;

import eu.europeana.annotation.definitions.model.vocabulary.AnnotationScenarioTypes;
import eu.europeana.annotation.solr.exceptions.AnnotationServiceException;
import eu.europeana.annotation.solr.service.SolrAnnotationService;
import eu.europeana.annotation.solr.vocabulary.SolrAnnotationConstants;
import eu.europeana.annotation.statistics.model.AnnotationMetric;
import eu.europeana.annotation.statistics.model.AnnotationStatistics;
import eu.europeana.annotation.statistics.model.AnnotationStatisticsClient;
import eu.europeana.annotation.statistics.model.AnnotationStatisticsUser;
import eu.europeana.annotation.statistics.vocabulary.AnnotationStatisticsConstants;

public class AnnotationStatisticsService {

	@Resource
	SolrAnnotationService solrService;

    public void getAnnotationsStatistics(AnnotationMetric annoMetric) throws AnnotationServiceException {
    	annoMetric.setTimestamp(new Date());  
    	//getting the annotations statistics for the clients
    	QueryResponse annoFacetStatsJson = solrService.getAnnotationStatisticsJsonNestedFacets(SolrAnnotationConstants.GENERATOR_URI);
    	extractAnnotationStatisticsClientUserFromJsonNestedFacet(annoFacetStatsJson, annoMetric, SolrAnnotationConstants.GENERATOR_URI, AnnotationStatisticsConstants.CLIENT);   	
    	//getting the annotations statistics for the users
    	annoFacetStatsJson = solrService.getAnnotationStatisticsJsonNestedFacets(SolrAnnotationConstants.CREATOR_URI);
    	extractAnnotationStatisticsClientUserFromJsonNestedFacet(annoFacetStatsJson, annoMetric, SolrAnnotationConstants.CREATOR_URI, AnnotationStatisticsConstants.USER);   	
    	//getting the overall annotations statistics
    	annoFacetStatsJson = solrService.getAnnotationStatisticsJsonFacets(); 
    	extractAnnotationStatisticsOverallFromJsonFacet(annoFacetStatsJson, annoMetric);
    }
    
    private void extractAnnotationStatisticsClientUserFromJsonNestedFacet (QueryResponse annoFacetStats, AnnotationMetric annoMetric, String facetField, String target) {
    	List<AnnotationStatistics> annotationStatistics = new ArrayList<AnnotationStatistics>();
	    annoFacetStats.getJsonFacetingResponse().getBucketBasedFacets(facetField).getBuckets();
    	for (BucketJsonFacet mainBucket : annoFacetStats.getJsonFacetingResponse().getBucketBasedFacets(facetField).getBuckets()) {
    		AnnotationStatistics annoStats = null;
    		if(target.compareToIgnoreCase(AnnotationStatisticsConstants.CLIENT)==0) {
    			annoStats = new AnnotationStatisticsClient();
    			((AnnotationStatisticsClient)annoStats).setClient(mainBucket.getVal().toString());
    		}
    		else if(target.compareToIgnoreCase(AnnotationStatisticsConstants.USER)==0) {
    			annoStats = new AnnotationStatisticsUser();
    			((AnnotationStatisticsUser)annoStats).setUser(mainBucket.getVal().toString());
    		}
    		
    		if (annoStats==null) continue;
		    	
    		setAnnotationStatisticsFromJsonBuckets(annoStats, mainBucket.getBucketBasedFacets(SolrAnnotationConstants.SCENARIO).getBuckets());
        	annotationStatistics.add(annoStats);
    	}
	    
		if(target.compareToIgnoreCase(AnnotationStatisticsConstants.CLIENT)==0) {
			annoMetric.setAnnotationStatisticsClients(annotationStatistics);
		}
		else if(target.compareToIgnoreCase(AnnotationStatisticsConstants.USER)==0) {
			annoMetric.setAnnotationStatisticsUsers(annotationStatistics);
		}
    }
    
    private void extractAnnotationStatisticsOverallFromJsonFacet (QueryResponse annoFacetStats, AnnotationMetric annoMetric) {
    	AnnotationStatistics annotationStatisticsOverall = new AnnotationStatistics();	
    	setAnnotationStatisticsFromJsonBuckets(annotationStatisticsOverall,annoFacetStats.getJsonFacetingResponse().getBucketBasedFacets(SolrAnnotationConstants.SCENARIO).getBuckets());
    	annoMetric.setAnnotationStatisticsScenarios(annotationStatisticsOverall);
    }
    
    private void setAnnotationStatisticsFromJsonBuckets (AnnotationStatistics annoStats, List<BucketJsonFacet> facetJsonBuckets) {
    	for (BucketJsonFacet facetJsonBucket : facetJsonBuckets) {  
        	if(facetJsonBucket.getVal().toString().compareToIgnoreCase(AnnotationScenarioTypes.GEO_TAG)==0) {
        		annoStats.setGeoTag(facetJsonBucket.getCount());
        	}
        	if(facetJsonBucket.getVal().toString().compareToIgnoreCase(AnnotationScenarioTypes.TRANSCRIPTION)==0) {
        		annoStats.setTranscription(facetJsonBucket.getCount());
        	}
        	if(facetJsonBucket.getVal().toString().compareToIgnoreCase(AnnotationScenarioTypes.OBJECT_LINK)==0) {
        		annoStats.setObjectLink(facetJsonBucket.getCount());
        	}
        	if(facetJsonBucket.getVal().toString().compareToIgnoreCase(AnnotationScenarioTypes.SEMANTIC_TAG)==0) {
        		annoStats.setSemanticTag(facetJsonBucket.getCount());
        	}
        	if(facetJsonBucket.getVal().toString().compareToIgnoreCase(AnnotationScenarioTypes.SUBTITLE)==0) {
        		annoStats.setSubtitle(facetJsonBucket.getCount());
        	}
    	}
    }
}