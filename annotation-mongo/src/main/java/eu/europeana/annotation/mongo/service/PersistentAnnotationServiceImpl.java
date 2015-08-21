package eu.europeana.annotation.mongo.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;

import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.QueryResults;
import com.google.code.morphia.query.UpdateOperations;

import eu.europeana.annotation.definitions.exception.AnnotationAttributeInstantiationException;
import eu.europeana.annotation.definitions.exception.AnnotationUpdateException;
import eu.europeana.annotation.definitions.exception.AnnotationValidationException;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.ImageAnnotation;
import eu.europeana.annotation.definitions.model.ObjectTag;
import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.body.TagBody;
import eu.europeana.annotation.definitions.model.utils.AnnotationBuilder;
import eu.europeana.annotation.definitions.model.vocabulary.AnnotationTypes;
import eu.europeana.annotation.definitions.model.vocabulary.BodyTypes;
import eu.europeana.annotation.definitions.model.vocabulary.TagTypes;
import eu.europeana.annotation.mongo.dao.PersistentAnnotationDao;
import eu.europeana.annotation.mongo.exception.AnnotationMongoException;
import eu.europeana.annotation.mongo.factory.PersistentAnnotationFactory;
import eu.europeana.annotation.mongo.model.MongoAnnotationId;
import eu.europeana.annotation.mongo.model.PersistentAnnotationImpl;
import eu.europeana.annotation.mongo.model.PersistentTagImpl;
import eu.europeana.annotation.mongo.model.internal.PersistentAnnotation;
import eu.europeana.annotation.mongo.model.internal.PersistentTag;
import eu.europeana.corelib.db.service.abstracts.AbstractNoSqlServiceImpl;

