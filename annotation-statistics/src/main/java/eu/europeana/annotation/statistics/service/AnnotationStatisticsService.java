package eu.europeana.annotation.statistics.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import eu.europeana.annotation.definitions.model.vocabulary.AnnotationScenarioTypes;
import eu.europeana.annotation.solr.exceptions.AnnotationServiceException;
import eu.europeana.annotation.solr.service.SolrAnnotationService;
import eu.europeana.annotation.solr.vocabulary.SolrAnnotationConstants;
import eu.europeana.annotation.statistics.model.AnnotationStatistics;
import eu.europeana.annotation.statistics.model.AnnotationStatistics.AnnotationStatisticsElement;
import eu.europeana.annotation.statistics.vocabulary.AnnotationStatisticsConstants;

public class AnnotationStatisticsService {

	@Resource
	SolrAnnotationService solrService;

    public void getAnnotationsStatistics(AnnotationStatistics annoStats) throws AnnotationServiceException {
    	
    	annoStats.setTimestamp(new Date());
    	
    	List<AnnotationStatisticsElement> annotationStatisticsElements = new ArrayList<AnnotationStatistics.AnnotationStatisticsElement>();
    	Map<String, Map<String, Long>> numAnnotations = solrService.getAnnotationStatisticsForFacetField(SolrAnnotationConstants.GENERATOR_URI);
    	extractAnnotationStatistics(numAnnotations, annoStats, annotationStatisticsElements, AnnotationStatisticsConstants.CLIENT);

    	numAnnotations = solrService.getAnnotationStatisticsForFacetField(SolrAnnotationConstants.CREATOR_URI);
    	extractAnnotationStatistics(numAnnotations, annoStats, annotationStatisticsElements, AnnotationStatisticsConstants.USER);

    	annoStats.setAnnotationStatistics(annotationStatisticsElements);

    }

    private void extractAnnotationStatistics (Map<String, Map<String, Long>> numAnnotations, AnnotationStatistics annoStats, List<AnnotationStatisticsElement> annotationStatisticsElements, String target) {
    	for(Map.Entry<String, Map<String,Long>> numAnnotationsEntry : numAnnotations.entrySet()) {
        	AnnotationStatistics.AnnotationStatisticsElement annoStatsElem = annoStats.new AnnotationStatisticsElement();
        	annoStatsElem.setTarget(target);
        	annoStatsElem.setValue(numAnnotationsEntry.getKey());
        	if(numAnnotationsEntry.getValue().get(AnnotationScenarioTypes.GEO_TAGS)!=null) {
        		annoStatsElem.setGeoTags(numAnnotationsEntry.getValue().get(AnnotationScenarioTypes.GEO_TAGS).longValue());
        	}
        	if(numAnnotationsEntry.getValue().get(AnnotationScenarioTypes.TRANSCRIPTIONS)!=null) {
        		annoStatsElem.setTranscriptions(numAnnotationsEntry.getValue().get(AnnotationScenarioTypes.TRANSCRIPTIONS).longValue());
        	}
        	if(numAnnotationsEntry.getValue().get(AnnotationScenarioTypes.OBJECT_LINKS)!=null) {
        		annoStatsElem.setObjectLinks(numAnnotationsEntry.getValue().get(AnnotationScenarioTypes.OBJECT_LINKS).longValue());
        	}
        	if(numAnnotationsEntry.getValue().get(AnnotationScenarioTypes.SEMANTIC_TAGS)!=null) {
        		annoStatsElem.setSemanticTags(numAnnotationsEntry.getValue().get(AnnotationScenarioTypes.SEMANTIC_TAGS).longValue());
        	}
        	if(numAnnotationsEntry.getValue().get(AnnotationScenarioTypes.SUBTITLES)!=null) {
        		annoStatsElem.setSubtitles(numAnnotationsEntry.getValue().get(AnnotationScenarioTypes.SUBTITLES).longValue());
        	}
        	annotationStatisticsElements.add(annoStatsElem);
    	}

    }

}