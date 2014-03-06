package eu.europeana.annotation.web.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.mongo.service.PersistentAnnotationService;
import eu.europeana.annotation.web.service.AnnotationConfiguration;
import eu.europeana.annotation.web.service.AnnotationService;
import eu.europeana.corelib.logging.Log;
import eu.europeana.corelib.logging.Logger;

public class AnnotationServiceImpl implements AnnotationService {

	@Autowired
	AnnotationConfiguration configuration;
	
	@Autowired
	PersistentAnnotationService mongoPersistance;
 		
	@Log
	private Logger log;
	
	@Override
	public String getComponentName() {
		return configuration.getComponentName();
	}

	protected AnnotationConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(AnnotationConfiguration configuration) {
		this.configuration = configuration;
	}

	protected PersistentAnnotationService getMongoPersistance() {
		return mongoPersistance;
	}

	public void setMongoPersistance(PersistentAnnotationService mongoPersistance) {
		this.mongoPersistance = mongoPersistance;
	}

	@Override
	public List<? extends Annotation> getAnnotationList(String collection, String object) {
		
		return getMongoPersistance().getAnnotationList("/"+collection + "/" +object);
	}
	
	@Override
	public Annotation getAnnotationById(String collection, String object,
			int annotationNr) {
		return getMongoPersistance().getAnnotation("/"+collection + "/" +object, annotationNr);
		
	}

	@Override
	public Annotation createAnnotation(Annotation newAnnotation) {
		//TODO: add solr indexing here
		return getMongoPersistance().store(newAnnotation);
	}

	@Override
	public Annotation updateAnnotation(Annotation newAnnotation) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteAnnotation(Annotation newAnnotation) {
		// TODO Auto-generated method stub
		
	}

	

}
