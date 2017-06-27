package eu.europeana.annotation.mongo.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

			Query<GeneratedAnnotationIdImpl> q = getDatastore().createQuery(GeneratedAnnotationIdImpl.class);
			q.filter("_id", provider);
			
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
				nextAnnotationId.setBaseUrl(getConfiguration().getAnnotationBaseUrl());
			}
		}

		return nextAnnotationId;
	}
	

	/**
	 * Generate a sequence of annotation ids
	 * @param provider The name of the provider
	 * @param sequenceLength The length of the id sequence to be created
	 */
	public List<AnnotationId> generateNextAnnotationIds(String provider, Integer sequenceLength) {
		
		List<AnnotationId> nextAnnotationIds = new ArrayList<AnnotationId>(sequenceLength);

		GeneratedAnnotationIdImpl annoId = null;

		synchronized (provider) {
			
			Query<GeneratedAnnotationIdImpl> q = getDatastore().createQuery(GeneratedAnnotationIdImpl.class);
			q.filter("_id", provider);
			
			UpdateOperations<GeneratedAnnotationIdImpl> uOps = getDatastore()
					.createUpdateOperations(GeneratedAnnotationIdImpl.class)
					.inc(GeneratedAnnotationIdImpl.SEQUENCE_COLUMN_NAME, sequenceLength);
			
			// search annotationId and if it exists increment annotation number by the given sequence 
			annoId = getDatastore().findAndModify(q, uOps);
			
			// no annotation id in collection for the given provider, therefore a new object is created
			if (annoId == null) {
				annoId = new GeneratedAnnotationIdImpl( getConfiguration().getAnnotationBaseUrl(), provider, ""+sequenceLength.toString());
				getDatastore().save(annoId);
			}
			// generating a sequence of annotation ids 
			Long firstNrOfSequence = annoId.getAnnotationNr() - sequenceLength;
			for(int i = 0; i < sequenceLength; i++) {
				Long newAnnoIdNr = (firstNrOfSequence + i + 1);
				annoId = new GeneratedAnnotationIdImpl( getConfiguration().getAnnotationBaseUrl(), provider, ""+newAnnoIdNr.toString());
				annoId.setProvider(provider);
				annoId.setBaseUrl(getConfiguration().getAnnotationBaseUrl());
				nextAnnotationIds.add(annoId);
			}
		}
		return nextAnnotationIds;
	}
	
	/**
	 * Get last annotation number for a given provider
	 * @param provider ID of the provider
	 * @return Last annotation number
	 */
	public Long getLastAnnotationNr(String provider) {
		List<GeneratedAnnotationIdImpl> res = getDatastore().createQuery(GeneratedAnnotationIdImpl.class)
                .filter("_id", provider)                
                .asList();
		if(res.size() == 1) {
			GeneratedAnnotationIdImpl genAnnoId = res.get(0);
			return genAnnoId.getAnnotationNr();
		} else
			return 0L;
	}

}
