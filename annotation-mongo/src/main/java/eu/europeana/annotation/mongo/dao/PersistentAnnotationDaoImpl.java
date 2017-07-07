package eu.europeana.annotation.mongo.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteError;
import com.mongodb.BulkWriteException;
import com.mongodb.BulkWriteOperation;
import com.mongodb.DBObject;

import eu.europeana.annotation.config.AnnotationConfiguration;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.mongo.batch.BulkOperationMode;
import eu.europeana.annotation.mongo.exception.BulkOperationException;
import eu.europeana.annotation.mongo.model.internal.GeneratedAnnotationIdImpl;
import eu.europeana.annotation.mongo.model.internal.PersistentAnnotation;
import eu.europeana.api.commons.nosql.dao.impl.NosqlDaoImpl;
import org.apache.log4j.Logger;


public class PersistentAnnotationDaoImpl<E extends PersistentAnnotation, T extends Serializable>
		extends NosqlDaoImpl<E, T> implements PersistentAnnotationDao<E, T> {

	@Resource
	private AnnotationConfiguration configuration;
	
	protected final Logger logger = Logger.getLogger(this.getClass());
	
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
	
	public void applyBulkOperation(List<? extends Annotation> annos, List<Integer> exceptIndices, BulkOperationMode bulkOpMode) throws BulkOperationException {
		List<Annotation> filteredList = new ArrayList<Annotation>();
		
		for(int i = 0; i < annos.size(); i++) {
			if(!exceptIndices.contains(i))
				filteredList.add(annos.get(i));
		}
		applyBulkOperation(filteredList, bulkOpMode);
	}
	

	/**
	 * Apply Create/Update/Delete bulk operations.
	 * @param annos List of annotations
	 * @param bulkOpMode Update mode: Create/Update/Delete
	 * @throws BulkOperationException
	 */
	public void applyBulkOperation(List<? extends Annotation> annos, BulkOperationMode bulkOpMode) throws BulkOperationException {
		@SuppressWarnings("unchecked")
		Class<PersistentAnnotation> entityClass = (Class<PersistentAnnotation>) this.getEntityClass();
		BulkWriteOperation bulkWrite = getDatastore().getCollection(entityClass).initializeOrderedBulkOperation();
		Morphia morphia = new Morphia();  
		DBObject dbObject;
		DBObject query;
		for (Annotation anno : annos) {
          dbObject = morphia.toDBObject(anno);
          switch(bulkOpMode) {
          case INSERT:
        	  bulkWrite.insert(dbObject);
        	  break;
          case UPDATE:
        	  query = new BasicDBObject();
	          query.put(Mapper.ID_KEY, dbObject.get(Mapper.ID_KEY));
	          bulkWrite.find(query).replaceOne(dbObject);
        	  break;
          case DELETE:
        	  query = new BasicDBObject();
	          query.put(Mapper.ID_KEY, dbObject.get(Mapper.ID_KEY));
	          bulkWrite.find(query).removeOne();
        	  break;
          }
		}
		try {
			  bulkWrite.execute();
		} catch (BulkWriteException e) {
			List<BulkWriteError> bulkWriteErrors = e.getWriteErrors();
			List<Integer> failedIndices = new ArrayList<Integer>();
			for (BulkWriteError bulkWriteError : bulkWriteErrors) {
				int failedIndex = bulkWriteError.getIndex();
				failedIndices.add(failedIndex);
				Annotation failedEntity = annos.get(failedIndex);
				logger.error("Batch upload failed: " + failedEntity);
			}
			throw new BulkOperationException("Bulk write operation failed", failedIndices, e);
		} catch (Exception e) {
			throw new BulkOperationException("Bulk operation failed", e);
		}
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
