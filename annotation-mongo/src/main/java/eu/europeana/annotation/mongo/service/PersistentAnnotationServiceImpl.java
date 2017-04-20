package eu.europeana.annotation.mongo.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.QueryResults;
import com.google.code.morphia.query.UpdateOperations;

import eu.europeana.annotation.config.AnnotationConfiguration;
import eu.europeana.annotation.definitions.exception.AnnotationValidationException;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.body.PlaceBody;
import eu.europeana.annotation.definitions.model.body.TagBody;
import eu.europeana.annotation.definitions.model.utils.AnnotationBuilder;
import eu.europeana.annotation.definitions.model.utils.TypeUtils;
import eu.europeana.annotation.definitions.model.vocabulary.AnnotationTypes;
import eu.europeana.annotation.definitions.model.vocabulary.BodyInternalTypes;
import eu.europeana.annotation.definitions.model.vocabulary.TagTypes;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.mongo.dao.PersistentAnnotationDao;
import eu.europeana.annotation.mongo.exception.AnnotationMongoException;
import eu.europeana.annotation.mongo.exception.AnnotationMongoRuntimeException;
import eu.europeana.annotation.mongo.factory.PersistentAnnotationFactory;
import eu.europeana.annotation.mongo.model.MongoAnnotationId;
import eu.europeana.annotation.mongo.model.PersistentAnnotationImpl;
import eu.europeana.annotation.mongo.model.PersistentTagImpl;
import eu.europeana.annotation.mongo.model.internal.PersistentAnnotation;
import eu.europeana.annotation.mongo.model.internal.PersistentTag;
import eu.europeana.annotation.mongo.service.validation.GeoPlaceValidator;
import eu.europeana.annotation.mongo.service.validation.impl.EdmPlaceValidatorImpl;
import eu.europeana.corelib.db.service.abstracts.AbstractNoSqlServiceImpl;

