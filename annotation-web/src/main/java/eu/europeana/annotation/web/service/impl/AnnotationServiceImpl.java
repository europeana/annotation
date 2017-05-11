package eu.europeana.annotation.web.service.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.stanbol.commons.exception.JsonParseException;

import com.google.common.base.Strings;

import eu.europeana.annotation.definitions.exception.AnnotationAttributeInstantiationException;
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
import eu.europeana.annotation.definitions.model.utils.AnnotationHttpUrls;
import eu.europeana.annotation.definitions.model.vocabulary.BodyInternalTypes;
import eu.europeana.annotation.definitions.model.vocabulary.IdGenerationTypes;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.mongo.exception.ModerationMongoException;
import eu.europeana.annotation.mongo.model.internal.PersistentAnnotation;
import eu.europeana.annotation.mongo.service.PersistentConceptService;
import eu.europeana.annotation.mongo.service.PersistentProviderService;
import eu.europeana.annotation.mongo.service.PersistentStatusLogService;
import eu.europeana.annotation.mongo.service.PersistentTagService;
import eu.europeana.annotation.mongo.service.PersistentWhitelistService;
import eu.europeana.annotation.solr.exceptions.AnnotationServiceException;
import eu.europeana.annotation.solr.exceptions.StatusLogServiceException;
import eu.europeana.annotation.solr.exceptions.TagServiceException;
import eu.europeana.annotation.solr.model.internal.SolrTag;
import eu.europeana.annotation.solr.vocabulary.SolrSyntaxConstants;
import eu.europeana.annotation.utils.UriUtils;
import eu.europeana.annotation.utils.parse.AnnotationLdParser;
import eu.europeana.annotation.web.exception.HttpException;
import eu.europeana.annotation.web.exception.request.ParamValidationException;
import eu.europeana.annotation.web.exception.request.RequestBodyValidationException;
import eu.europeana.annotation.web.model.BatchReportable;
import eu.europeana.annotation.web.model.BatchUploadStatus;
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

	// @Override
	// public List<? extends Annotation> getFilteredAnnotationList(String
	// resourceId, String startOn, String limit,
	// boolean isDisabled) {
	// return getMongoPersistence().getFilteredAnnotationList(resourceId, null,
	// startOn, limit, isDisabled);
	// }

	@Override
	public List<? extends Annotation> searchAnnotations(String query) throws AnnotationServiceException {
		return null;
		// // return getSolrService().search(query);
	}

	@Override
	public List<? extends Annotation> searchAnnotations(String query, String startOn, String limit)
			throws AnnotationServiceException {
		return null;
		// // return getSolrService().search(query, startOn, limit);
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
			throws JsonParseException, HttpException {

		/**
		 * parse JsonLd string using JsonLdParser. JsonLd string -> JsonLdParser
		 * -> JsonLd object
		 */
		try {
			AnnotationLdParser europeanaParser = new AnnotationLdParser();
			return europeanaParser.parseAnnotation(motivationType, annotationJsonLdStr);
		} catch (AnnotationAttributeInstantiationException e) {
			throw new RequestBodyValidationException(annotationJsonLdStr, e);
		}
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
	public Annotation updateAnnotation(PersistentAnnotation persistentAnnotation, Annotation webAnnotation) {

		mergeAnnotationProperties(persistentAnnotation, webAnnotation);

		Annotation res = getMongoPersistence().update(persistentAnnotation);

		// reindex annotation
		if (getConfiguration().isIndexingEnabled())
			reindexAnnotation(res, res.getLastUpdate());

		return res;
	}

	private void mergeAnnotationProperties(PersistentAnnotation annotation, Annotation updatedWebAnnotation) {
		if (updatedWebAnnotation.getType() != null)
			annotation.setType(updatedWebAnnotation.getType());

		// So my decision for the moment would be to only keep the "id" and
		// "created" immutable.
		//
		// With regards to the logic when each of the fields is missing:
		// - If modified is missing, update with the current timestamp,
		// otherwise overwrite.
		// - if creator is missing, keep the previous creator, otherwise
		// overwrite.
		// - if generator is missing, keep the previous generator, otherwise
		// overwrite.
		// - the generated should always be determined by the server.
		// - motivation, body and target are overwritten.

		// Motivation can be changed see #122
		// if (updatedWebAnnotation.getMotivationType() != null
		// && updatedWebAnnotation.getMotivationType() !=
		// storedAnnotation.getMotivationType())
		// throw new RuntimeException("Cannot change motivation type from: " +
		// storedAnnotation.getMotivationType()
		// + " to: " + updatedWebAnnotation.getMotivationType());
		// if (updatedWebAnnotation.getMotivation() != null)
		// currentWebAnnotation.setMotivation(updatedWebAnnotation.getMotivation());

		if (updatedWebAnnotation.getLastUpdate() != null) {
			annotation.setLastUpdate(updatedWebAnnotation.getLastUpdate());
		} else {
			Date timeStamp = new java.util.Date();
			annotation.setLastUpdate(timeStamp);
		}

		if (updatedWebAnnotation.getCreator() != null)
			annotation.setCreator(updatedWebAnnotation.getCreator());

		if (updatedWebAnnotation.getGenerator() != null)
			annotation.setGenerator(updatedWebAnnotation.getGenerator());

		if (updatedWebAnnotation.getCreated() != null)
			annotation.setCreated(updatedWebAnnotation.getCreated());
		// if (updatedWebAnnotation.getCreator() != null)
		// annotation.setCreator(updatedWebAnnotation.getCreator());
		if (updatedWebAnnotation.getGenerated() != null)
			annotation.setGenerated(updatedWebAnnotation.getGenerated());
		// if (updatedWebAnnotation.getGenerator() != null)
		// annotation.setGenerator(updatedWebAnnotation.getGenerator());
		if (updatedWebAnnotation.getBody() != null)
			annotation.setBody(updatedWebAnnotation.getBody());
		if (updatedWebAnnotation.getTarget() != null)
			annotation.setTarget(updatedWebAnnotation.getTarget());
		if (annotation.isDisabled() != updatedWebAnnotation.isDisabled())
			annotation.setDisabled(updatedWebAnnotation.isDisabled());
		if (updatedWebAnnotation.getEquivalentTo() != null)
			annotation.setEquivalentTo(updatedWebAnnotation.getEquivalentTo());
		if (updatedWebAnnotation.getInternalType() != null)
			annotation.setInternalType(updatedWebAnnotation.getInternalType());
		// if (updatedWebAnnotation.getLastUpdate() != null)
		// annotation.setLastUpdate(updatedWebAnnotation.getLastUpdate());
		if (updatedWebAnnotation.getSameAs() != null)
			annotation.setSameAs(updatedWebAnnotation.getSameAs());
		if (updatedWebAnnotation.getStatus() != null)
			annotation.setStatus(updatedWebAnnotation.getStatus());
		if (updatedWebAnnotation.getStyledBy() != null)
			annotation.setStyledBy(updatedWebAnnotation.getStyledBy());
		if (updatedWebAnnotation.getCanonical() != null)
			// #404 must never be overwritten
			if (StringUtils.isEmpty(annotation.getCanonical()))
				annotation.setCanonical(updatedWebAnnotation.getCanonical());
		if (updatedWebAnnotation.getVia() != null)
			annotation.setVia(updatedWebAnnotation.getVia());
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
		PersistentAnnotation persistentAnnotation;
		try {
			if (annotation instanceof PersistentAnnotation)
				persistentAnnotation = (PersistentAnnotation) annotation;
			else
				persistentAnnotation = getMongoPersistence().find(annotation.getAnnotationId());

			persistentAnnotation.setDisabled(true);
			persistentAnnotation = getMongoPersistence().update(persistentAnnotation);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		if (getConfiguration().isIndexingEnabled())
			removeFromIndex(annotation);

		return persistentAnnotation;
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

	@Override
	public List<? extends Annotation> getExisting(List<String> annotationHttpUrls) {
		try {
			List<? extends Annotation> dbRes = getMongoPersistence().getAnnotationList(annotationHttpUrls);
			return dbRes;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
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

	@Override
	public void validateAnnotationId(AnnotationId annoId) throws ParamValidationException {
		switch (annoId.getProvider()) {
		case WebAnnotationFields.PROVIDER_HISTORY_PIN:
			if (annoId.getIdentifier() == null)
				throw new ParamValidationException(ParamValidationException.MESSAGE_IDENTIFIER_NULL,
						WebAnnotationFields.PROVIDER + "/" + WebAnnotationFields.IDENTIFIER, annoId.toUri());
			break;
		case WebAnnotationFields.PROVIDER_PUNDIT:
			if (annoId.getIdentifier() == null)
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
			throw new ParamValidationException(ParamValidationException.MESSAGE_WRONG_CLASS, "tag.body.class",
					body.getClass().toString());

		Place place = ((PlaceBody) body).getPlace();

		if (StringUtils.isEmpty(place.getLatitude()))
			throw new ParamValidationException(ParamValidationException.MESSAGE_MISSING_MANDATORY_FIELD,
					"tag.body.latitude", null);

		if (StringUtils.isEmpty(place.getLongitude()))
			throw new ParamValidationException(ParamValidationException.MESSAGE_WRONG_CLASS, "tag.body.longitude",
					null);

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
		if (!eu.europeana.annotation.utils.UriUtils.isUrl(body.getSource()))
			throw new ParamValidationException(ParamValidationException.MESSAGE_INVALID_TAG_SPECIFIC_RESOURCE,
					"tag.format", body.getSource());

		// id is not a mandatory field but if exists it must be an URL
		if (body.getHttpUri() != null && !eu.europeana.annotation.utils.UriUtils.isUrl(body.getHttpUri()))
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

		if (eu.europeana.annotation.utils.UriUtils.isUrl(value))
			throw new ParamValidationException(ParamValidationException.MESSAGE_INVALID_SIMPLE_TAG, "tag.format",
					value);
		else if (value.length() > MAX_TAG_LENGTH)
			throw new ParamValidationException(ParamValidationException.MESSAGE_INVALID_TAG_SIZE, "tag.size",
					"" + value.length());

	}

	protected void validateSemanticTagUrl(Body body) {
		// TODO Add whitelist based validation here

	}

	@Override
	public void validateWebAnnotations(List<? extends Annotation> webAnnotations, BatchReportable batchReportable) {
		int position = 1;
		for (Annotation webanno : webAnnotations) {
			try {
				validateWebAnnotation(webanno);
				batchReportable.incrementSuccessCount();
			} catch (ParamValidationException e) {
				batchReportable.incrementFailureCount();
				batchReportable.addError(position, e.getMessage());
			}
			position++;
		}
	}

	@Override
	public void reportNonExisting(List<? extends Annotation> annotations, BatchReportable batchReportable,
			List<String> missingHttpUrls) {
		int position = 1;
		for (Annotation anno : annotations) {
			String httpUrl = anno.getHttpUrl();
			if (httpUrl != null) {
				if (missingHttpUrls.contains(httpUrl)) {
					batchReportable.incrementFailureCount();
					batchReportable.addError(position, "Annotation does not exist: " + httpUrl);
				} else
					batchReportable.incrementSuccessCount();
			}
			position++;
		}
	}

	@Override
	public void validateWebAnnotation(Annotation webAnnotation) throws ParamValidationException {

		// validate canonical to be an absolute URI
		if (webAnnotation.getCanonical() != null) {
			try {
				URI cannonicalUri = URI.create(webAnnotation.getCanonical());
				if (!cannonicalUri.isAbsolute())
					throw new ParamValidationException("The canonical URI is not absolute:",
							WebAnnotationFields.CANONICAL, webAnnotation.getCanonical());
			} catch (IllegalArgumentException e) {
				throw new ParamValidationException("Error when validating canonical URI:",
						WebAnnotationFields.CANONICAL, webAnnotation.getCanonical(), e);
			}
		}

		// validate via to be valid URL(s)
		if (webAnnotation.getVia() != null) {
			if (webAnnotation.getVia() instanceof String[]) {
				for (String via : webAnnotation.getVia()) {
					if (!(UriUtils.isUrl(via)))
						throw new ParamValidationException("This is not a valid URL:", WebAnnotationFields.VIA, via);
				}
			}
		}

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

	@Override
	public void updateExistingAnnotations(AnnotationHttpUrls annotationsWithId) {
		// TODO Auto-generated method stub
		
	}

}
