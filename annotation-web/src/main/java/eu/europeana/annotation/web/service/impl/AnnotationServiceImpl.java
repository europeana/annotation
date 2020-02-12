package eu.europeana.annotation.web.service.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.stanbol.commons.exception.JsonParseException;

import com.google.common.base.Strings;

import eu.europeana.annotation.config.AnnotationConfiguration;
import eu.europeana.annotation.definitions.exception.AnnotationAttributeInstantiationException;
import eu.europeana.annotation.definitions.exception.AnnotationValidationException;
import eu.europeana.annotation.definitions.exception.ModerationRecordValidationException;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.StatusLog;
import eu.europeana.annotation.definitions.model.body.Body;
import eu.europeana.annotation.definitions.model.body.PlaceBody;
import eu.europeana.annotation.definitions.model.body.impl.EdmAgentBody;
import eu.europeana.annotation.definitions.model.entity.Place;
import eu.europeana.annotation.definitions.model.impl.BaseAnnotationId;
import eu.europeana.annotation.definitions.model.impl.BaseStatusLog;
import eu.europeana.annotation.definitions.model.moderation.ModerationRecord;
import eu.europeana.annotation.definitions.model.search.SearchProfiles;
import eu.europeana.annotation.definitions.model.utils.AnnotationBuilder;
import eu.europeana.annotation.definitions.model.utils.AnnotationIdHelper;
import eu.europeana.annotation.definitions.model.vocabulary.BodyInternalTypes;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.dereferenciation.MetisDereferenciationClient;
import eu.europeana.annotation.mongo.exception.BulkOperationException;
import eu.europeana.annotation.mongo.exception.ModerationMongoException;
import eu.europeana.annotation.mongo.model.internal.PersistentAnnotation;
import eu.europeana.annotation.mongo.service.PersistentStatusLogService;
import eu.europeana.annotation.mongo.service.PersistentWhitelistService;
import eu.europeana.annotation.solr.exceptions.AnnotationServiceException;
import eu.europeana.annotation.solr.exceptions.StatusLogServiceException;
import eu.europeana.annotation.solr.vocabulary.SolrSyntaxConstants;
import eu.europeana.annotation.utils.UriUtils;
import eu.europeana.annotation.utils.parse.AnnotationLdParser;
import eu.europeana.annotation.web.exception.request.ParamValidationException;
import eu.europeana.annotation.web.exception.request.RequestBodyValidationException;
import eu.europeana.annotation.web.model.BatchReportable;
import eu.europeana.annotation.web.model.BatchUploadStatus;
import eu.europeana.annotation.web.service.AnnotationDefaults;
import eu.europeana.annotation.web.service.AnnotationService;
import eu.europeana.api.common.config.I18nConstants;
import eu.europeana.api.commons.config.i18n.I18nService;
import eu.europeana.api.commons.web.exception.HttpException;

public class AnnotationServiceImpl extends BaseAnnotationServiceImpl implements AnnotationService {

    @Resource
    PersistentWhitelistService mongoWhitelistPersistence;

    @Resource
    PersistentStatusLogService mongoStatusLogPersistence;

    @Resource
    private AnnotationConfiguration configuration;

    @Resource
    I18nService i18nService;
    
    private MetisDereferenciationClient dereferenciationClient = new MetisDereferenciationClient();

    AnnotationBuilder annotationBuilder;

    final AnnotationIdHelper annotationIdHelper = new AnnotationIdHelper();

    public AnnotationConfiguration getConfiguration() {
	return configuration;
    }

    public AnnotationIdHelper getAnnotationIdHelper() {
	return annotationIdHelper;
    }

