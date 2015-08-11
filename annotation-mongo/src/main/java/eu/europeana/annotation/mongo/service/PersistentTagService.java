package eu.europeana.annotation.mongo.service;

import java.util.List;

import eu.europeana.annotation.mongo.exception.AnnotationMongoException;
import eu.europeana.annotation.mongo.exception.AnnotationMongoRuntimeException;
import eu.europeana.annotation.mongo.model.internal.PersistentTag;
import eu.europeana.corelib.db.service.abstracts.AbstractNoSqlService;

public interface PersistentTagService  extends AbstractNoSqlService<PersistentTag, String> {

	//find() methods 
	//public String findId(PersistentTag tag);
	public PersistentTag find(PersistentTag tag) throws AnnotationMongoException;
	public List<PersistentTag> findAll(PersistentTag tag) throws AnnotationMongoException;
	
	//delete methods
	public void remove(String id) throws AnnotationMongoRuntimeException;
	public void remove(PersistentTag queryTag) throws AnnotationMongoException;
	
	//store() methods
	/**
	 * 
	 * @param tag
	 * @param agent - the person or software tool that performed the modifications of the tag
	 * @return
	 * @throws AnnotationMongoException
	 */
	public PersistentTag update(PersistentTag tag, String agent) throws AnnotationMongoException;
	public PersistentTag create(PersistentTag tag) throws AnnotationMongoException;
	
}
