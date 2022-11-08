package eu.europeana.annotation.mongo.dao;

import java.io.Serializable;

import eu.europeana.annotation.mongo.model.internal.PersistentApiWriteLock;
import eu.europeana.api.commons.nosql.dao.NosqlDao;

/**
 *
 * @param <E>
 * @param <T>
 */
public interface PersistentApiWriteLockDao<E extends PersistentApiWriteLock, T extends Serializable > extends NosqlDao<E, T> {

}
