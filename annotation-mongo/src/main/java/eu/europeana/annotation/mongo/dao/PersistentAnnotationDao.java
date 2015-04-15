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

	/**
	 * This method sets default provider. Currently there are two providers:
	 *    1. 'webanno' e.g. "http://data.europeana.eu/annotations/15502/GG_8285/webanno/1"
	 *    2. 'historypin' e.g. "http://historypin.com/annotation/1234"
	 * @param europeanaId
	 * @return AnnotationId object
	 */
	AnnotationId generateNextAnnotationId(String europeanaId);
	
}