public class PersistentAnnotationServiceImpl extends
		AbstractNoSqlServiceImpl<PersistentAnnotation, String> implements
		PersistentAnnotationService {

	@Resource
	private PersistentTagService tagService;
	
	AnnotationBuilder annotationBuilder = new AnnotationBuilder();

	public AnnotationBuilder getAnnotationBuilder() {
		return annotationBuilder;
	}

	public void setAnnotationBuilder(AnnotationBuilder annotationBuilder) {
		this.annotationBuilder = annotationBuilder;
	}

	/**
	 * This method shouldn't be public but protected. Anyway, it is forced to be
	 * public by the supper implementation
	 * 
	 * @throws AnnotationValidationException
	 *             - see AnnotationValidationException.ERROR_NULL_EUROPEANA_ID
	 */
	@Override
	public PersistentAnnotation store(PersistentAnnotation object) {
		Date now = new Date();
		validatePersistentAnnotation(object, now);
		object.setLastUpdate(now);
		return super.store(object);
	}

	private void validatePersistentAnnotation(PersistentAnnotation object, Date now) {
		validatePersistentAnnotationExt(object, now, true);
	}
	
	/**
	 * This method validates persistent annotation and generates ID if genarateId=true.
	 * @param object
	 * @param now
	 * @param generateId
	 */
	private void validatePersistentAnnotationExt(PersistentAnnotation object, Date now, boolean generateId) {
			
		if (object.getAnnotatedAt() == null)
			object.setAnnotatedAt(now);
		
		if(object.getSerializedAt() == null)
			object.setSerializedAt(now);
		
		// check Europeana ID
		if (generateId && object.getId() != null)
			throw new AnnotationValidationException(
					AnnotationValidationException.ERROR_NOT_NULL_OBJECT_ID);
		
		//TODO: check and remove the following code .. 
		//if (object.getInternalType().equals(AnnotationTypes.OBJECT_LINKING.name())) {
		//check Annotation ID
//		if (object.getAnnotationId() == null){ 
//				throw new AnnotationValidationException(
//						"AnnotationId must not be null.");
//		} else 
		
		if (object.getAnnotationId() == null || StringUtils.isEmpty(object.getAnnotationId().getProvider())) 
				throw new AnnotationValidationException(
						"AnnotationId must not be null. AnnotationId.provider attribute is required");
		//}
		
		// check target
		if (object.getTarget() == null)
			throw new AnnotationValidationException(
					AnnotationValidationException.ERROR_NULL_TARGET);

		// check AnnotatedBy (creator)
		if (object.getAnnotatedBy() == null
				|| object.getAnnotatedBy().getName() == null)
			throw new AnnotationValidationException(
					AnnotationValidationException.ERROR_NULL_ANNOTATED_BY);

		if (!object.getInternalType().equals(AnnotationTypes.OBJECT_LINKING.name())) {
			// check body
			// note: Bookmarks or Highlights may not have a body .. but they are not
			// supported yet
			if (object.getBody() == null)
				throw new AnnotationValidationException(
						AnnotationValidationException.ERROR_NULL_EUROPEANA_ID);
	
			// check if TAG
			if (hasTagBody(object)) {	
				TagBody body = (TagBody) object.getBody();
				if (body.getTagId() == null) {
					PersistentTag tag = findOrCreateTag(object, body);
					// set tagId
					body.setTagId(tag.getId().toString());
				}
			}
		}
		
		if (generateId) {
			MongoAnnotationId embeddedId = initializeAnnotationId(object);
			object.setAnnotationId(embeddedId);
		}
		
		//validate annotation NR
		//TODO: update this condition when specification is available
		if(Long.parseLong(object.getAnnotationId().getIdentifier()) < 1)
			throw new AnnotationValidationException("Annotaion.AnnotationId.identifier must be a positive number!");
	}

	MongoAnnotationId initializeAnnotationId(PersistentAnnotation object) {
		MongoAnnotationId embeddedId;
		String provider = object.getAnnotationId().getProvider(); 

		if(StringUtils.isEmpty(provider) || WebAnnotationFields.PROVIDER_WEBANNO.equals(provider)){
//			embeddedId = generateAnnotationId(object.getAnnotationId().getResourceId());		
			embeddedId = generateAnnotationId(object.getAnnotationId().getProvider());		
			embeddedId.setProvider(provider);
		}else{
			embeddedId = copyInto(object.getAnnotationId());
		}
		return embeddedId;
	}

	@Override
//	public MongoAnnotationId generateAnnotationId(String resourceId) {
//		AnnotationId annoId = getAnnotationDao().generateNextAnnotationId(
//				resourceId);
	public MongoAnnotationId generateAnnotationId(String provider) {
		AnnotationId annoId = getAnnotationDao().generateNextAnnotationId(
				provider);
		
		MongoAnnotationId embeddedId = copyInto(annoId);
		return embeddedId;
	}

	MongoAnnotationId copyInto(AnnotationId annoId) {
		MongoAnnotationId embeddedId = new MongoAnnotationId();
		embeddedId.copyFrom(annoId);
		return embeddedId;
	}

	public static String extractResoureIdFromHttpUri(String httpUri) {
		String res = "";
		if (StringUtils.isNotEmpty(httpUri)) {
			String[] arrValue = httpUri.split(WebAnnotationFields.SLASH);
			// computed from the end of the url
			int collectionPosition = arrValue.length - 2;
			int objectPosition = arrValue.length - 1;

			String collection = arrValue[collectionPosition];
			String object = arrValue[objectPosition].replace(".html", "");
			if (StringUtils.isNotEmpty(collection)
					&& StringUtils.isNotEmpty(object))
				res = WebAnnotationFields.SLASH + collection
						+ WebAnnotationFields.SLASH + object;
		}
		return res;
	}

	private PersistentTag findOrCreateTag(PersistentAnnotation object,
			TagBody body) {

		PersistentTag tag = null;

		// check if tag exists or create it
		if (body.getTagId() == null) {
			PersistentTag query = new PersistentTagImpl();
			body.copyInto(query);
			// query.setValue(body.getValue());
			// query.setHttpUri(body.getHttpUri());
			if (BodyTypes.isSimpleTagBody(body.getType()))
				query.setTagTypeEnum(TagTypes.SIMPLE_TAG);
			else if (BodyTypes.isSemanticTagBody(body.getType()))
				query.setTagTypeEnum(TagTypes.SEMANTIC_TAG);

			try {
				tag = tagService.find(query);
				if (tag == null) {
					query.setCreator(object.getAnnotatedBy().getName() + " : "
							+ object.getAnnotatedBy().getOpenId());
					tag = tagService.create(query);
				}

			} catch (AnnotationMongoException e) {
				throw new AnnotationValidationException(
						"Cannot read tag from database", e);
			}
		}
		if (tag.getId() == null && tag.getObjectId() != null)
			((PersistentTagImpl) tag).setId(tag.getObjectId().toString());
		return tag;
	}

	protected boolean hasTagBody(PersistentAnnotation object) {
		if(BodyTypes.isTagBody(object.getBody().getType()))
			return true;
///		List<String> types = object.getBody().getType();
//		Iterator<String> itr = types.iterator();
//		while(itr.hasNext()) {
//			if(BodyTypes.isTagBody(itr.next()))
//				return true;
///		}		
//		String euType = new TypeUtils().getEuTypeFromTypeArray(type);
//		return BodyTypes.isTagBody(object.getBody().getInternalType());//
		return BodyTypes.isTagBody(object.getBody().getInternalType());
	}

	@Override
	public Annotation store(Annotation object) {
		if(object instanceof PersistentAnnotation)
			return this.store((PersistentAnnotation) object);
		else{
			PersistentAnnotation persistentObject = copyIntoPersistentAnnotation(object);
			return this.store(persistentObject); 
		}
		
	}

	@Override
	public Annotation update(Annotation object) {
		if(object instanceof PersistentAnnotation)
			return this.update((PersistentAnnotation) object);
		else{
			PersistentAnnotation persistentObject = copyIntoPersistentAnnotation(object);
			return this.update(persistentObject); 
		}
		
	}

	@Override
	public ObjectTag store(ObjectTag object) {
		return (ObjectTag) this.store((PersistentAnnotation) object);
	}

	@Override
	public ImageAnnotation store(ImageAnnotation object) {
		return (ImageAnnotation) this.store((PersistentAnnotation) object);
	}

	protected PersistentAnnotationDao<PersistentAnnotation, String> getAnnotationDao() {
		return (PersistentAnnotationDao<PersistentAnnotation, String>) getDao();
	}

	@Override
	public List<? extends Annotation> getAnnotationList(String europeanaId) {
		return getFilteredAnnotationList(europeanaId, null, null, null, false);
	}

	@Override
	public List<? extends Annotation> getAnnotationListByProvider(
			String europeanaId, String provider) {
		return getFilteredAnnotationList(europeanaId, provider, null, null,
				false);
	}

	@Override
	public List<? extends Annotation> getAnnotationListByTarget(
			String target) {
		List<? extends Annotation> results = filterAnnotationListByTarget(target, false);
		if (results.size() == 0)
			results = filterAnnotationListByTarget(target, true);
		return results;
	}

	@Override
	public List<? extends Annotation> getAnnotationListByResourceId(
			String resourceId) {
		List<? extends Annotation> results = filterAnnotationListByResourceId(resourceId, false);
		if (results.size() == 0)
			results = filterAnnotationListByResourceId(resourceId, true);
		return results;
	}

	/**
	 * This method filters active annotations by target. By searching in 'target.value' parameter 'multiple' is false.
	 * By searching in 'target.values' parameter 'multiple' is true.
	 * @param target
	 * @param multiple
	 * @return evaluated list
	 */
	public List<? extends Annotation> filterAnnotationListByTarget(
			String target, boolean multiple) {
		Query<PersistentAnnotation> query = getAnnotationDao().createQuery();
		if (StringUtils.isNotEmpty(target)) {
			if (multiple)
				query.disableValidation().field(PersistentAnnotation.FIELD_TARGET + PersistentAnnotation.FIELD_VALUES).equal(target);
			else
				query.disableValidation().filter(PersistentAnnotation.FIELD_TARGET + PersistentAnnotation.FIELD_VALUE, target); 
		}
		query.filter(PersistentAnnotation.FIELD_DISABLED, false);
		QueryResults<? extends PersistentAnnotation> results = getAnnotationDao()
				.find(query);
		return results.asList();
	}

	/**
	 * This method filters active annotations by target. By searching in 'target.resourceId' parameter 'multiple' is false.
	 * By searching in 'target.resourceId' parameter 'multiple' is true.
	 * @param target
	 * @param multiple
	 * @return evaluated list
	 */
	public List<? extends Annotation> filterAnnotationListByResourceId(
			String target, boolean multiple) {
		Query<PersistentAnnotation> query = getAnnotationDao().createQuery();
		if (StringUtils.isNotEmpty(target)) {
			if (multiple)
				query.disableValidation().field(PersistentAnnotation.FIELD_TARGET + PersistentAnnotation.FIELD_RESOURCE_IDS).equal(target);
			else
				query.disableValidation().filter(PersistentAnnotation.FIELD_TARGET + PersistentAnnotation.FIELD_RESOURCE_ID, target); 
		}
		query.filter(PersistentAnnotation.FIELD_DISABLED, false);
		QueryResults<? extends PersistentAnnotation> results = getAnnotationDao()
				.find(query);
		return results.asList();
	}

	@Override
	public List<? extends Annotation> getFilteredAnnotationList(
			String europeanaId, String provider, String startOn, String limit,
			boolean isDisabled) {
		Query<PersistentAnnotation> query = getAnnotationDao().createQuery();
//		if (StringUtils.isNotEmpty(europeanaId))
//			query.filter(PersistentAnnotation.FIELD_EUROPEANA_ID, europeanaId);
		if (StringUtils.isNotEmpty(provider))
			query.filter(PersistentAnnotation.FIELD_PROVIDER, provider);
		query.filter(PersistentAnnotation.FIELD_DISABLED, isDisabled);
		try {
			if (StringUtils.isNotEmpty(startOn))
				query.offset(Integer.parseInt(startOn));
			if (StringUtils.isNotEmpty(limit))
				query.limit(Integer.parseInt(limit));
		} catch (Exception e) {
			throw new AnnotationAttributeInstantiationException(
					"Unexpected exception occured when searching annotations. "
							+ AnnotationAttributeInstantiationException.BASE_MESSAGE,
					"startOn: " + startOn + ", limit: " + limit + ". ", e);
		}

		QueryResults<? extends PersistentAnnotation> results = getAnnotationDao()
				.find(query);
		return results.asList();
	}

	@Override
	public PersistentAnnotation find(String provider, String identifier) {
		Query<PersistentAnnotation> query = getAnnotationDao().createQuery();
		query.filter(PersistentAnnotation.FIELD_PROVIDER, provider);
		query.filter(PersistentAnnotation.FIELD_IDENTIFIER, identifier);
		query.filter(PersistentAnnotation.FIELD_DISABLED, false);

		return getAnnotationDao().findOne(query);
	}

	@Override
	public PersistentAnnotation find(String europeanaId, String provider,
			String identifier) {
		Query<PersistentAnnotation> query = getAnnotationDao().createQuery();
//		query.filter(PersistentAnnotation.FIELD_EUROPEANA_ID, europeanaId);
		query.filter(PersistentAnnotation.FIELD_PROVIDER, provider);
		query.filter(PersistentAnnotation.FIELD_IDENTIFIER, identifier);
		query.filter(PersistentAnnotation.FIELD_DISABLED, false);

		return getAnnotationDao().findOne(query);
	}

	@Override
	public PersistentAnnotation find(AnnotationId annoId) {
//		return find(annoId.getResourceId(), annoId.getProvider(),
//				annoId.getAnnotationNr());
		return find(annoId.getProvider(), annoId.getIdentifier());
	}

	@Override
	public PersistentAnnotation findByID(String id) {
		return getDao().findOne("_id", new ObjectId(id));
	}

	@Override
	public void remove(String id) {
		// TODO use delete by query
		PersistentAnnotation annotation = findByID(id);
		getDao().delete(annotation);

	}

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.europeana.annotation.mongo.service.PersistentAnnotationService#remove
	 * (java.lang.String, java.lang.Integer)
	 */
//	public void remove(String resourceId, String provider, Long annotationNr) {
	public void remove(String provider, String identifier) {
		// String objectId = findObjectId(resourceId, annotationNr);
		Query<PersistentAnnotation> query = getAnnotationDao().createQuery();
//		query.filter(PersistentAnnotation.FIELD_EUROPEANA_ID, resourceId);
		query.filter(PersistentAnnotation.FIELD_PROVIDER, provider);
		query.filter(PersistentAnnotation.FIELD_IDENTIFIER, identifier);

		getDao().deleteByQuery(query);
	}

	// private String findObjectId(String resourceId, Integer annotationNr) {
	// Query<PersistentAnnotation> query = getAnnotationDao().createQuery();
	// query.filter(PersistentAnnotation.FIELD_EUROPEANA_ID, resourceId);
	// query.filter(PersistentAnnotation.FIELD_ANNOTATION_NR, annotationNr);
	// @SuppressWarnings("rawtypes")
	// List ids = getDao().findIds();
	// if(ids.size() != 1)
	// throw new
	// AnnotationMongoRuntimeException("Expected one object but found:" +
	// ids.size());
	//
	// ObjectId id = (ObjectId)ids.get(0);
	// return id.toString();
	// }

	@Override
	public void remove(AnnotationId annoId) {
//		remove(annoId.getResourceId(), annoId.getProvider(),
//				annoId.getAnnotationNr());
		remove(annoId.getProvider(), annoId.getIdentifier());
	}

	public PersistentAnnotation update(PersistentAnnotation object) { 

		PersistentAnnotation res = null;
		
		PersistentAnnotation persistentAnnotation = (PersistentAnnotation) object;

		if (persistentAnnotation != null
				&& persistentAnnotation.getAnnotationId() != null) {
/*			remove(//persistentAnnotation.getAnnotationId().getResourceId(),
					persistentAnnotation.getAnnotationId().getProvider(),
					persistentAnnotation.getAnnotationId().getIdentifier());
			//MongoDB object id - not annotation id
			persistentAnnotation.setId(null);
			if (persistentAnnotation.getBody() != null) {
				((TagBody) persistentAnnotation.getBody()).setTagId(null);
			}
			res = store(persistentAnnotation);
*/			
			PersistentAnnotation dbAnnotation = find(
					persistentAnnotation.getAnnotationId().getProvider()
					, persistentAnnotation.getAnnotationId().getIdentifier());
			
			persistentAnnotation.setId(dbAnnotation.getId());
			
			// generate new and replace existing timestamp for the Annotation
			Date now = new Date();
			// validate mandatory fields
			validatePersistentAnnotationExt(persistentAnnotation, now, false);
			persistentAnnotation.setLastUpdate(now);

			Query<PersistentAnnotation> updateQuery = super.getDao().createQuery();
			updateQuery.field(WebAnnotationFields.MONGO_ID).equal(persistentAnnotation.getId());
			
			UpdateOperations<PersistentAnnotation> updateOperations = super.getDao().createUpdateOperations();
			if (persistentAnnotation.getBody() != null)
				updateOperations.set(WebAnnotationFields.BODY, persistentAnnotation.getBody());
			if (persistentAnnotation.getTarget() != null)
				updateOperations.set(WebAnnotationFields.TARGET, persistentAnnotation.getTarget());
			updateOperations.set(WebAnnotationFields.ANNOTATED_AT, persistentAnnotation.getAnnotatedAt());
			updateOperations.set(WebAnnotationFields.ANNOTATED_BY, persistentAnnotation.getAnnotatedBy());
			updateOperations.set(WebAnnotationFields.SERIALIZED_AT, persistentAnnotation.getSerializedAt());
			updateOperations.set(WebAnnotationFields.SERIALIZED_BY, persistentAnnotation.getSerializedBy());
			updateOperations.set(WebAnnotationFields.MOTIVATION, persistentAnnotation.getMotivation());
			updateOperations.set(WebAnnotationFields.TYPE, persistentAnnotation.getType());
			updateOperations.set(WebAnnotationFields.DISABLED, persistentAnnotation.isDisabled());
			if (persistentAnnotation.getEquivalentTo() != null)
				updateOperations.set(WebAnnotationFields.EQUIVALENT_TO, persistentAnnotation.getEquivalentTo());
//			if (persistentAnnotation.getInternalType() != null)
//				updateOperations.set(WebAnnotationFields.INTERNAL_TYPE, persistentAnnotation.getInternalType());
			if (persistentAnnotation.getLastIndexedTimestamp() != null)
				updateOperations.set(WebAnnotationFields.LAST_INDEXED_TIMESTAMP, persistentAnnotation.getLastIndexedTimestamp());
			if (persistentAnnotation.getLastUpdate() != null)
				updateOperations.set(WebAnnotationFields.LAST_UPDATE, persistentAnnotation.getLastUpdate());
			if (persistentAnnotation.getSameAs() != null)
				updateOperations.set(WebAnnotationFields.SAME_AS, persistentAnnotation.getSameAs());
			if (persistentAnnotation.getStatus() != null)
				updateOperations.set(WebAnnotationFields.STATUS, persistentAnnotation.getStatus());
			if (persistentAnnotation.getStyledBy() != null)
				updateOperations.set(WebAnnotationFields.STYLED_BY, persistentAnnotation.getStyledBy());

			int numOfUpdatedEntries = super.update(updateQuery, updateOperations);
			if (numOfUpdatedEntries != 1)
				throw new AnnotationUpdateException(
						persistentAnnotation.getAnnotationId().toString());
			res = find(
					persistentAnnotation.getAnnotationId().getProvider()
					, persistentAnnotation.getAnnotationId().getIdentifier());
		} else {
			throw new AnnotationValidationException(
					AnnotationValidationException.ERROR_NULL_ANNOTATION_ID);
		}

		return res;
	}

	@Override
	public Annotation updateIndexingTime(AnnotationId annoId) {

		Annotation res = null;

		PersistentAnnotation annotation = find(annoId);

		if (annotation != null && annotation.getLastIndexedTimestamp() == null) {
			annotation.setLastIndexedTimestamp(System.currentTimeMillis());
			UpdateOperations<PersistentAnnotation> ops = getAnnotationDao()
					.createUpdateOperations().set(
							WebAnnotationFields.LAST_INDEXED_TIMESTAMP,
							annotation.getLastIndexedTimestamp());
			Query<PersistentAnnotation> updateQuery = getAnnotationDao()
					.createQuery().field("_id").equal(annotation.getId());
			getAnnotationDao().update(updateQuery, ops);
			res = annotation; // update(annotation);
		}

		return res;
	}

	
	@Override
	public Annotation updateStatus(Annotation newAnnotation) {

		Annotation res = null;

		PersistentAnnotation annotation = find(newAnnotation.getAnnotationId());

		if (annotation != null) {
			annotation.setStatus(newAnnotation.getStatus());
			UpdateOperations<PersistentAnnotation> ops = getAnnotationDao()
					.createUpdateOperations().set(
							WebAnnotationFields.STATUS,
							annotation.getStatus());
			Query<PersistentAnnotation> updateQuery = getAnnotationDao()
					.createQuery().field("_id").equal(annotation.getId());
			getAnnotationDao().update(updateQuery, ops);
			res = annotation; 
		}

		return res;
	}

		
	@SuppressWarnings("deprecation")
	public PersistentAnnotation copyIntoPersistentAnnotation(Annotation annotation) {

		PersistentAnnotationImpl persistentAnnotation = (PersistentAnnotationImpl) (PersistentAnnotationFactory
				.getInstance().createAnnotationInstance(annotation.getInternalType()));

		MongoAnnotationId mongoAnnotationId = new MongoAnnotationId();
	    mongoAnnotationId.copyFrom(annotation.getAnnotationId());
	    persistentAnnotation.setAnnotationId(mongoAnnotationId);
	    
		getAnnotationBuilder().copyAnnotationAttributes(annotation, persistentAnnotation);
		return persistentAnnotation;
	}

	@Override
	public Annotation findByTagId(String tagId) {
		throw new RuntimeException("This method is not supported yet!");
	}

}
