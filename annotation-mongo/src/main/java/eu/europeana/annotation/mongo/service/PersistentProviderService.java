package eu.europeana.annotation.mongo.service;

import java.util.List;

import eu.europeana.annotation.definitions.exception.ProviderValidationException;
import eu.europeana.annotation.definitions.model.Provider;
import eu.europeana.annotation.mongo.model.internal.PersistentProvider;
import eu.europeana.corelib.db.service.abstracts.AbstractNoSqlService;

public interface PersistentProviderService extends AbstractNoSqlService<PersistentProvider, String>{

	public abstract Provider store(Provider object) throws ProviderValidationException;

	public List<? extends Provider> getProviderList(String idGeneration);

	/**
	 * This method retrieves providers applying filters.
	 * @param provider
	 * @param idGeneration
	 * @param startOn
	 * @param limit
	 * @return the list of providers
	 */
	public List<? extends Provider> getFilteredProviderList (
			String idGeneration, String startOn, String limit);

	public PersistentProvider find(String name, String idGeneration);
	
	/**
	 * @param name
	 * @param idGeneration
	 * @throws ProviderMongoRuntimeException - less or more than 1 object is found for the given arguments
	 */
	public void remove(String name, String idGeneration);
	
	/**
	 * This method performs update for the passed provider object
	 * @param object
	 */
	public Provider update(Provider object) throws ProviderValidationException;

}

