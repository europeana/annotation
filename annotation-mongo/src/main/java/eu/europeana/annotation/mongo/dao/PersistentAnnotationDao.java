package eu.europeana.annotation.mongo.dao;

import java.io.Serializable;

import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.mongo.model.internal.PersistentAnnotation;
import eu.europeana.corelib.db.dao.NosqlDao;

/**
 * Not sure if this DAO is needed. It will be marked as deprecated until the first mehtod is defined here
 * @author Sergiu Gordea 
 *
 * @param <E>
 * @param <T>
 */
public interface PersistentAnnotationDao<E extends PersistentAnnotation, T extends Serializable > extends NosqlDao<E, T> {

	AnnotationId generateNextAnnotationId(String europeanaId);

}
