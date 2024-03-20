package eu.europeana.annotation.mongo.dao;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.mongodb.AggregationOptions;
import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteError;
import com.mongodb.BulkWriteException;
import com.mongodb.BulkWriteOperation;
import com.mongodb.BulkWriteResult;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.mapping.Mapper;
import dev.morphia.query.Query;
import dev.morphia.query.UpdateOperations;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.mongo.batch.BulkOperationMode;
import eu.europeana.annotation.mongo.exception.BulkOperationException;
import eu.europeana.annotation.mongo.model.internal.GeneratedAnnotationIdentifierImpl;
import eu.europeana.annotation.mongo.model.internal.PersistentAnnotation;
import eu.europeana.api.commons.nosql.dao.impl.NosqlDaoImpl;

public class PersistentAnnotationDaoImpl<E extends PersistentAnnotation, T extends Serializable>
    extends NosqlDaoImpl<E, T> implements PersistentAnnotationDao<E, T> {

  protected final Logger logger = LogManager.getLogger(this.getClass());


  public PersistentAnnotationDaoImpl(Class<E> clazz, Datastore datastore) {
    super(datastore, clazz);
  }

  @SuppressWarnings("deprecation")
  public long generateNextAnnotationIdentifier() {

    GeneratedAnnotationIdentifierImpl nextAnnotationIdentifier = null;

    synchronized (this) {
      Query<GeneratedAnnotationIdentifierImpl> q =
          getDatastore().createQuery(GeneratedAnnotationIdentifierImpl.class);
      q.filter("_id", WebAnnotationFields.DEFAULT_PROVIDER);

      UpdateOperations<GeneratedAnnotationIdentifierImpl> uOps =
          getDatastore().createUpdateOperations(GeneratedAnnotationIdentifierImpl.class)
              .inc(GeneratedAnnotationIdentifierImpl.SEQUENCE_COLUMN_NAME);
      // search annotationId and get incremented annotation number
      nextAnnotationIdentifier = getDatastore().findAndModify(q, uOps);

      if (nextAnnotationIdentifier == null) {
        // if first annotationId
        nextAnnotationIdentifier =
            new GeneratedAnnotationIdentifierImpl(WebAnnotationFields.DEFAULT_PROVIDER, 1L);
        ds.save(nextAnnotationIdentifier);
      }
    }

    return nextAnnotationIdentifier.getAnnotationNr();
  }


  /**
   * Generate a sequence of annotation ids
   * 
   * @param provider The name of the provider
   * @param sequenceLength The length of the id sequence to be created
   */
  public List<Long> generateNextAnnotationIdentifiers(Integer sequenceLength) {

    List<Long> nextAnnotationIds = new ArrayList<Long>(sequenceLength);

    GeneratedAnnotationIdentifierImpl annoId = null;

    synchronized (this) {
      // synchronized (provider) {

      Query<GeneratedAnnotationIdentifierImpl> q =
          getDatastore().createQuery(GeneratedAnnotationIdentifierImpl.class);
      // q.filter("_id", provider);

      UpdateOperations<GeneratedAnnotationIdentifierImpl> uOps =
          getDatastore().createUpdateOperations(GeneratedAnnotationIdentifierImpl.class)
              .inc(GeneratedAnnotationIdentifierImpl.SEQUENCE_COLUMN_NAME, sequenceLength);

      // search annotationId and if it exists increment annotation number by the given sequence
      annoId = getDatastore().findAndModify(q, uOps);

      // no annotation id in collection for the given provider, therefore a new object is created
      if (annoId == null) {
        annoId = new GeneratedAnnotationIdentifierImpl(WebAnnotationFields.DEFAULT_PROVIDER,
            sequenceLength.longValue());
        getDatastore().save(annoId);
      }
      // generating a sequence of annotation ids
      Long firstNrOfSequence = annoId.getAnnotationNr() - sequenceLength + 1;
      for (int i = 0; i < sequenceLength; i++) {
        nextAnnotationIds.add(firstNrOfSequence + i);
      }
    }
    return nextAnnotationIds;
  }

  public BulkWriteResult applyBulkOperation(List<? extends Annotation> annos,
      List<Integer> exceptIndices, BulkOperationMode bulkOpMode) throws BulkOperationException {
    List<Annotation> filteredList = new ArrayList<Annotation>();

    for (int i = 0; i < annos.size(); i++) {
      if (!exceptIndices.contains(i))
        filteredList.add(annos.get(i));
    }
    return applyBulkOperation(filteredList, bulkOpMode);
  }


  /**
   * Apply Create/Update/Delete bulk operations.
   * 
   * @param annos List of annotations
   * @param bulkOpMode Update mode: Create/Update/Delete
   * @throws BulkOperationException
   */
  public BulkWriteResult applyBulkOperation(List<? extends Annotation> annos,
      BulkOperationMode bulkOpMode) throws BulkOperationException {
    @SuppressWarnings("unchecked")
    Class<PersistentAnnotation> entityClass = (Class<PersistentAnnotation>) this.getEntityClass();
    BulkWriteOperation bulkWrite =
        getDatastore().getCollection(entityClass).initializeOrderedBulkOperation();
    Morphia morphia = new Morphia();
    DBObject dbObject;
    DBObject query;
    for (Annotation anno : annos) {
      // anno.setLastUpdate(new Date());
      dbObject = morphia.toDBObject(anno);
      switch (bulkOpMode) {
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
      BulkWriteResult result = bulkWrite.execute();
      if (bulkOpMode.equals(BulkOperationMode.UPDATE) && (result.getModifiedCount() < annos.size()
          || result.getMatchedCount() < annos.size())) {
        String errMsg = "Bulk write operation failed (%d out of $d succeeded)";
        throw new BulkOperationException(
            String.format(errMsg, result.getMatchedCount(), annos.size()), null);
      }
      return result;
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
   * 
   * @param provider ID of the provider
   * @return Last annotation number
   */
  public Long getLastAnnotationNr() {
    // public Long getLastAnnotationNr(String provider) {
    List<GeneratedAnnotationIdentifierImpl> res =
        getDatastore().createQuery(GeneratedAnnotationIdentifierImpl.class)
            // .filter("_id", provider)
            .asList();
    if (res.size() == 1) {
      GeneratedAnnotationIdentifierImpl genAnnoId = res.get(0);
      return genAnnoId.getAnnotationNr();
    } else
      return 0L;
  }

  /**
   * Copy annotations from a source collection to a target collection
   * 
   * @param existingAnnos Annotations to be copied
   * @param sourceCollection Source collection
   * @param targetCollection Target collection
   * @throws IOException
   * @throws InterruptedException
   */
  @SuppressWarnings({"deprecation", "unchecked"})
  @Override
  public void copyAnnotations(List<? extends Annotation> existingAnnos, String sourceCollection,
      String targetCollection) throws IOException, InterruptedException {
    List<DBObject> ops = new ArrayList<DBObject>();
    Query<PersistentAnnotation> query = (Query<PersistentAnnotation>) createQuery();
    List<Long> annoIdentifiers =
        existingAnnos.stream().map(Annotation::getIdentifier).collect(Collectors.toList());
    query.filter(PersistentAnnotation.FIELD_IDENTIFIER + " in", annoIdentifiers);
    ops.add(new BasicDBObject("$match", query.getQueryObject()));
    ops.add(new BasicDBObject("$out", targetCollection));
    DBCollection source = getDatastore().getDB().getCollection(sourceCollection);
    source.aggregate(ops, getDefaultAggregationOptions());

  }

}
