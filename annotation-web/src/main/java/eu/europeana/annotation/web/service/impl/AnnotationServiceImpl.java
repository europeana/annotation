package eu.europeana.annotation.web.service.impl;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.stanbol.commons.exception.JsonParseException;

import com.google.common.base.Strings;

import eu.europeana.annotation.definitions.exception.AnnotationValidationException;
import eu.europeana.annotation.definitions.exception.ModerationRecordValidationException;
import eu.europeana.annotation.definitions.exception.ProviderValidationException;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.Provider;
import eu.europeana.annotation.definitions.model.StatusLog;
import eu.europeana.annotation.definitions.model.body.Body;
import eu.europeana.annotation.definitions.model.body.PlaceBody;
import eu.europeana.annotation.definitions.model.entity.Concept;
import eu.europeana.annotation.definitions.model.entity.Place;
import eu.europeana.annotation.definitions.model.impl.BaseStatusLog;
import eu.europeana.annotation.definitions.model.moderation.ModerationRecord;
import eu.europeana.annotation.definitions.model.utils.AnnotationBuilder;
import eu.europeana.annotation.definitions.model.vocabulary.AnnotationStates;
import eu.europeana.annotation.definitions.model.vocabulary.BodyInternalTypes;
import eu.europeana.annotation.definitions.model.vocabulary.IdGenerationTypes;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.mongo.exception.ModerationMongoException;
import eu.europeana.annotation.mongo.service.PersistentConceptService;
import eu.europeana.annotation.mongo.service.PersistentProviderService;
import eu.europeana.annotation.mongo.service.PersistentStatusLogService;
import eu.europeana.annotation.mongo.service.PersistentTagService;
import eu.europeana.annotation.mongo.service.PersistentWhitelistService;
import eu.europeana.annotation.solr.exceptions.AnnotationServiceException;
import eu.europeana.annotation.solr.exceptions.AnnotationStateException;
import eu.europeana.annotation.solr.exceptions.StatusLogServiceException;
import eu.europeana.annotation.solr.exceptions.TagServiceException;
import eu.europeana.annotation.solr.model.internal.SolrTag;
import eu.europeana.annotation.solr.vocabulary.SolrSyntaxConstants;
import eu.europeana.annotation.utils.parse.AnnotationLdParser;
import eu.europeana.annotation.web.exception.request.ParamValidationException;
import eu.europeana.annotation.web.service.AnnotationService;

public class AnnotationServiceImpl extends BaseAnnotationServiceImpl implements AnnotationService {

	@Resource
	PersistentTagService mongoTagPersistence;

	@Resource
	PersistentProviderService mongoProviderPersistance;

	@Resource
	PersistentConceptService mongoConceptPersistence;

	@Resource
	PersistentWhitelistService mongoWhitelistPersistence;

	@Resource
	PersistentStatusLogService mongoStatusLogPersistence;

	AnnotationBuilder annotationBuilder;

	public AnnotationBuilder getAnnotationHelper() {
		if (annotationBuilder == null)
			annotationBuilder = new AnnotationBuilder();
		return annotationBuilder;
	}

	public PersistentTagService getMongoTagPersistence() {
		return mongoTagPersistence;
	}

	public void setMongoTagPersistance(PersistentTagService mongoTagPersistence) {
		this.mongoTagPersistence = mongoTagPersistence;
	}

	public PersistentProviderService getMongoProviderPersistence() {
		return mongoProviderPersistance;
	}

	public void setMongoProviderPersistance(PersistentProviderService mongoProviderPersistance) {
		this.mongoProviderPersistance = mongoProviderPersistance;
	}

	public PersistentConceptService getMongoConceptPersistence() {
		return mongoConceptPersistence;
	}

	public void setMongoConceptPersistance(PersistentConceptService mongoConceptPersistence) {
		this.mongoConceptPersistence = mongoConceptPersistence;
	}

	public PersistentWhitelistService getMongoWhitelistPersistence() {
		return mongoWhitelistPersistence;
	}

	public void setMongoWhitelistPersistance(PersistentWhitelistService mongoWhitelistPersistence) {
		this.mongoWhitelistPersistence = mongoWhitelistPersistence;
	}

	public PersistentStatusLogService getMongoStatusLogPersistence() {
		return mongoStatusLogPersistence;
	}

