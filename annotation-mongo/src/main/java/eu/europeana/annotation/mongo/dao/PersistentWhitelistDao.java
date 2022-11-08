package eu.europeana.annotation.mongo.dao;

import java.io.Serializable;

import eu.europeana.annotation.mongo.model.internal.PersistentWhitelistEntry;
import eu.europeana.api.commons.nosql.dao.NosqlDao;

/**
 *
 * @param <E>
 * @param <T>
 */
public interface PersistentWhitelistDao<E extends PersistentWhitelistEntry, T extends Serializable > extends NosqlDao<E, T> {

}
