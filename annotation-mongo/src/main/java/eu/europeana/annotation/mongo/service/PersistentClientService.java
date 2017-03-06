package eu.europeana.annotation.mongo.service;

import java.util.List;

import eu.europeana.annotation.definitions.model.authentication.Client;
import eu.europeana.annotation.mongo.exception.AnnotationMongoException;
import eu.europeana.annotation.mongo.model.internal.PersistentClient;
import eu.europeana.corelib.db.service.abstracts.AbstractNoSqlService;

public interface PersistentClientService extends AbstractNoSqlService<PersistentClient, String> {

	//find() methods 
	public List<? extends PersistentClient> getAll() throws AnnotationMongoException;
	
	/**
	 * 
	 * @param Client
	 * @return
	 * @throws AnnotationMongoException
	 */
	public PersistentClient create(PersistentClient client) throws AnnotationMongoException;

	public Client findByApiKey(String apiKey);
	
}
