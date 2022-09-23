package eu.europeana.annotation.mongo.dao;

import java.io.Serializable;

import eu.europeana.annotation.mongo.model.internal.PersistentStatusLog;
import eu.europeana.api.commons.nosql.dao.NosqlDao;

/**
 *
 * @param <E>
 * @param <T>
 */
public interface PersistentStatusLogDao<E extends PersistentStatusLog, T extends Serializable > extends NosqlDao<E, T>{

}
