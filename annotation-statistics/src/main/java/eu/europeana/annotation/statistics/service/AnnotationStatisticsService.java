package eu.europeana.annotation.statistics.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.PivotField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.util.NamedList;

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
    	QueryResponse annoFacetStats = solrService.getAnnotationStatisticsPivotFacets(SolrAnnotationConstants.GENERATOR_URI);
    	extractAnnotationStatisticsClientUser(annoFacetStats, annoMetric, SolrAnnotationConstants.GENERATOR_URI, AnnotationStatisticsConstants.CLIENT);   	
    	//getting the annotations statistics for the users
    	annoFacetStats = solrService.getAnnotationStatisticsPivotFacets(SolrAnnotationConstants.CREATOR_URI);
    	extractAnnotationStatisticsClientUser(annoFacetStats, annoMetric, SolrAnnotationConstants.CREATOR_URI, AnnotationStatisticsConstants.USER);
    	//getting the overall annotations statistics
    	annoFacetStats = solrService.getAnnotationStatisticsFacets(SolrAnnotationConstants.SCENARIO); 
    	extractOverallAnnotationStatistics(annoFacetStats, annoMetric, SolrAnnotationConstants.SCENARIO);
    }

    /*
     * This function extracts the overall annotation statistics from the Solr response
     */
    private void extractOverallAnnotationStatistics (QueryResponse annoFacetStats, AnnotationMetric annoMetric, String facetField) {
    	AnnotationStatistics annotationStatisticsAll = new AnnotationStatistics();
    	FacetField annotationStatisticsFacetField = annoFacetStats.getFacetField(facetField);	
    	for (Count facetCount : annotationStatisticsFacetField.getValues()) {  
        	if(facetCount.getName().compareToIgnoreCase(AnnotationScenarioTypes.GEO_TAG)==0) {
        		annotationStatisticsAll.setGeoTag(facetCount.getCount());
        	}
        	if(facetCount.getName().compareToIgnoreCase(AnnotationScenarioTypes.TRANSCRIPTION)==0) {
        		annotationStatisticsAll.setTranscription(facetCount.getCount());
        	}
        	if(facetCount.getName().compareToIgnoreCase(AnnotationScenarioTypes.OBJECT_LINK)==0) {
        		annotationStatisticsAll.setObjectLink(facetCount.getCount());
        	}
        	if(facetCount.getName().compareToIgnoreCase(AnnotationScenarioTypes.SEMANTIC_TAG)==0) {
        		annotationStatisticsAll.setSemanticTag(facetCount.getCount());
        	}
        	if(facetCount.getName().compareToIgnoreCase(AnnotationScenarioTypes.SUBTITLE)==0) {
        		annotationStatisticsAll.setSubtitle(facetCount.getCount());
        	}
    	}
    	annoMetric.setAnnotationStatisticsScenarios(annotationStatisticsAll);
    }
    /*
     * This function extracts the annotation statistics from the Solr response for the clients and users
     */
    private void extractAnnotationStatisticsClientUser (QueryResponse annoFacetStats, AnnotationMetric annoMetric, String facetField, String target) {
    	List<AnnotationStatistics> annotationStatistics = new ArrayList<AnnotationStatistics>();
	    NamedList<List<PivotField>> pivotFieldsNamedList = annoFacetStats.getFacetPivot();
	    List<PivotField> pivotFields = pivotFieldsNamedList.get(facetField+','+SolrAnnotationConstants.SCENARIO);
	    for (PivotField pf : pivotFields) {
    		AnnotationStatistics annoStats = null;
    		if(target.compareToIgnoreCase(AnnotationStatisticsConstants.CLIENT)==0) {
    			annoStats = new AnnotationStatisticsClient();
    			((AnnotationStatisticsClient)annoStats).setClient(pf.getValue().toString());
    		}
    		else if(target.compareToIgnoreCase(AnnotationStatisticsConstants.USER)==0) {
    			annoStats = new AnnotationStatisticsUser();
    			((AnnotationStatisticsUser)annoStats).setUser(pf.getValue().toString());
    		}
    		
    		if (annoStats==null) continue;
		    	
	    	for (PivotField pfNested : pf.getPivot()) {
	    		
	        	if(pfNested.getValue()!=null && pfNested.getValue().toString().compareToIgnoreCase(AnnotationScenarioTypes.GEO_TAG)==0) {
	        		annoStats.setGeoTag(Long.valueOf(pfNested.getCount()));
	        	}
	        	if(pfNested.getValue()!=null && pfNested.getValue().toString().compareToIgnoreCase(AnnotationScenarioTypes.TRANSCRIPTION)==0) {
	        		annoStats.setTranscription(Long.valueOf(pfNested.getCount()));
	        	}
	        	if(pfNested.getValue()!=null && pfNested.getValue().toString().compareToIgnoreCase(AnnotationScenarioTypes.OBJECT_LINK)==0) {
	        		annoStats.setObjectLink(Long.valueOf(pfNested.getCount()));
	        	}
	        	if(pfNested.getValue()!=null && pfNested.getValue().toString().compareToIgnoreCase(AnnotationScenarioTypes.SEMANTIC_TAG)==0) {
	        		annoStats.setSemanticTag(Long.valueOf(pfNested.getCount()));
	        	}
	        	if(pfNested.getValue()!=null && pfNested.getValue().toString().compareToIgnoreCase(AnnotationScenarioTypes.SUBTITLE)==0) {
	        		annoStats.setSubtitle(Long.valueOf(pfNested.getCount()));
	        	}
	    	}        	
        	annotationStatistics.add(annoStats);
    	}
	    
		if(target.compareToIgnoreCase(AnnotationStatisticsConstants.CLIENT)==0) {
			annoMetric.setAnnotationStatisticsClients(annotationStatistics);
		}
		else if(target.compareToIgnoreCase(AnnotationStatisticsConstants.USER)==0) {
			annoMetric.setAnnotationStatisticsUsers(annotationStatistics);
		}
    }

}