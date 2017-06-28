package eu.europeana.annotation.mongo.dao;

import java.io.Serializable;
import java.util.List;

import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.mongo.model.internal.PersistentAnnotation;
import eu.europeana.api.commons.nosql.dao.NosqlDao;

/**
 * This dao is used for generating AnnotationIds automatically
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
	 * @param provider
	 * @return AnnotationId object
	 */
	AnnotationId generateNextAnnotationId(String provider);
	
	List<AnnotationId> generateNextAnnotationIds(String provider, Integer sequenceIncrement);
	
	Long getLastAnnotationNr(String provider);
	
}