    public AnnotationBuilder getAnnotationHelper() {
	if (annotationBuilder == null)
	    annotationBuilder = new AnnotationBuilder();
	return annotationBuilder;
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

    public MetisDereferenciationClient getDereferenciationClient() {
	return dereferenciationClient;
    }

    public void setDereferenciationClient(MetisDereferenciationClient dereferenciationClient) {
	this.dereferenciationClient = dereferenciationClient;
    }


    @Override
    @Deprecated
    /**
     * To remove this method. Update tests to use the commonly used search methods
     */
    public List<? extends Annotation> searchAnnotations(String query, String startOn, String limit)
	    throws AnnotationServiceException {
	return null;
//		// return getSolrService().search(query, startOn, limit);
    }

    @SuppressWarnings("deprecation")
    @Override
    public Map<String, Integer> searchAnnotations(String[] qf, List<String> queries) throws AnnotationServiceException {
	return getSolrService().queryFacetSearch(SolrSyntaxConstants.ALL_SOLR_ENTRIES, qf, queries);
    }

    @Override
    public List<? extends StatusLog> searchStatusLogs(String query, String startOn, String limit)
	    throws StatusLogServiceException {
	return getMongoStatusLogPersistence().getFilteredStatusLogList(query, startOn, limit);
    }

    @Override
    public Annotation parseAnnotationLd(MotivationTypes motivationType, String annotationJsonLdStr)
	    throws JsonParseException, HttpException {

	/**
	 * parse JsonLd string using JsonLdParser. JsonLd string -> JsonLdParser ->
	 * JsonLd object
	 */
	try {
	    AnnotationLdParser europeanaParser = new AnnotationLdParser();
	    return europeanaParser.parseAnnotation(motivationType, annotationJsonLdStr);
	} catch (AnnotationAttributeInstantiationException e) {
	    throw new RequestBodyValidationException(annotationJsonLdStr, I18nConstants.ANNOTATION_CANT_PARSE_BODY, e);
	}
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
		getLogger().info("The annotation was stored correctly into the Mongo, but it was not indexed yet. ", e);
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
		getLogger().warn(
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
    }

    /**
     * This method validates AnnotationId object for moderation record.
     * 
     * @param newModerationRecord
     */
    private void validateAnnotationIdForModerationRecord(ModerationRecord newModerationRecord) {

	if (newModerationRecord.getAnnotationId() == null)
	    throw new ModerationRecordValidationException("ModerationRecord.AnnotationId must not be null!");
    }

    @Override
    public Annotation updateAnnotation(PersistentAnnotation persistentAnnotation, Annotation webAnnotation) {
	mergeAnnotationProperties(persistentAnnotation, webAnnotation);
	Annotation res = updateAndReindex(persistentAnnotation);

	return res;
    }

    @SuppressWarnings("deprecation")
    private void mergeAnnotationProperties(PersistentAnnotation annotation, Annotation webAnnotation) {
	if (webAnnotation.getType() != null)
	    annotation.setType(webAnnotation.getType());

//		So my decision for the moment would be to only keep the "id" and "created" immutable.
//
//		With regards to the logic when each of the fields is missing:
//		- If modified is missing, update with the current timestamp, otherwise overwrite.
//		- if creator is missing, keep the previous creator, otherwise overwrite.
//		- if generator is missing, keep the previous generator, otherwise overwrite.
//		- the generated should always be determined by the server.
//		- motivation, body and target are overwritten.

	// Motivation can be changed see #122
//		if (updatedWebAnnotation.getMotivationType() != null
//				&& updatedWebAnnotation.getMotivationType() != storedAnnotation.getMotivationType())
//			throw new RuntimeException("Cannot change motivation type from: " + storedAnnotation.getMotivationType()
//					+ " to: " + updatedWebAnnotation.getMotivationType());
	// if (updatedWebAnnotation.getMotivation() != null)
	// currentWebAnnotation.setMotivation(updatedWebAnnotation.getMotivation());

	if (webAnnotation.getLastUpdate() != null) {
	    annotation.setLastUpdate(webAnnotation.getLastUpdate());
	} else {
	    Date timeStamp = new java.util.Date();
	    annotation.setLastUpdate(timeStamp);
	}

// creator and generator are mandatory, and set during the creation	
//	if (updatedWebAnnotation.getCreator() != null)
//	    annotation.setCreator(updatedWebAnnotation.getCreator());
//
//	if (updatedWebAnnotation.getGenerator() != null)
//	    annotation.setGenerator(updatedWebAnnotation.getGenerator());

//created must not be updated	
//	if (webAnnotation.getCreated() != null)
//	    annotation.setCreated(webAnnotation.getCreated());
//		if (updatedWebAnnotation.getCreator() != null)
//			annotation.setCreator(updatedWebAnnotation.getCreator());
	if (webAnnotation.getGenerated() != null)
	    annotation.setGenerated(webAnnotation.getGenerated());
//		if (updatedWebAnnotation.getGenerator() != null)
//			annotation.setGenerator(updatedWebAnnotation.getGenerator());
	if (webAnnotation.getBody() != null)
	    annotation.setBody(webAnnotation.getBody());
	if (webAnnotation.getTarget() != null)
	    annotation.setTarget(webAnnotation.getTarget());
	if (annotation.isDisabled() != webAnnotation.isDisabled())
	    annotation.setDisabled(webAnnotation.isDisabled());
	if (webAnnotation.getEquivalentTo() != null)
	    annotation.setEquivalentTo(webAnnotation.getEquivalentTo());
	if (webAnnotation.getInternalType() != null)
	    annotation.setInternalType(webAnnotation.getInternalType());
//		if (updatedWebAnnotation.getLastUpdate() != null)
//			annotation.setLastUpdate(updatedWebAnnotation.getLastUpdate());
	if (webAnnotation.getSameAs() != null)
	    annotation.setSameAs(webAnnotation.getSameAs());
	if (webAnnotation.getStatus() != null)
	    annotation.setStatus(webAnnotation.getStatus());
	if (webAnnotation.getStyledBy() != null)
	    annotation.setStyledBy(webAnnotation.getStyledBy());
	if (webAnnotation.getCanonical() != null) {
	    //TODO: #404 must never be overwritten
	    if (StringUtils.isEmpty(annotation.getCanonical())) {
		annotation.setCanonical(webAnnotation.getCanonical());
	    }
	}
	if (webAnnotation.getVia() != null)
	    annotation.setVia(webAnnotation.getVia());
    }

    @Override
    public Annotation updateAnnotationStatus(Annotation annotation) {

	return getMongoPersistence().updateStatus(annotation);
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
	    getLogger().error(
		    "Cannot remove annotation from solr index: " + annotation.getAnnotationId().toRelativeUri(), e);
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
//    public boolean existsProviderInDb(Provider provider) {
//	boolean res = false;
//	try {
//	    Provider dbRes = getMongoProviderPersistence().find(provider.getName(), provider.getIdGeneration());
//	    if (dbRes != null)
//		res = true;
//	} catch (Exception e) {
//	    throw new RuntimeException(e);
//	}
//	return res;
//    }

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
	if (annoId.getIdentifier() != null)
	    throw new ParamValidationException(ParamValidationException.MESSAGE_IDENTIFIER_NOT_NULL,
		    I18nConstants.MESSAGE_IDENTIFIER_NOT_NULL,
		    new String[] { WebAnnotationFields.IDENTIFIER, annoId.toRelativeUri() });
    }

    protected boolean validateResource(String url) throws ParamValidationException {

	String domainName;
	try {
	    domainName = getMongoWhitelistPersistence().getDomainName(url);
	    Set<String> domains = getMongoWhitelistPersistence().getWhitelistDomains();
	    if (!domains.contains(domainName))
		throw new ParamValidationException(ParamValidationException.MESSAGE_INVALID_PARAMETER_VALUE,
			I18nConstants.MESSAGE_INVALID_PARAMETER_VALUE, new String[] { "target.value", url });
	} catch (URISyntaxException e) {
	    throw new ParamValidationException(ParamValidationException.MESSAGE_URL_NOT_VALID,
		    I18nConstants.MESSAGE_URL_NOT_VALID, new String[] { "target.value", url });
	}

	return true;
    }

    /**
     * This method verifies if provided right is in a list of valid licenses
     * 
     * @param webAnnotation The right provided in the input from annotation object
     * @return true if provided right is in a list of valid licenses
     * @throws ParamValidationException
     * @throws RequestBodyValidationException
     */
    protected void validateEdmRights(Annotation webAnnotation)
	    throws ParamValidationException, RequestBodyValidationException {

	if (webAnnotation == null || webAnnotation.getBody() == null
		|| webAnnotation.getBody().getEdmRights() == null) {
	    return; // nothing to validate
	}

	// if rights are provided, check if it belongs to the valid license list
	String rightsClaim = webAnnotation.getBody().getEdmRights();
	String licence = null;
	// remove version from the right and get licenses
	char PATH_DELIMITER = '/';
	long delimiterCount = rightsClaim.chars().filter(ch -> ch == PATH_DELIMITER).count();

	if (delimiterCount != 6 || !rightsClaim.endsWith("" + PATH_DELIMITER)) {
	    // wrong format, max 6 (including the / after version, for )
	    throw new RequestBodyValidationException(webAnnotation.getBody().getInputString(),
		    I18nConstants.ANNOTATION_INVALID_RIGHTS, new String[] { rightsClaim });
	} else {
	    // remove last /
	    licence = rightsClaim.substring(0, rightsClaim.length() - 1);
	    // remove version, but preserve last /
	    int versionStart = licence.lastIndexOf(PATH_DELIMITER) + 1;
	    licence = licence.substring(0, versionStart);
	}
	Set<String> rights = getConfiguration().getAcceptedLicenceses();
	if (!rights.contains(licence))
	    throw new RequestBodyValidationException(webAnnotation.getBody().getInputString(),
		    I18nConstants.MESSAGE_INVALID_PARAMETER_VALUE, new String[] { rightsClaim });

    }

    /**
     * Validation of simple tags.
     * 
     * Pre-processing: Trim spaces. If the tag is encapsulated by double or single
     * quotes, remove these.
     *
     * Validation rules: A maximum of 64 characters is allowed for the tag. Tags
     * cannot be URIs, tags which start with http://, ftp:// or https:// are not
     * allowed.
     *
     * Examples of allowed tags: black, white, "black and white" (will become tag:
     * black and white)
     *
     * @param webAnnotation
     */
    private void validateTag(Annotation webAnnotation) throws ParamValidationException {
	// webAnnotation.
	Body body = webAnnotation.getBody();

	// TODO: the body type shouldn't be null at this stage
	if (body.getType() != null && body.getType().contains(WebAnnotationFields.SPECIFIC_RESOURCE)) {
	    validateTagWithSpecificResource(body);
	} else if (body.getType() != null && body.getType().contains(WebAnnotationFields.FULL_TEXT_RESOURCE)) {
	    validateTagWithFullTextResource(body);
	} else if (BodyInternalTypes.isSemanticTagBody(body.getInternalType())) {
	    validateSemanticTagUrl(body);
	} else if (BodyInternalTypes.isAgentBodyTag(body.getInternalType())) {
	    validateAgentBody(body);
	} else if (BodyInternalTypes.isGeoTagBody(body.getInternalType())) {
	    validateGeoTag(body);
	} else if (BodyInternalTypes.isVcardAddressTagBody(body.getInternalType())) {
	    validateVcardAddressBody(body);
	} else {
	    validateTagWithValue(body);
	}
    }

    /**
     * This method validate entity body
     * 
     * @param body The entity body
     * @throws ParamValidationException
     */
    private void validateAgentBody(Body body) throws ParamValidationException {
	if (!(body instanceof EdmAgentBody)) {
	    throw new ParamValidationException(ParamValidationException.MESSAGE_WRONG_CLASS,
		    I18nConstants.MESSAGE_WRONG_CLASS, new String[] { "tag.body.class", body.getClass().toString() });
	}
    }

    private void validateGeoTag(Body body) throws ParamValidationException {
	if (!(body instanceof PlaceBody))
	    throw new ParamValidationException(ParamValidationException.MESSAGE_WRONG_CLASS,
		    I18nConstants.MESSAGE_WRONG_CLASS, new String[] { "tag.body.class", body.getClass().toString() });

	Place place = ((PlaceBody) body).getPlace();

	if (StringUtils.isEmpty(place.getLatitude()))
	    throw new ParamValidationException(ParamValidationException.MESSAGE_MISSING_MANDATORY_FIELD,
		    I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD, new String[] { "tag.body.latitude" });

	if (StringUtils.isEmpty(place.getLongitude()))
	    throw new ParamValidationException(ParamValidationException.MESSAGE_WRONG_CLASS,
		    I18nConstants.MESSAGE_WRONG_CLASS, new String[] { "tag.body.longitude" });

    }

    private void validateTagWithSpecificResource(Body body) throws ParamValidationException {
	// check mandatory fields
	if (Strings.isNullOrEmpty(body.getInternalType().toString()))
	    throw new ParamValidationException(ParamValidationException.MESSAGE_MISSING_MANDATORY_FIELD,
		    I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD,
		    new String[] { "tag.body.type", body.getType().toString() });
	if (Strings.isNullOrEmpty(body.getSource()))
	    throw new ParamValidationException(ParamValidationException.MESSAGE_MISSING_MANDATORY_FIELD,
		    I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD,
		    new String[] { "tag.body.source", body.getSource() });

	// source must be an URL
	if (!eu.europeana.annotation.utils.UriUtils.isUrl(body.getSource()))
	    throw new ParamValidationException(ParamValidationException.MESSAGE_INVALID_TAG_SPECIFIC_RESOURCE,
		    I18nConstants.MESSAGE_INVALID_TAG_SPECIFIC_RESOURCE,
		    new String[] { "tag.format", body.getSource() });

	// id is not a mandatory field but if exists it must be an URL
	if (body.getHttpUri() != null && !eu.europeana.annotation.utils.UriUtils.isUrl(body.getHttpUri()))
	    throw new ParamValidationException(ParamValidationException.MESSAGE_INVALID_TAG_ID_FORMAT,
		    I18nConstants.MESSAGE_INVALID_TAG_ID_FORMAT,
		    new String[] { "tag.body.httpUri", body.getHttpUri() });
    }

    private void validateTagWithFullTextResource(Body body) throws ParamValidationException {

	// check mandatory fields

	// check type
	if (Strings.isNullOrEmpty(body.getInternalType().toString()))
	    throw new ParamValidationException(ParamValidationException.MESSAGE_MISSING_MANDATORY_FIELD,
		    I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD,
		    new String[] { "tag.body.type", body.getType().toString() });
	if (Strings.isNullOrEmpty(body.getSource()))
	    throw new ParamValidationException(ParamValidationException.MESSAGE_MISSING_MANDATORY_FIELD,
		    I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD,
		    new String[] { "tag.body.source", body.getSource() });
    }

    /**
     * @param body
     * @throws ParamValidationException
     */
    private void validateVcardAddressBody(Body body) throws ParamValidationException {

	// check mandatory fields

	// check type
	if (Strings.isNullOrEmpty(body.getInternalType().toString())
		|| !BodyInternalTypes.isVcardAddressTagBody(body.getInternalType()))
	    throw new ParamValidationException(ParamValidationException.MESSAGE_MISSING_MANDATORY_FIELD,
		    I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD,
		    new String[] { "tag.body.type", body.getType().toString() });
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
	    throw new ParamValidationException(ParamValidationException.MESSAGE_INVALID_SIMPLE_TAG,
		    I18nConstants.MESSAGE_INVALID_SIMPLE_TAG, new String[] { value });
	else if (value.length() > MAX_TAG_LENGTH)
	    throw new ParamValidationException(ParamValidationException.MESSAGE_INVALID_TAG_SIZE,
		    I18nConstants.MESSAGE_INVALID_TAG_SIZE, new String[] { String.valueOf(value.length()) });
    }

    protected void validateSemanticTagUrl(Body body) {
	// TODO Add whitelist based validation here

    }

    @Override
    public void validateWebAnnotations(List<? extends Annotation> webAnnotations, BatchReportable batchReportable) {
	for (Annotation webanno : webAnnotations) {
	    try {
		validateWebAnnotation(webanno);
		// TODO: validate via, size must be 1
		batchReportable.incrementSuccessCount();
	    } catch (ParamValidationException | RequestBodyValidationException e) {
		batchReportable.incrementFailureCount();
		String message = i18nService.getMessage(e.getI18nKey(), e.getI18nParams());
		batchReportable.addError(webanno.getAnnotationId().toHttpUrl(), message);
	    }
	}
    }

    @Override
    public void reportNonExisting(List<? extends Annotation> annotations, BatchReportable batchReportable,
	    List<String> missingHttpUrls) {
	for (Annotation anno : annotations) {
	    String httpUrl = anno.getAnnotationId().toHttpUrl();
	    if (httpUrl != null) {
		if (missingHttpUrls.contains(httpUrl)) {
		    batchReportable.incrementFailureCount();
		    batchReportable.addError(anno.getAnnotationId().toHttpUrl(),
			    "Annotation does not exist: " + httpUrl);
		} else
		    batchReportable.incrementSuccessCount();
	    }
	}
    }

    @Override
    public void validateWebAnnotation(Annotation webAnnotation)
	    throws ParamValidationException, RequestBodyValidationException {

	// validate canonical to be an absolute URI
	if (webAnnotation.getCanonical() != null) {
	    try {
		URI cannonicalUri = URI.create(webAnnotation.getCanonical());
		if (!cannonicalUri.isAbsolute())
		    throw new ParamValidationException("The canonical URI is not absolute:",
			    I18nConstants.ANNOTATION_VALIDATION,
			    new String[] { WebAnnotationFields.CANONICAL, webAnnotation.getCanonical() });
	    } catch (IllegalArgumentException e) {
		throw new ParamValidationException("Error when validating canonical URI:",
			I18nConstants.ANNOTATION_VALIDATION,
			new String[] { WebAnnotationFields.CANONICAL, webAnnotation.getCanonical() }, e);
	    }
	}

	// validate via to be valid URL(s)
	if (webAnnotation.getVia() != null) {
	    if (webAnnotation.getVia() instanceof String[]) {
		for (String via : webAnnotation.getVia()) {
		    if (!(UriUtils.isUrl(via)))
			throw new ParamValidationException("This is not a valid URL:",
				I18nConstants.ANNOTATION_VALIDATION, new String[] { WebAnnotationFields.VIA, via });
		}
	    }
	}

	switch (webAnnotation.getMotivationType()) {
	case LINKING:
	    // validate target URLs against whitelist
	    if (webAnnotation.getTarget() != null) {
		if (webAnnotation.getTarget().getValue() != null)
		    validateResource(webAnnotation.getTarget().getValue());

		if (webAnnotation.getTarget().getValues() != null)
		    for (String url : webAnnotation.getTarget().getValues()) {
			validateResource(url);
		    }
	    }
	    break;
	case TAGGING:
	    validateTag(webAnnotation);
	    break;
	case TRANSCRIBING:
	    validateEdmRights(webAnnotation);
	    break;
	default:
	    break;
	}
    }

    /**
     * Update existing annotations
     * 
     * @param batchReportable          Reportable object for collecting information
     *                                 to be reported
     * @param existingAnnos            Existing annotations
     * @param updateAnnos              Update annotations
     * @param webAnnoStoredAnnoAnnoMap Map required to maintain the correct sorting
     *                                 of annotations when returned as response
     */
    @Override
    public void updateExistingAnnotations(BatchReportable batchReportable, List<? extends Annotation> existingAnnos,
	    HashMap<String, ? extends Annotation> updateAnnos,
	    LinkedHashMap<Annotation, Annotation> webAnnoStoredAnnoAnnoMap)
	    throws AnnotationValidationException, BulkOperationException {
	// the size of existing and update lists must match (this must be checked
	// beforehand, so a runtime exception is sufficient here)
	if (existingAnnos.size() != updateAnnos.size())
	    throw new IllegalArgumentException("The existing and update lists must be of equal size");

	// Backup
	getMongoPersistence().createBackupCopy(existingAnnos);

	// merge update annotations (web anno) into existing annotations (db anno)
	for (int i = 0; i < existingAnnos.size(); i++) {
	    Annotation existingAnno = existingAnnos.get(i);
	    String existingHttpUrl = existingAnno.getAnnotationId().getHttpUrl();
	    Annotation updateAnno = updateAnnos.get(existingHttpUrl);

	    // merge update annotation (web anno) into existing annotation (db anno)
	    this.mergeAnnotationProperties((PersistentAnnotation) existingAnno, updateAnno);

	    // set last update
	    existingAnno.setLastUpdate(new Date());

	    // store annotation in the "web annotation - stored annotation" map - used to
	    // preserve the order
	    // of submitted annotations.
	    webAnnoStoredAnnoAnnoMap.put(updateAnno, existingAnno);
	}
	getMongoPersistence().update(existingAnnos);
    }

    /**
     * Update existing annotations
     * 
     * @param batchReportable          Reportable object for collecting information
     *                                 to be reported
     * @param existingAnnos            Existing annotations
     * @param updateAnnos              Update annotations
     * @param webAnnoStoredAnnoAnnoMap Map required to maintain the correct sorting
     *                                 of annotations when returned as response
     */
    @Override
    public void insertNewAnnotations(BatchUploadStatus uploadStatus, List<? extends Annotation> annotations,
	    AnnotationDefaults annoDefaults, LinkedHashMap<Annotation, Annotation> webAnnoStoredAnnoAnnoMap)
	    throws AnnotationValidationException, BulkOperationException {

	int count = annotations.size();

	List<AnnotationId> annoIdSequence = null;
	annoIdSequence = generateAnnotationIds(count);

	// number of ids must equal number of annotations - not applicable in case the
	// id is provided by the via field
	if ((annotations.size() != annoIdSequence.size()))
	    throw new IllegalStateException("The list of new annotations and corresponding ids are not of equal size");

	AnnotationId newAnnoId;
	Annotation anno;
	AnnotationId genAnnoId;
	for (int i = 0; i < annotations.size(); i++) {
	    // default: use the annotation id from the sequence generated above
//			if(!reuseViaIdentifier) {
	    genAnnoId = annoIdSequence.get(i);
	    newAnnoId = new BaseAnnotationId(genAnnoId.getBaseUrl(), genAnnoId.getIdentifier());
//			} else {// for some providers, the id must be provided by the via field 
//				String[] via = annotations.get(i).getVia();
//				if(via == null || via.length == 0)
//					throw new AnnotationValidationException("The annotation id must be provided by the via field for the provider: '"+provider+"'");
//				if(via.length > 1)
//					logger.warn("Multiple URLS provided in via field");
//				String viaUrl = via[0];
//				newAnnoId = getAnnotationIdHelper().getAnnotationIdBasedOnVia(getConfiguration().getAnnotationBaseUrl(), 
//						viaUrl);
//			}
	    anno = annotations.get(i);
	    anno.setAnnotationId(newAnnoId);
	    annoDefaults.putAnnotationDefaultValues(anno);
	    // store annotation in the "web annotation - stored annotation" map - used to
	    // preserve the order
	    // of submitted annotations.
	    webAnnoStoredAnnoAnnoMap.put(anno, anno);
	}
	getMongoPersistence().create(annotations);
    }

    public List<AnnotationId> generateAnnotationIds(int count) {
	List<AnnotationId> annoIdSequence = getMongoPersistence().generateAnnotationIdSequence(count);
	return annoIdSequence;
    }

    @Override
    public void dereferenceSemanticTags(Annotation annotation, SearchProfiles searchProfile, String language)
	    throws HttpException, IOException {

	if (!SearchProfiles.DEREFERENCE.equals(searchProfile)) {
	    return;
	}

	String bodyValue = annotation.getBody().getValue();
	if (!UriUtils.isUrl(bodyValue)) {
	    return;
	}

	List<String> queryList = Arrays.asList(bodyValue);
	String metisBaseUrl = getConfiguration().getMetisBaseUrl();

	Map<String, String> dereferencedMap = getDereferenciationClient().queryMetis(metisBaseUrl, queryList, language);
	String dereferencedJsonLdMapStr = dereferencedMap.get(bodyValue);
	// replace URI with dereferenced entity
	if (StringUtils.isNotEmpty(dereferencedJsonLdMapStr)) {
	    annotation.getBody().setValue(dereferencedJsonLdMapStr);
	    annotation.getBody().setInputString(dereferencedJsonLdMapStr);
	}
    }

}
