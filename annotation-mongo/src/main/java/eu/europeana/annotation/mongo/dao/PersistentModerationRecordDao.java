package eu.europeana.annotation.mongo.dao;

import java.io.Serializable;

import eu.europeana.annotation.mongo.model.internal.PersistentModerationRecord;
import eu.europeana.api.commons.nosql.dao.NosqlDao;

/**
 *
 * @param <E>
 * @param <T>
 */
public interface PersistentModerationRecordDao<E extends PersistentModerationRecord, T extends Serializable > extends NosqlDao<E, T>{

}
