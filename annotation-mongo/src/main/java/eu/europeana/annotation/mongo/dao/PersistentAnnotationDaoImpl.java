package eu.europeana.annotation.mongo.dao;

import java.io.Serializable;

import javax.annotation.Resource;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import eu.europeana.annotation.config.AnnotationConfiguration;
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.mongo.model.internal.GeneratedAnnotationIdImpl;
import eu.europeana.annotation.mongo.model.internal.PersistentAnnotation;
import eu.europeana.api.commons.nosql.dao.impl.NosqlDaoImpl;

public class PersistentAnnotationDaoImpl<E extends PersistentAnnotation, T extends Serializable>
		extends NosqlDaoImpl<E, T> implements PersistentAnnotationDao<E, T> {

	@Resource
	private AnnotationConfiguration configuration;
	
	public AnnotationConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(AnnotationConfiguration configuration) {
		this.configuration = configuration;
	}

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
			// search annotationId and get incremented annotation number 
			nextAnnotationId = getDatastore().findAndModify(q, uOps);
			
			if (nextAnnotationId == null) {
				// if first annotationId
				nextAnnotationId = new GeneratedAnnotationIdImpl( getConfiguration().getAnnotationBaseUrl(), provider, ""+1L);
				ds.save(nextAnnotationId);
			}else{
				nextAnnotationId.setProvider(provider);
			}
		}

		return nextAnnotationId;
	}

}