	public void setMongoStatusLogPersistance(PersistentStatusLogService mongoStatusLogPersistence) {
		this.mongoStatusLogPersistence = mongoStatusLogPersistence;
	}

	@Override
	public List<? extends Annotation> getAnnotationList(String resourceId) {
		return getMongoPersistence().getAnnotationList(resourceId);
	}

	@Override
	public List<? extends Annotation> getFilteredAnnotationList(String resourceId, String startOn, String limit,
			boolean isDisabled) {
		return getMongoPersistence().getFilteredAnnotationList(resourceId, null, startOn, limit, isDisabled);
	}

	@Override
	public List<? extends Annotation> searchAnnotations(String query) throws AnnotationServiceException {
		return null;
		// return getSolrService().search(query);
	}

	@Override
	public List<? extends Annotation> searchAnnotations(String query, String startOn, String limit)
			throws AnnotationServiceException {
		return null;
		// return getSolrService().search(query, startOn, limit);
	}

	@Override
	public Map<String, Integer> searchAnnotations(String[] qf, List<String> queries) throws AnnotationServiceException {
		return getSolrService().queryFacetSearch(SolrSyntaxConstants.ALL_SOLR_ENTRIES, qf, queries);
	}

	@Override
	public List<? extends SolrTag> searchTags(String query) throws TagServiceException {
		return getSolrTagService().search(query);
	}

	@Override
	public List<? extends StatusLog> searchStatusLogs(String query, String startOn, String limit)
			throws StatusLogServiceException {
		return getMongoStatusLogPersistence().getFilteredStatusLogList(query, startOn, limit);
	}

	@Override
	public List<? extends SolrTag> searchTags(String query, String startOn, String limit) throws TagServiceException {
		return getSolrTagService().search(query, startOn, limit);
	}

	@Override
	public Annotation parseAnnotationLd(MotivationTypes motivationType, String annotationJsonLdStr)
			throws JsonParseException {

		/**
		 * parse JsonLd string using JsonLdParser. JsonLd string -> JsonLdParser
		 * -> JsonLd object
		 */
		AnnotationLdParser europeanaParser = new AnnotationLdParser();
		return europeanaParser.parseAnnotation(motivationType, annotationJsonLdStr);
	}

	@Override
	public Provider storeProvider(Provider newProvider) {

		// must have registered id generation type.
		validateProvider(newProvider);

		// store in mongo database
		Provider res = getMongoProviderPersistence().store(newProvider);

		return res;
	}

	/**
	 * This method validates Provider object.
	 * 
	 * @param newProvider
	 */
	private void validateProvider(Provider newProvider) {

		if (newProvider.getIdGeneration() == null)
			throw new ProviderValidationException(ProviderValidationException.ERROR_NOT_NULL_ID_GENERATION);

		if (StringUtils.isEmpty(IdGenerationTypes.isRegisteredAs(newProvider.getIdGeneration())))
			throw new ProviderValidationException(ProviderValidationException.ERROR_NOT_STANDARDIZED_ID_GENERATION);
	}

	@Override
	public List<? extends Provider> getProviderList(String idGeneration) {
		return getMongoProviderPersistence().getProviderList(idGeneration);
	}

	@Override
	public List<? extends Provider> getFilteredProviderList(String idGeneration, String startOn, String limit) {
		return getMongoProviderPersistence().getFilteredProviderList(idGeneration, startOn, limit);
	}

	@Override
	public Provider updateProvider(Provider provider) {
		Provider res = getMongoProviderPersistence().update(provider);
		return res;
	}

	@Override
	public void deleteProvider(String name, String idGeneration) {
		getMongoProviderPersistence().remove(name, idGeneration);
	}

	@Override
	public Concept storeConcept(Concept newConcept) {

		// must have registered id generation type.
		// validateConcept(newConcept);

		// store in mongo database
		Concept res = getMongoConceptPersistence().store(newConcept);

		return res;
	}

	@Override
	public Concept updateConcept(Concept concept) {
		Concept res = getMongoConceptPersistence().update(concept);
		return res;
	}

	@Override
	public void deleteConcept(String url) {
		getMongoConceptPersistence().remove(url);
	}

