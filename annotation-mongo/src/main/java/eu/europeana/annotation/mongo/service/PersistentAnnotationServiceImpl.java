package eu.europeana.annotation.mongo.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.bson.types.ObjectId;

import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.QueryResults;
import com.google.code.morphia.query.UpdateOperations;

import eu.europeana.annotation.definitions.exception.AnnotationValidationException;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.ImageAnnotation;
import eu.europeana.annotation.definitions.model.ObjectTag;
import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.body.TagBody;
import eu.europeana.annotation.definitions.model.utils.TypeUtils;
import eu.europeana.annotation.definitions.model.vocabulary.BodyTypes;
import eu.europeana.annotation.definitions.model.vocabulary.TagTypes;
import eu.europeana.annotation.mongo.dao.PersistentAnnotationDao;
import eu.europeana.annotation.mongo.exception.AnnotationMongoException;
import eu.europeana.annotation.mongo.model.MongoAnnotationId;
import eu.europeana.annotation.mongo.model.PersistentTagImpl;
import eu.europeana.annotation.mongo.model.internal.PersistentAnnotation;
import eu.europeana.annotation.mongo.model.internal.PersistentTag;
import eu.europeana.corelib.db.service.abstracts.AbstractNoSqlServiceImpl;

public class PersistentAnnotationServiceImpl extends AbstractNoSqlServiceImpl<PersistentAnnotation, String> 
	implements PersistentAnnotationService {

	@Resource
	private PersistentTagService tagService;
	
	/**
	 * This method shouldn't be public but protected. Anyway, it is forced to be public by the supper implementation
	 * @throws AnnotationValidationException - see AnnotationValidationException.ERROR_NULL_EUROPEANA_ID
	 */
	@Override
	public PersistentAnnotation store(PersistentAnnotation object) {
		if(object.getAnnotatedAt() == null){
			object.setAnnotatedAt(new Date());
			object.setSerializedAt(object.getAnnotatedAt());
			
		}
		
		//check Europeana ID
		if(object.getId() != null)
			throw new AnnotationValidationException(AnnotationValidationException.ERROR_NOT_NULL_OBJECT_ID);
				
		//check target
		if(object.getTarget() == null)
			throw new AnnotationValidationException(AnnotationValidationException.ERROR_NULL_TARGET);
		else if(object.getTarget().getEuropeanaId() == null)
			throw new AnnotationValidationException(AnnotationValidationException.ERROR_NULL_EUROPEANA_ID);
		
		//check AnnotatedBy (creator)
		if(object.getAnnotatedBy() == null || object.getAnnotatedBy().getName() == null)
			throw new AnnotationValidationException(AnnotationValidationException.ERROR_NULL_ANNOTATED_BY);
		
		//check body
		//note: Bookmarks or Highlights may not have a body .. but they are not supported yet
		if(object.getBody() == null)
			throw new AnnotationValidationException(AnnotationValidationException.ERROR_NULL_EUROPEANA_ID);
			
		//check if TAG
		if(hasTagBody(object)){
			
			TagBody body = (TagBody)object.getBody();
			PersistentTag tag = findOrCreateTag(object, body);
			//set tagId
			body.setTagId(tag.getId().toString()); 
		}
		
		AnnotationId annoId = getAnnotationDao().generateNextAnnotationId(object.getTarget().getEuropeanaId());
		
		MongoAnnotationId embeddedId = new MongoAnnotationId();
		embeddedId.copyFrom(annoId);
		
		object.setAnnotationId(embeddedId);
		
		return super.store(object);
	}


	private PersistentTag findOrCreateTag(PersistentAnnotation object,
			TagBody body) {
		
		PersistentTag tag = null;
		
		//check if tag exists or create it
		if(body.getTagId() == null){
			PersistentTag query = new PersistentTagImpl();
			body.copyInto(query);
//				query.setValue(body.getValue());
//				query.setHttpUri(body.getHttpUri());
			if(BodyTypes.isSimpleTagBody(body.getBodyType()))
				query.setTagTypeEnum(TagTypes.SIMPLE_TAG);
			else if(BodyTypes.isSemanticTagBody(body.getBodyType()))
				query.setTagTypeEnum(TagTypes.SEMANTIC_TAG);
			
			try {
				tag = tagService.find(query);
				if(tag == null){
					query.setCreator(object.getAnnotatedBy().getName() + " : " + object.getAnnotatedBy().getOpenId());
					tag = tagService.create(query);
				}
				
			} catch (AnnotationMongoException e) {
				throw new AnnotationValidationException("Cannot read tag from database", e);
			}
		}
		if (tag.getId() == null && tag.getObjectId() != null)
			((PersistentTagImpl) tag).setId(tag.getObjectId().toString());
		return tag;
	}	

	protected boolean hasTagBody(PersistentAnnotation object) {
		String euType = new TypeUtils().getEuTypeFromTypeArray(object.getBody().getBodyType());
		return BodyTypes.isTagBody(euType);
	}

	@Override
	public Annotation store(Annotation object) {
		return this.store((PersistentAnnotation)object);
	}
	
	@Override
	public ObjectTag store(ObjectTag object) {
		return (ObjectTag) this.store((PersistentAnnotation)object);
	}
	
	@Override
	public ImageAnnotation store(ImageAnnotation object) {
		return (ImageAnnotation) this.store((PersistentAnnotation) object);
	}
	
	protected PersistentAnnotationDao<PersistentAnnotation, String> getAnnotationDao(){
		return (PersistentAnnotationDao<PersistentAnnotation, String>) getDao();
	}
	
	public List<? extends Annotation> getAnnotationList(String europeanaId) {
		return getAnnotationListFilteredByDisabled(europeanaId, false);		
	}

	public List<? extends Annotation> getAnnotationListFilteredByDisabled(String europeanaId, boolean isDisabled) {
		Query<PersistentAnnotation> query = getAnnotationDao().createQuery();
		query.filter(PersistentAnnotation.FIELD_EUROPEANA_ID, europeanaId);
		query.filter(PersistentAnnotation.FIELD_DISABLED, isDisabled);
		QueryResults<? extends PersistentAnnotation> results = getAnnotationDao().find(query);
		return results.asList();		
	}

	@Override
	public PersistentAnnotation find(String europeanaId, Integer annotationNr) {
		Query<PersistentAnnotation> query = getAnnotationDao().createQuery();
		query.filter(PersistentAnnotation.FIELD_EUROPEANA_ID, europeanaId);
		query.filter(PersistentAnnotation.FIELD_ANNOTATION_NR, annotationNr);
		
		return getAnnotationDao().findOne(query);
	}
	
	@Override
	public PersistentAnnotation find(AnnotationId annoId) {
		return find(annoId.getResourceId(), annoId.getAnnotationNr());
	}

	@Override
	public PersistentAnnotation findByID(String id) {
			return  getDao(). findOne("_id", new ObjectId(id));
	}
	
	@Override
	public void remove(String id) {
		//TODO use delete by query		
		PersistentAnnotation annotation = findByID(id);
		getDao().delete(annotation);
		
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see eu.europeana.annotation.mongo.service.PersistentAnnotationService#remove(java.lang.String, java.lang.Integer)
	 */
	public void remove(String resourceId, Integer annotationNr) {
//		String objectId = findObjectId(resourceId, annotationNr); 
		Query<PersistentAnnotation> query = getAnnotationDao().createQuery();
		query.filter(PersistentAnnotation.FIELD_EUROPEANA_ID, resourceId);
		query.filter(PersistentAnnotation.FIELD_ANNOTATION_NR, annotationNr);
	
		getDao().deleteByQuery(query);
	}



//	private String findObjectId(String resourceId, Integer annotationNr) {
//		Query<PersistentAnnotation> query = getAnnotationDao().createQuery();
//		query.filter(PersistentAnnotation.FIELD_EUROPEANA_ID, resourceId);
//		query.filter(PersistentAnnotation.FIELD_ANNOTATION_NR, annotationNr);
//		@SuppressWarnings("rawtypes")
//		List ids = getDao().findIds();
//		if(ids.size() != 1)
//			throw new AnnotationMongoRuntimeException("Expected one object but found:" + ids.size());
//	
//		ObjectId id = (ObjectId)ids.get(0);
//		return id.toString();		
//	}



	@Override
	public void remove(AnnotationId annoId) {
		remove(annoId.getResourceId(), annoId.getAnnotationNr());		
	}

	@Override
	public Annotation update(Annotation object) {
		
		Annotation res = null;
		
		PersistentAnnotation persistentAnnotation = (PersistentAnnotation) object;
		
		if (persistentAnnotation != null && persistentAnnotation.getAnnotationId() != null) {
			remove(persistentAnnotation.getAnnotationId().getResourceId(), persistentAnnotation.getAnnotationId().getAnnotationNr());	
			persistentAnnotation.setId(null);
			if (persistentAnnotation.getBody() != null) {
				((TagBody) persistentAnnotation.getBody()).setTagId(null);
			}
			res = store(persistentAnnotation);
		} else {
			throw new AnnotationValidationException(AnnotationValidationException.ERROR_NULL_ANNOTATION_ID);
		}
		
		return res;
	}

	@Override
	public Annotation updateIndexingTime(AnnotationId annoId) {
		
		Annotation res = null;
		
		PersistentAnnotation annotation = find(annoId);
		
		if (annotation != null && annotation.getLastIndexedTimestamp() == null) {
			annotation.setLastIndexedTimestamp(System.currentTimeMillis());
			UpdateOperations<PersistentAnnotation> ops = getAnnotationDao().createUpdateOperations()
					.set(WebAnnotationFields.LAST_INDEXED_TIMESTAMP, annotation.getLastIndexedTimestamp());
			Query<PersistentAnnotation> updateQuery = getAnnotationDao().createQuery().field("_id").equal(annotation.getId());
			getAnnotationDao().update(updateQuery, ops);
			res = annotation; //update(annotation);
		}
		
		return res;
	}
	
}
