package eu.europeana.annotation.mongo.dao;

import java.io.Serializable;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;

import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.mongo.model.internal.GeneratedAnnotationIdImpl;
import eu.europeana.annotation.mongo.model.internal.PersistentAnnotation;
import eu.europeana.corelib.db.dao.impl.NosqlDaoImpl;

public class PersistentAnnotationDaoImpl<E extends PersistentAnnotation, T extends Serializable>
		extends NosqlDaoImpl<E, T> implements PersistentAnnotationDao<E, T> {

	public PersistentAnnotationDaoImpl(Class<E> clazz, Datastore datastore) {
		super(datastore, clazz);
	}

	public AnnotationId generateNextAnnotationId(String provider) {

		GeneratedAnnotationIdImpl nextAnnotationId = null;

		synchronized (provider) {

			Query<GeneratedAnnotationIdImpl> q = getDatastore().find(
					GeneratedAnnotationIdImpl.class, "_id", provider);
			
			UpdateOperations<GeneratedAnnotationIdImpl> uOps = getDatastore()
					.createUpdateOperations(GeneratedAnnotationIdImpl.class)
					.inc(GeneratedAnnotationIdImpl.SEQUENCE_COLUMN_NAME);
			// search annotationId and increment
			nextAnnotationId = getDatastore().findAndModify(q, uOps);

			// if first annotationId
			if (nextAnnotationId == null) {
				nextAnnotationId = new GeneratedAnnotationIdImpl(provider, ""+1L);
				ds.save(nextAnnotationId);
			}
		}

		return nextAnnotationId;
	}

}