@Component
public class PersistentAnnotationServiceImpl extends AbstractNoSqlServiceImpl<PersistentAnnotation, String>
		implements PersistentAnnotationService {

	GeoPlaceValidator geoPlaceValidator;
	
	@Resource
	private PersistentTagService tagService;

	@Resource
	private AnnotationConfiguration configuration;

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
		validatePersistentAnnotation(object, now, true);
		object.setLastUpdate(now);
		return super.store(object);
	}

	/**
	 * This method validates persistent annotation and generates ID if
	 * genarateId=true.
	 * 
	 * @param object
	 * @param now
	 * @param isNew
	 */
	private void validatePersistentAnnotation(PersistentAnnotation object, Date now, boolean isNew) {

		if (object.getCreated() == null && isNew)
			object.setCreated(now);
		//TODO: what to check for update?
//		else if(object.getCreated() == null && isNew)

		//always overwrite generated field	
		object.setGenerated(now);

		// check creator
		if (object.getCreator() == null)
			throw new AnnotationValidationException(AnnotationValidationException.ERROR_NULL_CREATOR);

		// check Europeana ID
		if (isNew && object.getId() != null)
			throw new AnnotationValidationException(AnnotationValidationException.ERROR_NOT_NULL_OBJECT_ID);

		if (object.getAnnotationId() == null || StringUtils.isEmpty(object.getAnnotationId().getProvider()))
			throw new AnnotationValidationException(
					"AnnotationId must not be null. AnnotationId.provider attribute is required");

		// check target
		validateTarget(object);
		// checkBody
		validateBody(object);

		if (isNew) {
			MongoAnnotationId embeddedId = initializeAnnotationId(object);
			object.setAnnotationId(embeddedId);
		}

		// validate annotation NR
		if (StringUtils.isBlank(object.getAnnotationId().getIdentifier()) 
				|| AnnotationId.NOT_INITIALIZED_LONG_ID.equals(object.getAnnotationId().getIdentifier()))
				throw new AnnotationValidationException("Annotaion.AnnotationId.identifier must be a valid alpha-numeric value or a positive number!");
	}

	protected void validateTarget(PersistentAnnotation object) {
		if (object.getTarget() == null)
			throw new AnnotationValidationException(AnnotationValidationException.ERROR_NULL_TARGET);
	}

	protected void validateBody(PersistentAnnotation object) {

		if (object.getBody() == null && isBodyMandatory(object))//check if mandatory
			throw new AnnotationValidationException(AnnotationValidationException.ERROR_NULL_BODY);
		else if(object.getBody() == null)
			return;//nothing to check
		
			
		if(object.getBody() instanceof PlaceBody)
			getGeoPlaceValidator().validate(((PlaceBody)object.getBody()).getPlace());
		
	}

		
	protected boolean isBodyMandatory(PersistentAnnotation object) {
		return !object.getInternalType().equals(AnnotationTypes.OBJECT_LINKING.name());
	}

	MongoAnnotationId initializeAnnotationId(PersistentAnnotation object) {
		if (StringUtils.isEmpty(object.getAnnotationId().getIdentifier())) {
			return generateAnnotationId(object.getAnnotationId().getProvider());
		} else {
			return new MongoAnnotationId(object.getAnnotationId());
		}
	}

	@Override
	public MongoAnnotationId generateAnnotationId(String provider) {
		AnnotationId annoId = getAnnotationDao().generateNextAnnotationId(provider);
		annoId.setBaseUrl(getConfiguration().getAnnotationBaseUrl());

		return new MongoAnnotationId(annoId);
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
			if (StringUtils.isNotEmpty(collection) && StringUtils.isNotEmpty(object))
				res = WebAnnotationFields.SLASH + collection + WebAnnotationFields.SLASH + object;
		}
		return res;
	}

	private PersistentTag findOrCreateTag(PersistentAnnotation object, TagBody body) {

		PersistentTag tag = null;

		// check if tag exists or create it
		if (body.getTagId() == null) {
			PersistentTag query = new PersistentTagImpl();
			body.copyInto(query);
			// query.setValue(body.getValue());
			// query.setHttpUri(body.getHttpUri());
			if (BodyInternalTypes.isSimpleTagBody(body.getType()))
				query.setTagTypeEnum(TagTypes.SIMPLE_TAG);
			else if (BodyInternalTypes.isSemanticTagBody(body.getType()))
				query.setTagTypeEnum(TagTypes.SEMANTIC_TAG);

			try {
				tag = tagService.find(query);
				if (tag == null) {
					query.setCreator(object.getCreator().getName() + " : " + object.getCreator().getHttpUrl());
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
		if (BodyInternalTypes.isTagBody(object.getBody().getType()))
			return true;
		return BodyInternalTypes.isTagBody(object.getBody().getInternalType());
	}

	@Override
	public Annotation store(Annotation object) {
		if (object instanceof PersistentAnnotation)
			return this.store((PersistentAnnotation) object);
		else {
			PersistentAnnotation persistentObject = copyIntoPersistentAnnotation(object);
			return this.store(persistentObject);
		}

	}


	protected PersistentAnnotationDao<PersistentAnnotation, String> getAnnotationDao() {
		return (PersistentAnnotationDao<PersistentAnnotation, String>) getDao();
	}

	@Override
	public List<? extends Annotation> getAnnotationList(String europeanaId) {
		return getFilteredAnnotationList(europeanaId, null, null, null, false);
	}


	@Override
	public List<? extends Annotation> getAnnotationListByTarget(String target) {
		List<? extends Annotation> results = filterAnnotationListByTarget(target, false);
		if (results.size() == 0)
			results = filterAnnotationListByTarget(target, true);
		return results;
	}

	@Override
	public List<? extends Annotation> getAnnotationListByResourceId(String resourceId) {
		List<? extends Annotation> results = filterAnnotationListByResourceId(resourceId);
		if (results == null)
			results = new ArrayList<PersistentAnnotationImpl>(0);
		return results;
	}

	/**
	 * This method filters active annotations by target. By searching in
	 * 'target.value' parameter 'multiple' is false. By searching in
	 * 'target.values' parameter 'multiple' is true.
	 * 
	 * @param target
	 * @param multiple
	 * @return evaluated list
	 */
	protected List<? extends Annotation> filterAnnotationListByTarget(String target, boolean multiple) {
		Query<PersistentAnnotation> query = getAnnotationDao().createQuery();
		if (StringUtils.isNotEmpty(target)) {
			if (multiple)
				query.disableValidation().field(PersistentAnnotation.FIELD_TARGET + PersistentAnnotation.FIELD_VALUES)
						.equal(target);
			else
				query.disableValidation().filter(PersistentAnnotation.FIELD_TARGET + PersistentAnnotation.FIELD_VALUE,
						target);
		}
		query.filter(PersistentAnnotation.FIELD_DISABLED, false);
		QueryResults<? extends PersistentAnnotation> results = getAnnotationDao().find(query);
		return results.asList();
	}

	/**
	 * This method filters active annotations by target. By searching in
	 * 'target.resourceId' parameter 'multiple' is false. By searching in
	 * 'target.resourceId' parameter 'multiple' is true.
	 * 
	 * @param resourceId
	 * @param multiple
	 * @return evaluated list
	 */
	protected List<? extends Annotation> filterAnnotationListByResourceId(String resourceId) {
		//ensure not empty resourceID
		if(StringUtils.isBlank(resourceId))
			return null;
		
		Query<PersistentAnnotation> query = getAnnotationDao().createQuery();
		//add resourceID filter
		query.disableValidation()
					.field(PersistentAnnotation.FIELD_TARGET + PersistentAnnotation.FIELD_RESOURCE_IDS)
						.equal(resourceId);
			
		query.filter(PersistentAnnotation.FIELD_DISABLED, false);
		QueryResults<? extends PersistentAnnotation> results = getAnnotationDao().find(query);
		return results.asList();
	}

	@Override
	public List<? extends Annotation> getFilteredAnnotationList(String europeanaId, String provider, String startOn,
			String limit, boolean isDisabled) {
		Query<PersistentAnnotation> query = getAnnotationDao().createQuery();
		if (StringUtils.isNotEmpty(provider))
			query.filter(PersistentAnnotation.FIELD_PROVIDER, provider);
		query.filter(PersistentAnnotation.FIELD_DISABLED, isDisabled);
		try {
			if (StringUtils.isNotEmpty(startOn))
				query.offset(Integer.parseInt(startOn));
			if (StringUtils.isNotEmpty(limit))
				query.limit(Integer.parseInt(limit));
		} catch (NumberFormatException e) {
			throw new AnnotationMongoRuntimeException(
					"Invalid startOn/limit params. "+						
					"startOn: " + startOn + ", limit: " + limit + ". ", e);
		}

		QueryResults<? extends PersistentAnnotation> results = getAnnotationDao().find(query);
		return results.asList();
	}

	@Override
	public PersistentAnnotation find(AnnotationId annoId) {

		Query<PersistentAnnotation> query = getAnnotationDao().createQuery();
		query.filter(PersistentAnnotation.FIELD_HTTPURL, annoId.getHttpUrl());
//		query.filter(PersistentAnnotation.FIELD_BASEURL, annoId.getBaseUrl());
//		query.filter(PersistentAnnotation.FIELD_PROVIDER, annoId.getProvider());
//		query.filter(PersistentAnnotation.FIELD_IDENTIFIER, annoId.getIdentifier());
		query.filter(PersistentAnnotation.FIELD_DISABLED, false);

		return getAnnotationDao().findOne(query);
	}

	@Override
	public PersistentAnnotation findByID(String id) {
		return getDao().findOne( WebAnnotationFields.MONGO_ID, new ObjectId(id));
	}

	@Override
	public void remove(String id) {
		// TODO use delete by query
		PersistentAnnotation annotation = findByID(id);
		getDao().delete(annotation);

	}


	@Override
	public void remove(AnnotationId annoId) {

		Query<PersistentAnnotation> query = getAnnotationDao().createQuery();
		query.filter(PersistentAnnotation.FIELD_HTTPURL, annoId.getHttpUrl());
		
//		query.filter(PersistentAnnotation.FIELD_BASEURL, annoId.getBaseUrl());
//		query.filter(PersistentAnnotation.FIELD_PROVIDER, annoId.getProvider());
//		query.filter(PersistentAnnotation.FIELD_IDENTIFIER, annoId.getIdentifier());

		getDao().deleteByQuery(query);
	}

	/**
	 * use store() instead
	 * @param persistentAnnotation
	 * @return
	 */
	public PersistentAnnotation update(PersistentAnnotation persistentAnnotation) {

		PersistentAnnotation res = null;

		if (persistentAnnotation != null && persistentAnnotation.getAnnotationId() != null) {
			// generate new and replace existing timestamp for the Annotation
			Date now = new Date();
			// validate mandatory fields
			persistentAnnotation.setLastUpdate(now);
			validatePersistentAnnotation(persistentAnnotation, now, false);
			
			res = super.store(persistentAnnotation);
		} else {
			throw new AnnotationValidationException(AnnotationValidationException.ERROR_NULL_ANNOTATION_ID);
		}

		return res;
	}

	protected Query<PersistentAnnotation> buildFindByIdQuery(PersistentAnnotation persistentAnnotation) {
		Query<PersistentAnnotation> findByIdQuery = super.getDao().createQuery();
		findByIdQuery.field(WebAnnotationFields.MONGO_ID).equal(persistentAnnotation.getId());
		return findByIdQuery;
	}

	/**
	 * The usage of this method should be avoided. Simply use the dao.save()
	 * method
	 * 
	 * @param persistentAnnotation
	 * @return
	 */
	@Deprecated
	protected UpdateOperations<PersistentAnnotation> buildUpdateOperations(PersistentAnnotation persistentAnnotation) {
		UpdateOperations<PersistentAnnotation> updateOperations = super.getDao().createUpdateOperations();
		if (persistentAnnotation.getBody() != null)
			updateOperations.set(WebAnnotationFields.BODY, persistentAnnotation.getBody());
		if (persistentAnnotation.getTarget() != null)
			updateOperations.set(WebAnnotationFields.TARGET, persistentAnnotation.getTarget());
		updateOperations.set(WebAnnotationFields.CREATED, persistentAnnotation.getCreated());
		updateOperations.set(WebAnnotationFields.CREATOR, persistentAnnotation.getCreator());
		updateOperations.set(WebAnnotationFields.GENERATED, persistentAnnotation.getGenerated());
		updateOperations.set(WebAnnotationFields.GENERATOR, persistentAnnotation.getGenerator());
		updateOperations.set(WebAnnotationFields.MOTIVATION, persistentAnnotation.getMotivation());
		if (persistentAnnotation.getType() != null)
			updateOperations.set(WebAnnotationFields.TYPE, persistentAnnotation.getType());
		if (persistentAnnotation.getInternalType() != null)
			updateOperations.set(WebAnnotationFields.INTERNAL_TYPE, persistentAnnotation.getInternalType());
		updateOperations.set(WebAnnotationFields.DISABLED, persistentAnnotation.isDisabled());
		if (persistentAnnotation.getEquivalentTo() != null)
			updateOperations.set(WebAnnotationFields.EQUIVALENT_TO, persistentAnnotation.getEquivalentTo());
		// if (persistentAnnotation.getInternalType() != null)
		// updateOperations.set(WebAnnotationFields.INTERNAL_TYPE,
		// persistentAnnotation.getInternalType());
		if (persistentAnnotation.getLastIndexed() != null)
			updateOperations.set(WebAnnotationFields.LAST_INDEXED,
					persistentAnnotation.getLastIndexed());
		if (persistentAnnotation.getLastUpdate() != null)
			updateOperations.set(WebAnnotationFields.LAST_UPDATE, persistentAnnotation.getLastUpdate());
		if (persistentAnnotation.getSameAs() != null)
			updateOperations.set(WebAnnotationFields.SAME_AS, persistentAnnotation.getSameAs());
		if (persistentAnnotation.getStatus() != null)
			updateOperations.set(WebAnnotationFields.STATUS, persistentAnnotation.getStatus());
		if (persistentAnnotation.getStyledBy() != null)
			updateOperations.set(WebAnnotationFields.STYLED_BY, persistentAnnotation.getStyledBy());
		if (persistentAnnotation.getCanonical() != null)
			//#404 check if this correct, since it must not be changed once set
			updateOperations.set(WebAnnotationFields.CANONICAL, persistentAnnotation.getCanonical());
		if (persistentAnnotation.getVia() != null)
			updateOperations.set(WebAnnotationFields.VIA, persistentAnnotation.getVia());
		return updateOperations;
	}

	@Override
	public Annotation updateIndexingTime(AnnotationId annoId, Date lastIndexing) throws AnnotationMongoException {

		PersistentAnnotation annotation = find(annoId);

		if (annotation == null) {
			throw new AnnotationMongoException(AnnotationMongoException.NO_RECORD_FOUND + annoId);
		}

		if (lastIndexing == null)
			lastIndexing = new Date();

		if (!isValidLastIndexingTimestamp(annotation, lastIndexing))
			throw new AnnotationMongoRuntimeException(
					AnnotationMongoRuntimeException.INVALID_LAST_INDEXING + lastIndexing);

		annotation.setLastIndexed(lastIndexing);
		
		UpdateOperations<PersistentAnnotation> ops = getAnnotationDao().createUpdateOperations()
				.set(WebAnnotationFields.LAST_INDEXED, lastIndexing.getTime()).set(WebAnnotationFields.LAST_INDEXED, lastIndexing);
		Query<PersistentAnnotation> updateQuery = getAnnotationDao().createQuery().field(WebAnnotationFields.MONGO_ID)
				.equal(annotation.getId());
		getAnnotationDao().update(updateQuery, ops);

		return annotation;
	}

	private boolean isValidLastIndexingTimestamp(PersistentAnnotation annotation, Date lastIndexing) {
		// if never indexed
		if (lastIndexing != null && annotation.getLastIndexed() == null)
			return true;

		return lastIndexing.after(annotation.getLastIndexed());
	}

	/**
	 * This method returns annotations by start- and end timestamps.
	 * 
	 * @param startTimestamp
	 *            as long value in string format
	 * @param endTimestamp
	 *            as long value in string format
	 * @return evaluated ID list
	 */
	public List<String> filterByLastUpdateTimestamp(
			// public List<AnnotationId> filterByTimestamp(
			// public List<? extends Annotation> filterByTimestamp(
			String startTimestamp, String endTimestamp) {
		Query<PersistentAnnotation> query = getAnnotationDao().createQuery();
		if (StringUtils.isNotBlank(startTimestamp)) {
			//Date start = TypeUtils.convertUnixTimestampStrToDate(startTimestamp);
			Date start = new Date(Long.parseLong(startTimestamp));
			query.field(WebAnnotationFields.LAST_UPDATE).greaterThan(start);
		}
		if (StringUtils.isNotBlank(endTimestamp)) {
			//Date end = TypeUtils.convertUnixTimestampStrToDate(endTimestamp);
			Date end = new Date(Long.parseLong(endTimestamp));
			query.field(WebAnnotationFields.LAST_UPDATE).lessThan(end);
		}
		//Actually this is a list of Objects
		List<String> res = getAnnotationDao().findIds(query);
		//convert the list
		List<String> response = new ArrayList<String>(res.size());
		for (Object id : res){ response.add(id.toString()); }
		
		return response;
	}

	@Override
	public Annotation updateStatus(Annotation newAnnotation) {

		Annotation res = null;

		PersistentAnnotation annotation = find(newAnnotation.getAnnotationId());

		if (annotation != null) {
			annotation.setStatus(newAnnotation.getStatus());
			UpdateOperations<PersistentAnnotation> ops = getAnnotationDao().createUpdateOperations()
					.set(WebAnnotationFields.STATUS, annotation.getStatus());
			Query<PersistentAnnotation> updateQuery = getAnnotationDao().createQuery().field(WebAnnotationFields.MONGO_ID)
					.equal(annotation.getId());
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

	public AnnotationConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(AnnotationConfiguration configuration) {
		this.configuration = configuration;
	}

	public GeoPlaceValidator getGeoPlaceValidator() {
		if(geoPlaceValidator == null)
			geoPlaceValidator = new EdmPlaceValidatorImpl();
		return geoPlaceValidator;
	}

	@Override
	public List<? extends Annotation> getAnnotationList(List<String> annotationIds) {
		
		Query<PersistentAnnotation> searchQuery = getAnnotationDao().createQuery();
		
		searchQuery.filter(PersistentAnnotation.FIELD_HTTPURL + " in", annotationIds);
		
		return getAnnotationDao().find(searchQuery).asList();		
	}

	@Override
	public List<String> filterByLastUpdateGreaterThanLastIndexTimestamp() {
		Query<PersistentAnnotation> query = getAnnotationDao().createQuery();
				
		// Morphia query
		query.where("this." + PersistentAnnotation.FIELD_DISABLED + " == false && "
			  + "(this." +WebAnnotationFields.LAST_UPDATE + "> this." + WebAnnotationFields.LAST_INDEXED + " || "
			  + " this." + WebAnnotationFields.LAST_INDEXED + " == null)");
		
		//Actually this is a list of Objects
		List<String> res = getAnnotationDao().findIds(query);		
		
		//convert the list
		List<String> response = new ArrayList<String>(res.size());
		for (Object id : res){ response.add(id.toString()); }
		
		return response;
	}
	
}
