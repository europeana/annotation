package eu.europeana.annotation.mongo.service;

import java.util.List;

import javax.annotation.Resource;

import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.QueryResults;

import eu.europeana.annotation.definitions.exception.AnnotationValidationException;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.ImageAnnotation;
import eu.europeana.annotation.definitions.model.body.TagBody;
import eu.europeana.annotation.definitions.model.vocabulary.BodyTypes;
import eu.europeana.annotation.definitions.model.vocabulary.TagTypes;
import eu.europeana.annotation.mongo.dao.PersistentAnnotationDao;
import eu.europeana.annotation.mongo.exception.AnnotationMongoException;
import eu.europeana.annotation.mongo.model.MongoAnnotationId;
import eu.europeana.annotation.mongo.model.PersistentTagImpl;
import eu.europeana.annotation.mongo.model.internal.PersistentAnnotation;
import eu.europeana.annotation.mongo.model.internal.PersistentTag;
import eu.europeana.corelib.db.service.abstracts.AbstractNoSqlServiceImpl;

public class PersistentAnnotationServiceImpl extends AbstractNoSqlServiceImpl<PersistentAnnotation, String> implements PersistentAnnotationService {

	@Resource
	private PersistentTagService tagService;
	
	/**
	 * This method shouldn't be public but protected. Anyway, it is forced to be public by the supper implementation
	 * @throws AnnotationValidationException - see AnnotationValidationException.ERROR_NULL_EUROPEANA_ID
	 */
	@Override
	public PersistentAnnotation store(PersistentAnnotation object) {
		if(object.getAnnotatedAtTs() == null){
			object.setAnnotatedAtTs(System.currentTimeMillis());
			object.setSerializedAtTs(object.getAnnotatedAtTs());
			
		}
		
		//check Europeana ID
		if(object.getId() != null)
			throw new AnnotationValidationException(AnnotationValidationException.ERROR_NOT_NULL_OBJECT_ID);
				
		//check target
		if(object.getHasTarget() == null)
			throw new AnnotationValidationException(AnnotationValidationException.ERROR_NULL_TARGET);
		else if(object.getHasTarget().getEuropeanaId() == null)
			throw new AnnotationValidationException(AnnotationValidationException.ERROR_NULL_EUROPEANA_ID);
		
		//check AnnotatedBy (creator)
		if(object.getAnnotatedBy() == null || object.getAnnotatedBy().getName() == null)
			throw new AnnotationValidationException(AnnotationValidationException.ERROR_NULL_ANNOTATED_BY);
		
		//check body
		//note: Bookmarks or Highlights may not have a body .. but they are not supported yet
		if(object.getHasBody() == null)
			throw new AnnotationValidationException(AnnotationValidationException.ERROR_NULL_EUROPEANA_ID);
			
		//check if TAG
		if(hasTagBody(object)){
			
			TagBody body = (TagBody)object.getHasBody();
			PersistentTag tag = findOrCreateTag(object, body);
			//set tagId
			body.setTagId(tag.getId().toString()); 
		}
		
		AnnotationId annoId = getAnnotationDao().generateNextAnnotationId(object.getHasTarget().getEuropeanaId());
		
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
				query.setTagType(TagTypes.SIMPLE_TAG);
			else if(BodyTypes.isSemanticTagBody(body.getBodyType()))
				query.setTagType(TagTypes.SEMANTIC_TAG);
			
			try {
				tag = tagService.find(query);
				if(tag == null){
					query.setCreator(object.getAnnotatedBy().getName() + " : " + object.getAnnotatedBy().getOpenId());
					tag = tagService.create(query);
				}
				
			} catch (AnnotationMongoException e) {
				throw new AnnotationValidationException("Cannot validate Tag Body", e);
			}
		}
		return tag;
	}
	
	

	protected boolean hasTagBody(PersistentAnnotation object) {
		return BodyTypes.isTagBody(object.getHasBody().getBodyType());
	}

	@Override
	public Annotation store(Annotation object) {
		return this.store((PersistentAnnotation)object);
	}
	
//	@Override
//	public SemanticTag store(SemanticTag object) {
//		return (SemanticTag) this.store((PersistentAnnotation)object);
//	}
	
	@Override
	public ImageAnnotation store(ImageAnnotation object) {
		return (ImageAnnotation) this.store((PersistentAnnotation) object);
	}
	
	protected PersistentAnnotationDao<PersistentAnnotation, String> getAnnotationDao(){
		return (PersistentAnnotationDao<PersistentAnnotation, String>) getDao();
	}
	
	public List<? extends Annotation> getAnnotationList(String europeanaId) {
		Query<PersistentAnnotation> query = getAnnotationDao().createQuery();
		query.filter(PersistentAnnotation.FIELD_EUROPEANA_ID, europeanaId);
		QueryResults<? extends PersistentAnnotation> results = getAnnotationDao().find(query);
		return results.asList();		
	}

	@Override
	public PersistentAnnotation getAnnotation(String europeanaId, Integer annotationNr) {
		Query<PersistentAnnotation> query = getAnnotationDao().createQuery();
		query.filter(PersistentAnnotation.FIELD_EUROPEANA_ID, europeanaId);
		query.filter(PersistentAnnotation.FIELD_ANNOTATION_NR, annotationNr);
		
		return getAnnotationDao().findOne(query);
	}
}
