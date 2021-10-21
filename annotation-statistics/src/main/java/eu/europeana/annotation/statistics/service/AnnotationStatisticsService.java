package eu.europeana.annotation.statistics.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

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
    	//getting the annotations for the clients
    	List<AnnotationStatistics> annotationStatistics = new ArrayList<AnnotationStatistics>(); 
    	QueryResponse annoFacetStats = solrService.getAnnotationStatisticsForFacetField(SolrAnnotationConstants.GENERATOR_URI);
    	extractAnnotationStatistics(annoFacetStats, annotationStatistics, SolrAnnotationConstants.GENERATOR_URI, AnnotationStatisticsConstants.CLIENT);
    	annoMetric.setAnnotationStatisticsClients(annotationStatistics);
    	//getting the annotations for the users
    	annotationStatistics = new ArrayList<AnnotationStatistics>();
    	annoFacetStats = solrService.getAnnotationStatisticsForFacetField(SolrAnnotationConstants.CREATOR_URI);
    	extractAnnotationStatistics(annoFacetStats, annotationStatistics, SolrAnnotationConstants.CREATOR_URI, AnnotationStatisticsConstants.USER);
    	annoMetric.setAnnotationStatisticsUsers(annotationStatistics);
    }

    private void extractAnnotationStatistics (QueryResponse annoFacetStats, List<AnnotationStatistics> annotationStatistics, String facetField, String target) {
    	
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

    }

}