	@Override
	public Concept getConceptByUrl(String url) {
		return getMongoConceptPersistence().findByUrl(url);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.europeana.annotation.web.service.AnnotationService#storeAnnotation(eu.
	 * europeana.annotation.definitions.model.Annotation)
	 */
	@Override
	public Annotation storeAnnotation(Annotation newAnnotation) {
		return storeAnnotation(newAnnotation, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.europeana.annotation.web.service.AnnotationService#storeAnnotation(eu.
	 * europeana.annotation.definitions.model.Annotation, boolean)
	 */
	@Override
	public Annotation storeAnnotation(Annotation newAnnotation, boolean indexing) {

		// must have annotaionId with resourceId and provider.
		validateAnnotationId(newAnnotation);

		// store in mongo database
		Annotation res = getMongoPersistence().store(newAnnotation);

		if (indexing) {
			// add solr indexing here
			try {
				getSolrService().store(res);
			} catch (Exception e) {
				Logger.getLogger(getClass().getName())
						.warn("The annotation was stored correctly into the Mongo, but it was not indexed yet. ", e);
				// throw new RuntimeException(e);
			}

			// check if the tag is already indexed
			try {
				// TODO : enable+ refactor when semantic tagging is in place
				// SolrTag indexedTag = copyIntoSolrTag(res.getBody());
				// getSolrTagService().findOrStore(indexedTag);
			} catch (Exception e) {
				Logger.getLogger(getClass().getName()).warn(
						"The annotation was stored correctly into the Mongo, but the Body tag was not indexed yet. ",
						e);
			}

			// save the time of the last SOLR indexing
			updateLastIndexingTime(res, newAnnotation.getLastUpdate());
		}

		return res;
	}

	public ModerationRecord storeModerationRecord(ModerationRecord newModerationRecord) {

		// must have annotaionId with resourceId and provider.
		validateAnnotationIdForModerationRecord(newModerationRecord);

		// store in mongo database
		ModerationRecord res = getMongoModerationRecordPersistence().store(newModerationRecord);

		// lastindexe
		Date lastindexing = res.getLastUpdated();

		if (getConfiguration().isIndexingEnabled()) {
			try {
				Annotation annotation = getMongoPersistance().find(res.getAnnotationId());
				reindexAnnotation(annotation, lastindexing);
			} catch (Exception e) {
				Logger.getLogger(getClass().getName()).warn(
						"The moderation record was stored correctly into the Mongo, but related annotation was not indexed with summary yet. ",
						e);
			}
		}

		return res;
	}

	public ModerationRecord findModerationRecordById(AnnotationId annoId) throws ModerationMongoException {
		return getMongoModerationRecordPersistence().find(annoId);

	}

	/**
	 * This method validates AnnotationId object.
	 * 
	 * @param newAnnotation
	 */
	private void validateAnnotationId(Annotation newAnnotation) {

		if (newAnnotation.getAnnotationId() == null)
			throw new AnnotationValidationException("Annotaion.AnnotationId must not be null!");

		// if (newAnnotation.getAnnotationId().getResourceId() == null)
		// throw new AnnotationValidationException(
		// "Annotaion.AnnotationId.resourceId must not be null!");

		if (newAnnotation.getAnnotationId().getProvider() == null)
			throw new AnnotationValidationException("Annotaion.AnnotationId.provider must not be null!");
	}

	/**
	 * This method validates AnnotationId object for moderation record.
	 * 
	 * @param newModerationRecord
	 */
	private void validateAnnotationIdForModerationRecord(ModerationRecord newModerationRecord) {

		if (newModerationRecord.getAnnotationId() == null)
			throw new ModerationRecordValidationException("ModerationRecord.AnnotationId must not be null!");

		if (newModerationRecord.getAnnotationId().getProvider() == null)
			throw new ModerationRecordValidationException("ModerationRecord.AnnotationId.provider must not be null!");
	}

	@Override
	public Annotation updateAnnotation(Annotation annotation) {

		Annotation res = getMongoPersistence().update(annotation);

		if (getConfiguration().isIndexingEnabled())
			reindexAnnotation(res, res.getLastUpdate());

		return res;
	}

	@Override
	public Annotation updateAnnotationStatus(Annotation annotation) {

		Annotation res = getMongoPersistence().updateStatus(annotation);

		return res;
	}

	@Override
	public Annotation disableAnnotation(AnnotationId annoId) {
		try {
			// disable annotation
			Annotation res = getMongoPersistence().find(annoId);
			return disableAnnotation(res);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Annotation disableAnnotation(Annotation annotation) {
		Annotation res;
		try {
			annotation.setDisabled(true);
			res = getMongoPersistence().update(annotation);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		if (getConfiguration().isIndexingEnabled())
			removeFromIndex(annotation);

		return res;
	}

	protected void removeFromIndex(Annotation annotation) {
		try {
			getSolrService().delete(annotation.getAnnotationId());
		} catch (Exception e) {
			logger.error("Cannot remove annotation from solr index: " + annotation.getAnnotationId().toUri(), e);
		}
	}

	@Override
	public void deleteTag(String tagId) {
		try {
			Annotation res = getMongoPersistence().findByTagId(tagId);
			if (res == null) {
				// PersistentTag persistentTag =
				// getMongoTagPersistence().findByID(tagId);
				// SolrTag indexedTag =
				// copyPersistentTagIntoSolrTag(persistentTag);
				SolrTag solrTag = getSolrTagService().search(tagId).get(0);
				getSolrTagService().delete(solrTag);
				getMongoTagPersistence().remove(tagId);
			} else {
				throw new TagServiceException(
						"Tag with ID: '" + tagId + "' can't be removed since it is referenced by annotation '"
								+ res.getAnnotationId().toString() + "'.");
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<? extends Annotation> getAnnotationListByProvider(String resourceId, String provider) {
		return getMongoPersistence().getAnnotationListByProvider(resourceId, provider);
	}

	@Override
	public List<? extends Annotation> getAnnotationListByTarget(String target) {
		return getMongoPersistence().getAnnotationListByTarget(target);
	}

	@Override
	public List<? extends Annotation> getAnnotationListByResourceId(String resourceId) {
		return getMongoPersistence().getAnnotationListByResourceId(resourceId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.europeana.annotation.web.service.AnnotationService#existsInDb(eu.
	 * europeana.annotation.definitions.model.AnnotationId)
	 */
	public boolean existsInDb(AnnotationId annoId) {
		boolean res = false;
		try {
			Annotation dbRes = getMongoPersistence().find(annoId);
			if (dbRes != null)
				res = true;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return res;
	}

	public boolean existsModerationInDb(AnnotationId annoId) {
		boolean res = false;
		try {
			ModerationRecord dbRes = getMongoModerationRecordPersistence().find(annoId);
			if (dbRes != null)
				res = true;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return res;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.europeana.annotation.web.service.AnnotationService#existsProviderInDb(
	 * eu.europeana.annotation.definitions.model.Provider)
	 */
	public boolean existsProviderInDb(Provider provider) {
		boolean res = false;
		try {
			Provider dbRes = getMongoProviderPersistence().find(provider.getName(), provider.getIdGeneration());
			if (dbRes != null)
				res = true;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return res;
	}

	public void logAnnotationStatusUpdate(String user, Annotation annotation) {
		// store in mongo database
		StatusLog statusLog = new BaseStatusLog();
		statusLog.setUser(user);
		statusLog.setStatus(annotation.getStatus());
		long currentTimestamp = System.currentTimeMillis();
		statusLog.setDate(currentTimestamp);
		statusLog.setAnnotationId(annotation.getAnnotationId());
		getMongoStatusLogPersistence().store(statusLog);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.europeana.annotation.web.service.AnnotationService#checkVisibility(eu.
	 * europeana.annotation.definitions.model.Annotation, java.lang.String)
	 */
	public void checkVisibility(Annotation annotation, String user) throws AnnotationStateException {
		// Annotation res = null;
		// res =
		// getMongoPersistence().find(annotation.getAnnotationId().getProvider(),
		// annotation.getAnnotationId().getIdentifier());
		if (annotation.isDisabled())
			throw new AnnotationStateException(AnnotationStateException.MESSAGE_NOT_ACCESSIBLE,
					AnnotationStates.DISABLED);

		// if (annotation.
		// StringUtils.isNotEmpty(user) && !user.equals("null") &&
		// !res.getAnnotatedBy().getName().equals(user))
		// throw new AnnotationStateException("Given user (" + user + ") does
		// not match to the 'annotatedBy' user ("
		// + res.getAnnotatedBy().getName() + ").");
		// TODO update when the authorization concept is specified
		if (annotation.isPrivate() && !annotation.getCreator().getOpenId().equals(user))
			throw new AnnotationStateException(AnnotationStateException.MESSAGE_NOT_ACCESSIBLE,
					AnnotationStates.PRIVATE);

	}

	@Override
	public void validateAnnotationId(AnnotationId annoId) throws ParamValidationException {
		switch (annoId.getProvider()) {
		case WebAnnotationFields.PROVIDER_HISTORY_PIN:
			if (annoId.getIdentifier() == null || Long.parseLong(annoId.getIdentifier()) < 1)
				throw new ParamValidationException(ParamValidationException.MESSAGE_IDENTIFIER_NULL,
						WebAnnotationFields.PROVIDER + "/" + WebAnnotationFields.IDENTIFIER, annoId.toUri());
			break;
		case WebAnnotationFields.PROVIDER_PUNDIT:
			if (annoId.getIdentifier() == null || Long.parseLong(annoId.getIdentifier()) < 1)
				throw new ParamValidationException(ParamValidationException.MESSAGE_IDENTIFIER_NULL,
						WebAnnotationFields.PROVIDER + "/" + WebAnnotationFields.IDENTIFIER, annoId.toUri());
			break;
		case WebAnnotationFields.PROVIDER_BASE:
			if (annoId.getIdentifier() != null)
				throw new ParamValidationException(ParamValidationException.MESSAGE_IDENTIFIER_NOT_NULL,
						WebAnnotationFields.PROVIDER + "/" + WebAnnotationFields.IDENTIFIER, annoId.toUri());
			break;
		case WebAnnotationFields.PROVIDER_WEBANNO:
			if (annoId.getIdentifier() != null)
				throw new ParamValidationException(ParamValidationException.MESSAGE_IDENTIFIER_NOT_NULL,
						WebAnnotationFields.PROVIDER + "/" + WebAnnotationFields.IDENTIFIER, annoId.toUri());
			break;
		case WebAnnotationFields.PROVIDER_COLLECTIONS:
			if (annoId.getIdentifier() != null)
				throw new ParamValidationException(ParamValidationException.MESSAGE_IDENTIFIER_NOT_NULL,
						WebAnnotationFields.PROVIDER + "/" + WebAnnotationFields.IDENTIFIER, annoId.toUri());
			break;
		case WebAnnotationFields.PROVIDER_EUROPEANA_DEV:
			if (annoId.getIdentifier() != null)
				throw new ParamValidationException(ParamValidationException.MESSAGE_IDENTIFIER_NOT_NULL,
						WebAnnotationFields.PROVIDER + "/" + WebAnnotationFields.IDENTIFIER, annoId.toUri());
			break;
		case WebAnnotationFields.PROVIDER_WITH:
			if (annoId.getIdentifier() != null)
				throw new ParamValidationException(ParamValidationException.MESSAGE_IDENTIFIER_NOT_NULL,
						WebAnnotationFields.PROVIDER + "/" + WebAnnotationFields.IDENTIFIER, annoId.toUri());
			break;

		default:
			throw new ParamValidationException(WebAnnotationFields.INVALID_PROVIDER, WebAnnotationFields.PROVIDER,
					annoId.getProvider());
		}

	}

	protected boolean validateResource(String url) throws ParamValidationException {

		String domainName;
		try {
			domainName = getMongoWhitelistPersistence().getDomainName(url);
			Set<String> domains = getMongoWhitelistPersistence().getWhitelistDomains();
			if (!domains.contains(domainName))
				throw new ParamValidationException(ParamValidationException.MESSAGE_INVALID_PARAMETER_VALUE,
						"target.value", url);
		} catch (URISyntaxException e) {
			throw new ParamValidationException(ParamValidationException.MESSAGE_URL_NOT_VALID, "target.value", url);
		}

		return true;
	}

	/**
	 * Validation of simple tags.
	 * 
	 * Pre-processing: Trim spaces. If the tag is encapsulated by double or
	 * single quotes, remove these.
	 *
	 * Validation rules: A maximum of 64 characters is allowed for the tag. Tags
	 * cannot be URIs, tags which start with http://, ftp:// or https:// are not
	 * allowed.
	 *
	 * Examples of allowed tags: black, white, "black and white" (will become
	 * tag: black and white)
	 *
	 * @param webAnnotation
	 */
	private void validateTag(Annotation webAnnotation) throws ParamValidationException {
		// webAnnotation.
		Body body = webAnnotation.getBody();

		// TODO: the body type shouldn't be null at this stage
		if (body.getType() != null && body.getType().contains(WebAnnotationFields.SPECIFIC_RESOURCE)) {
			validateTagWithSpecificResource(body);
		} else if (BodyInternalTypes.isSemanticTagBody(body.getInternalType())) {
			validateSemanticTagUrl(body);
		} else if (BodyInternalTypes.isGeoTagBody(body.getInternalType())) {
			validateGeoTag(body);
		} else {
			validateTagWithValue(body);
		}
	}

	private void validateGeoTag(Body body) throws ParamValidationException {
		if (!(body instanceof PlaceBody))
			throw new ParamValidationException(ParamValidationException.MESSAGE_WRONG_CLASS, "tag.body.class", body.getClass().toString());

		Place place = ((PlaceBody) body).getPlace();
		
		if(StringUtils.isEmpty(place.getLatitude()))
			throw new ParamValidationException(ParamValidationException.MESSAGE_MISSING_MANDATORY_FIELD, "tag.body.latitude", null);
				
		if(StringUtils.isEmpty(place.getLongitude()))
			throw new ParamValidationException(ParamValidationException.MESSAGE_WRONG_CLASS, "tag.body.longitude", null);

	}

	private void validateTagWithSpecificResource(Body body) throws ParamValidationException {
		// check mandatory fields
		if (Strings.isNullOrEmpty(body.getInternalType().toString()))
			throw new ParamValidationException(ParamValidationException.MESSAGE_MISSING_MANDATORY_FIELD,
					"tag.body.type", body.getType().toString());
		if (Strings.isNullOrEmpty(body.getSource()))
			throw new ParamValidationException(ParamValidationException.MESSAGE_MISSING_MANDATORY_FIELD,
					"tag.body.source", body.getSource());

		// source must be an URL
		if (!isUrl(body.getSource()))
			throw new ParamValidationException(ParamValidationException.MESSAGE_INVALID_TAG_SPECIFIC_RESOURCE,
					"tag.format", body.getSource());

		// id is not a mandatory field but if exists it must be an URL
		if (body.getHttpUri() != null && !isUrl(body.getHttpUri()))
			throw new ParamValidationException(ParamValidationException.MESSAGE_INVALID_TAG_ID_FORMAT,
					"tag.body.httpUri", body.getHttpUri());
	}

	private void validateTagWithValue(Body body) throws ParamValidationException {

		String value = body.getValue();

		value = value.trim();
		// remove leading and end quotes
		if (value.startsWith("\"")) {
			int secondPosition = 1;
			value = value.substring(secondPosition);
			value = value.trim();
		}

		if (value.endsWith("\"")) {
			int secondLastPosition = value.length() - 1;
			value = value.substring(0, secondLastPosition);
			value = value.trim();
		}

		// reset the tag value with the trimmed value
		body.setValue(value);

		int MAX_TAG_LENGTH = 64;

		if (isUrl(value))
			throw new ParamValidationException(ParamValidationException.MESSAGE_INVALID_SIMPLE_TAG, "tag.format",
					value);
		else if (value.length() > MAX_TAG_LENGTH)
			throw new ParamValidationException(ParamValidationException.MESSAGE_INVALID_TAG_SIZE, "tag.size",
					"" + value.length());

	}

	protected void validateSemanticTagUrl(Body body) {
		// TODO Add whitelist based validation here

	}

	private boolean isUrl(String value) {

		// if (value.startsWith("http://") || value.startsWith("ftp://") ||
		// value.startsWith("https://")) {
		try {
			new URL(value);
		} catch (MalformedURLException e) {
			return false;
		}
		return true;
	}

	@Override
	public void validateWebAnnotation(Annotation webAnnotation) throws ParamValidationException {

		switch (webAnnotation.getMotivationType()) {
		case LINKING:
			// validate target URLs against whitelist
			if (webAnnotation.getTarget().getValue() != null)
				validateResource(webAnnotation.getTarget().getValue());

			if (webAnnotation.getTarget().getValues() != null)
				for (String url : webAnnotation.getTarget().getValues()) {
					validateResource(url);
				}
			break;
		case TAGGING:
			validateTag(webAnnotation);
			break;
		default:
			break;
		}
	}
	
}
