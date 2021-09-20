package eu.europeana.annotation.statistics.service;

import eu.europeana.annotation.config.AnnotationConfiguration;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.mongo.service.PersistentAnnotationService;
import eu.europeana.annotation.statistics.model.AnnotationStatistics;
import eu.europeana.annotation.statistics.model.AnnotationStatistics.NumberAnnotationsPerScenario;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

public class AnnotationStatisticsService {

	@Resource
	PersistentAnnotationService mongoAnnotationPersistanceService;

    public void getAnnotationsStatistics(AnnotationStatistics annoStats) {
    	annoStats.setTimestamp(new Date());
    	
    	List<NumberAnnotationsPerScenario> numberAnnosPerScenarioList = new ArrayList<AnnotationStatistics.NumberAnnotationsPerScenario>();
    	
    	AnnotationStatistics.NumberAnnotationsPerScenario numberAnnosPerScenario = annoStats.new NumberAnnotationsPerScenario();
    	List<? extends Annotation> filteredAnnos = mongoAnnotationPersistanceService.getFilteredAnnotations(AnnotationConfiguration.ANNOTATION_SCENARIO_TRANSCRIPTIONS);
    	if(filteredAnnos!=null && filteredAnnos.size()>0) {
        	numberAnnosPerScenario.setAnnotationsScenario(AnnotationConfiguration.ANNOTATION_SCENARIO_TRANSCRIPTIONS);
   			numberAnnosPerScenario.setNumberAnnotations(String.valueOf(filteredAnnos.size()));
   			numberAnnosPerScenarioList.add(numberAnnosPerScenario);
    	}
    	    	
    	numberAnnosPerScenario = annoStats.new NumberAnnotationsPerScenario();
    	filteredAnnos = mongoAnnotationPersistanceService.getFilteredAnnotations(AnnotationConfiguration.ANNOTATION_SCENARIO_GEO_TAGS);
    	if(filteredAnnos!=null && filteredAnnos.size()>0) {
    		numberAnnosPerScenario.setNumberAnnotations(String.valueOf(filteredAnnos.size()));    	
    		numberAnnosPerScenario.setAnnotationsScenario(AnnotationConfiguration.ANNOTATION_SCENARIO_GEO_TAGS);
    		numberAnnosPerScenarioList.add(numberAnnosPerScenario);
    	}

    	numberAnnosPerScenario = annoStats.new NumberAnnotationsPerScenario();
    	filteredAnnos = mongoAnnotationPersistanceService.getFilteredAnnotations(AnnotationConfiguration.ANNOTATION_SCENARIO_OBJECT_LINKS);
    	if(filteredAnnos!=null && filteredAnnos.size()>0) {
    		numberAnnosPerScenario.setAnnotationsScenario(AnnotationConfiguration.ANNOTATION_SCENARIO_OBJECT_LINKS);
    		numberAnnosPerScenario.setNumberAnnotations(String.valueOf(filteredAnnos.size()));
    		numberAnnosPerScenarioList.add(numberAnnosPerScenario);
    	}

    	numberAnnosPerScenario = annoStats.new NumberAnnotationsPerScenario();
    	filteredAnnos = mongoAnnotationPersistanceService.getFilteredAnnotations(AnnotationConfiguration.ANNOTATION_SCENARIO_SEMANTIC_TAGS);
    	if(filteredAnnos!=null && filteredAnnos.size()>0) {
        	numberAnnosPerScenario.setAnnotationsScenario(AnnotationConfiguration.ANNOTATION_SCENARIO_SEMANTIC_TAGS);
        	numberAnnosPerScenario.setNumberAnnotations(String.valueOf(filteredAnnos.size()));
        	numberAnnosPerScenarioList.add(numberAnnosPerScenario);
    	}
    	
    	numberAnnosPerScenario = annoStats.new NumberAnnotationsPerScenario();
    	filteredAnnos = mongoAnnotationPersistanceService.getFilteredAnnotations(AnnotationConfiguration.ANNOTATION_SCENARIO_SIMPLE_TAGS);
    	if(filteredAnnos!=null && filteredAnnos.size()>0) {
    		numberAnnosPerScenario.setAnnotationsScenario(AnnotationConfiguration.ANNOTATION_SCENARIO_SIMPLE_TAGS);
    		numberAnnosPerScenario.setNumberAnnotations(String.valueOf(filteredAnnos.size()));
    		numberAnnosPerScenarioList.add(numberAnnosPerScenario);
    	}

    	numberAnnosPerScenario = annoStats.new NumberAnnotationsPerScenario();
    	filteredAnnos = mongoAnnotationPersistanceService.getFilteredAnnotations(AnnotationConfiguration.ANNOTATION_SCENARIO_SUBTITLES);
    	if(filteredAnnos!=null && filteredAnnos.size()>0) {
    		numberAnnosPerScenario.setAnnotationsScenario(AnnotationConfiguration.ANNOTATION_SCENARIO_SUBTITLES);
    		numberAnnosPerScenario.setNumberAnnotations(String.valueOf(filteredAnnos.size()));
    		numberAnnosPerScenarioList.add(numberAnnosPerScenario);
    	}

    	annoStats.setAnnotationsStatistics(numberAnnosPerScenarioList);

    }


}