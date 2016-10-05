package eu.europeana.annotation.mongo.service;

import java.util.List;

import eu.europeana.annotation.definitions.exception.ConceptValidationException;
import eu.europeana.annotation.definitions.model.entity.Concept;
import eu.europeana.annotation.mongo.exception.AnnotationMongoException;
import eu.europeana.annotation.mongo.exception.AnnotationMongoRuntimeException;
import eu.europeana.annotation.mongo.model.internal.PersistentConcept;
import eu.europeana.corelib.db.service.abstracts.AbstractNoSqlService;

public interface PersistentConceptService  extends AbstractNoSqlService<PersistentConcept, String> {

	//find() methods 
	public PersistentConcept find(PersistentConcept concept) throws AnnotationMongoException;
	public List<PersistentConcept> findAll(PersistentConcept concept) throws AnnotationMongoException;
	
	//delete methods
	public void remove(String id) throws AnnotationMongoRuntimeException;
	public void remove(PersistentConcept queryConcept) throws AnnotationMongoException;
	
	//store() methods
	/**
	 * 
	 * @param concept
	 * @param agent - the person or software tool that performed the modifications of the concept
	 * @return
	 * @throws AnnotationMongoException
	 */
	public PersistentConcept update(PersistentConcept concept, String agent) throws AnnotationMongoException;
	public PersistentConcept create(PersistentConcept concept) throws AnnotationMongoException;
	
	public abstract Concept store(Concept object) throws ConceptValidationException;
	public PersistentConcept findByUrl(String url);
	public Concept update(Concept object);
	
}
