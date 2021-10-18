package eu.europeana.annotation.statistics.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import eu.europeana.annotation.config.AnnotationConfiguration;
import eu.europeana.annotation.solr.exceptions.AnnotationServiceException;
import eu.europeana.annotation.solr.service.SolrAnnotationService;
import eu.europeana.annotation.statistics.model.AnnotationStatistics;
import eu.europeana.annotation.statistics.model.AnnotationStatistics.NumberAnnotationsPerScenario;

public class AnnotationStatisticsService {

	@Resource
	SolrAnnotationService solrService;

    public void getAnnotationsStatistics(AnnotationStatistics annoStats) throws AnnotationServiceException {
    	annoStats.setTimestamp(new Date());
    	
    	List<NumberAnnotationsPerScenario> numberAnnosPerScenarioList = new ArrayList<AnnotationStatistics.NumberAnnotationsPerScenario>();
    	long numAnnotations = solrService.getAnnotationStatistics(AnnotationConfiguration.ANNOTATION_SCENARIO_TRANSCRIPTIONS);
    	AnnotationStatistics.NumberAnnotationsPerScenario numberAnnosPerScenario = annoStats.new NumberAnnotationsPerScenario();
    	numberAnnosPerScenario.setAnnotationsScenario(AnnotationConfiguration.ANNOTATION_SCENARIO_TRANSCRIPTIONS);
		numberAnnosPerScenario.setNumberAnnotations(String.valueOf(numAnnotations));
		numberAnnosPerScenarioList.add(numberAnnosPerScenario);
    	    	
    	numAnnotations = solrService.getAnnotationStatistics(AnnotationConfiguration.ANNOTATION_SCENARIO_GEO_TAGS);
    	numberAnnosPerScenario = annoStats.new NumberAnnotationsPerScenario();
    	numberAnnosPerScenario.setAnnotationsScenario(AnnotationConfiguration.ANNOTATION_SCENARIO_GEO_TAGS);
		numberAnnosPerScenario.setNumberAnnotations(String.valueOf(numAnnotations));
		numberAnnosPerScenarioList.add(numberAnnosPerScenario);
		
    	numAnnotations = solrService.getAnnotationStatistics(AnnotationConfiguration.ANNOTATION_SCENARIO_OBJECT_LINKS);
    	numberAnnosPerScenario = annoStats.new NumberAnnotationsPerScenario();
    	numberAnnosPerScenario.setAnnotationsScenario(AnnotationConfiguration.ANNOTATION_SCENARIO_OBJECT_LINKS);
		numberAnnosPerScenario.setNumberAnnotations(String.valueOf(numAnnotations));
		numberAnnosPerScenarioList.add(numberAnnosPerScenario);

    	numAnnotations = solrService.getAnnotationStatistics(AnnotationConfiguration.ANNOTATION_SCENARIO_SEMANTIC_TAGS);
    	numberAnnosPerScenario = annoStats.new NumberAnnotationsPerScenario();
    	numberAnnosPerScenario.setAnnotationsScenario(AnnotationConfiguration.ANNOTATION_SCENARIO_SEMANTIC_TAGS);
		numberAnnosPerScenario.setNumberAnnotations(String.valueOf(numAnnotations));
		numberAnnosPerScenarioList.add(numberAnnosPerScenario);

    	numAnnotations = solrService.getAnnotationStatistics(AnnotationConfiguration.ANNOTATION_SCENARIO_SIMPLE_TAGS);
    	numberAnnosPerScenario = annoStats.new NumberAnnotationsPerScenario();
    	numberAnnosPerScenario.setAnnotationsScenario(AnnotationConfiguration.ANNOTATION_SCENARIO_SIMPLE_TAGS);
		numberAnnosPerScenario.setNumberAnnotations(String.valueOf(numAnnotations));
		numberAnnosPerScenarioList.add(numberAnnosPerScenario);

    	numAnnotations = solrService.getAnnotationStatistics(AnnotationConfiguration.ANNOTATION_SCENARIO_SUBTITLES);
    	numberAnnosPerScenario = annoStats.new NumberAnnotationsPerScenario();
    	numberAnnosPerScenario.setAnnotationsScenario(AnnotationConfiguration.ANNOTATION_SCENARIO_SUBTITLES);
		numberAnnosPerScenario.setNumberAnnotations(String.valueOf(numAnnotations));
		numberAnnosPerScenarioList.add(numberAnnosPerScenario);

    	annoStats.setAnnotationsStatistics(numberAnnosPerScenarioList);

    }


}