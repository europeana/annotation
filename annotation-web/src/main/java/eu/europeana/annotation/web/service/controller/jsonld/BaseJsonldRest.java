package eu.europeana.annotation.web.service.controller.jsonld;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient.RemoteSolrException;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.apache.stanbol.commons.jsonld.JsonLd;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import com.google.gson.Gson;
import eu.europeana.annotation.definitions.exception.AnnotationAttributeInstantiationException;
import eu.europeana.annotation.definitions.exception.AnnotationInstantiationException;
import eu.europeana.annotation.definitions.exception.AnnotationValidationException;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.definitions.model.factory.impl.AgentObjectFactory;
import eu.europeana.annotation.definitions.model.impl.AbstractAnnotation;
import eu.europeana.annotation.definitions.model.moderation.ModerationRecord;
import eu.europeana.annotation.definitions.model.moderation.Summary;
import eu.europeana.annotation.definitions.model.moderation.Vote;
import eu.europeana.annotation.definitions.model.moderation.impl.BaseModerationRecord;
import eu.europeana.annotation.definitions.model.moderation.impl.BaseSummary;
import eu.europeana.annotation.definitions.model.moderation.impl.BaseVote;
import eu.europeana.annotation.definitions.model.search.SearchProfiles;
import eu.europeana.annotation.definitions.model.search.result.AnnotationPage;
import eu.europeana.annotation.definitions.model.search.result.impl.AnnotationPageImpl;
import eu.europeana.annotation.definitions.model.utils.AnnotationIdHelper;
import eu.europeana.annotation.definitions.model.utils.AnnotationsList;
import eu.europeana.annotation.definitions.model.vocabulary.AgentTypes;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.mongo.model.internal.PersistentAnnotation;
import eu.europeana.annotation.solr.exceptions.AnnotationServiceException;
import eu.europeana.annotation.utils.parse.AnnotationPageParser;
import eu.europeana.annotation.utils.serialize.AnnotationLdSerializer;
import eu.europeana.annotation.utils.serialize.AnnotationPageSerializer;
import eu.europeana.annotation.web.exception.InternalServerException;
import eu.europeana.annotation.web.exception.authorization.OperationAuthorizationException;
import eu.europeana.annotation.web.exception.request.AnnotationUniquenessValidationException;
import eu.europeana.annotation.web.exception.request.ParamValidationI18NException;
import eu.europeana.annotation.web.exception.request.RequestBodyValidationException;
import eu.europeana.annotation.web.exception.response.BatchUploadException;
import eu.europeana.annotation.web.http.AnnotationHttpHeaders;
import eu.europeana.annotation.web.model.BatchOperationStep;
import eu.europeana.annotation.web.model.BatchUploadStatus;
import eu.europeana.annotation.web.model.vocabulary.UserRoles;
import eu.europeana.annotation.web.service.AnnotationDefaults;
import eu.europeana.annotation.web.service.SearchServiceUtils;
import eu.europeana.annotation.web.service.controller.BaseRest;
import eu.europeana.api.common.config.I18nConstants;
import eu.europeana.api.commons.web.definitions.WebFields;
import eu.europeana.api.commons.web.exception.ApplicationAuthenticationException;
import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.api.commons.web.http.HttpHeaders;

public class BaseJsonldRest extends BaseRest {

    protected ResponseEntity<String> storeAnnotation(MotivationTypes motivation, boolean indexOnCreate,
	    String annotation, Authentication authentication) throws HttpException {
	
      Annotation webAnnotation = null;
      try {
	    // parse
	    webAnnotation = getAnnotationService().parseAnnotationLd(motivation, annotation);

		// validate annotation and check that no generator and creator exists in input
	    // set generator and creator
	    String userId = authentication.getPrincipal().toString();
	    String apikeyId = authentication.getDetails().toString();
	    String generatorId = AnnotationIdHelper.buildGeneratorUri(getConfiguration().getAnnoClientApiEndpoint(), apikeyId);
	    String creatorId = AnnotationIdHelper.buildCreatorUri(getConfiguration().getAnnoUserDataEndpoint(), userId);

	    webAnnotation.setGenerator(buildAgent(generatorId));
	    webAnnotation.setCreator(buildAgent(creatorId));

	    // 2. validate
	    // annotation id cannot be provided in the input of the create method
	    if (!(webAnnotation.getIdentifier()==0))
		throw new ParamValidationI18NException(ParamValidationI18NException.MESSAGE_ANNOTATION_IDENTIFIER_PROVIDED_UPON_CREATION,
			I18nConstants.ANNOTATION_VALIDATION,
			new String[] { "identifier", String.valueOf(webAnnotation.getIdentifier()) });
	    // 2.1 validate annotation properties
	    getAnnotationService().validateWebAnnotation(webAnnotation);
	    
	    //check the annotation uniqueness, only after validation 
        List<String> duplicateAnnotationIds = getAnnotationService().checkDuplicateAnnotations(webAnnotation, false);
        if(duplicateAnnotationIds!=null) {
            String [] i18nParamsAnnoDuplicates = new String [1];
            i18nParamsAnnoDuplicates[0]=String.join(",", duplicateAnnotationIds);
            throw new AnnotationUniquenessValidationException(I18nConstants.ANNOTATION_DUPLICATION,
                    I18nConstants.ANNOTATION_DUPLICATION, i18nParamsAnnoDuplicates);
        }

        // 3-6 create ID and annotation + backend validation
        long annoIdentifier = mongoPersistance.generateAnnotationIdentifier();
	    webAnnotation.setIdentifier(annoIdentifier);

	    // validate api key ... and request limit only if the request is
	    // correct (avoid useless DB requests)
	    // Done in authorize user
	    // validateApiKey(wsKey);

	    Annotation storedAnnotation = getAnnotationService().storeAnnotation(webAnnotation, indexOnCreate);

	    // serialize to jsonld
        JsonLd annotationLd = new AnnotationLdSerializer(storedAnnotation, getConfiguration().getAnnotationBaseUrl());
        String jsonLd = annotationLd.toString(4);

	    // build response entity with headers
	    // TODO: clarify serialization ETag: "_87e52ce126126"
	    // TODO: clarify Allow: PUT,GET,DELETE,OPTIONS,HEAD,PATCH
	    String apiVersion = getConfiguration().getAnnotationApiVersion();
	    String eTag = generateETag(storedAnnotation.getGenerated(), WebFields.FORMAT_JSONLD, apiVersion);

	    MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>(5);
	    headers.add(HttpHeaders.VARY, HttpHeaders.ACCEPT);
	    headers.add(HttpHeaders.ETAG, eTag);
	    headers.add(HttpHeaders.LINK, HttpHeaders.VALUE_LDP_RESOURCE);
	    headers.add(HttpHeaders.ALLOW, HttpHeaders.ALLOW_POST);

	    ResponseEntity<String> response = new ResponseEntity<String>(jsonLd, headers, HttpStatus.CREATED);

	    return response;

	} catch (JsonParseException e) {
	    throw new RequestBodyValidationException(annotation, I18nConstants.ANNOTATION_CANT_PARSE_BODY, e);
	} catch (AnnotationValidationException e) { // TODO: transform to
						    // checked annotation type
	    throw new RequestBodyValidationException(annotation, I18nConstants.ANNOTATION_CANT_PARSE_BODY, e);
	} catch (AnnotationAttributeInstantiationException e) {
	    throw new RequestBodyValidationException(annotation, I18nConstants.ANNOTATION_CANT_PARSE_BODY, e);
	} catch (AnnotationInstantiationException e) {
	    throw new HttpException(null, I18nConstants.ANNOTATION_INVALID_BODY, null, HttpStatus.BAD_REQUEST, e);
	} catch (HttpException e) {
	    // avoid wrapping HttpExceptions
	    throw e;
	} catch (AnnotationServiceException e) {
	    String debugInfo = (webAnnotation != null) ?  webAnnotation.toString() : ""; 
	    throw SearchServiceUtils.convertSearchException(debugInfo, e);
	} catch (Exception e) {
	    throw new InternalServerException(e);
	}

    }

    /**
     * 
     * @param wsKey
     * @param annotationPageIn
     * @param authentication
     * @return
     * @throws HttpException
     */
    protected ResponseEntity<String> storeAnnotations(String annotationPageIn, Authentication authentication)
	    throws HttpException {
	try {

	    String userId = authentication.getPrincipal().toString();

	    // parse annotation page
	    AnnotationPageParser annoPageParser = new AnnotationPageParser();
	    AnnotationPage annotationPage = annoPageParser.parseAnnotationPage(annotationPageIn);
	    List<? extends Annotation> annotations = annotationPage.getAnnotations();

	    // initialize upload status
	    BatchUploadStatus uploadStatus = new BatchUploadStatus();
	    uploadStatus.setTotalNumberOfAnnotations(annotations.size());

	    // validate annotations
	    uploadStatus.setStep(BatchOperationStep.VALIDATION);
	    getAnnotationService().validateWebAnnotations(annotations, uploadStatus);

	    // in case of validation errors, return error report
	    if (uploadStatus.getFailureCount() > 0)
		throw new BatchUploadException(uploadStatus.toString(), uploadStatus);

	    AnnotationsList webAnnotations = new AnnotationsList(annotationPage.getAnnotations());

	    // annotations are separated into those with identifier (assumed updates)
	    // and those without identifier (new annotations which should be created);
	    // first annotations with identifier (assumed updates)
	    AnnotationsList annosWithId = webAnnotations.getAnnotationsWithId();
	    uploadStatus.setNumberOfAnnotationsWithId(annosWithId.size());

	    // verify if the annotations with identifiers exist in the database
	    List<Long> annoIdentifiers = annosWithId.getIdentifiers();
	    AnnotationsList existingInDb;

	    if (!annoIdentifiers.isEmpty()) {
	      existingInDb = new AnnotationsList(getAnnotationService().getExisting(annoIdentifiers));
	    }
	    else {
	      existingInDb = new AnnotationsList(new ArrayList<AbstractAnnotation>(0));
	    }

	    // consistency (annotations with identifier must match existing annotations)
	    uploadStatus.setStep(BatchOperationStep.CHECK_UPDATE_ANNOTATIONS_AVAILABLE);
	    if (annosWithId.size() != existingInDb.size()) {
		// remove existing identifiers, the remaining list contains only missing identifiers
	    annoIdentifiers.removeAll(existingInDb.getIdentifiers());
		getAnnotationService().reportNonExisting(annotations, uploadStatus, annoIdentifiers);
		throw new BatchUploadException(uploadStatus.toString(), uploadStatus, HttpStatus.NOT_FOUND);
	    }

	    LinkedHashMap<Annotation, Annotation> webAnnoStoredAnnoAnnoMap = webAnnotations.getAnnotationsMap();

	    // update existing annotations
	    if (annosWithId.getAnnotations().size() > 0) {
		uploadStatus.setStep(BatchOperationStep.UPDATE_EXISTING_ANNOTATIONS);
		getAnnotationService().updateExistingAnnotations(uploadStatus, existingInDb.getAnnotations(),
		    annosWithId.getAnnotations(), webAnnoStoredAnnoAnnoMap);
	    }
	    // annotations are separated into those with identifier (assumed updates)
	    // and those without identifier (new annotations which should be created);
	    // second annotations without (assumed inserts)
	    AnnotationsList annosWithoutId = webAnnotations.getAnnotationsWithoutId();
	    uploadStatus.setStep(BatchOperationStep.INSERT_NEW_ANNOTATIONS);
	    uploadStatus.setNumberOfAnnotationsWithoutId(annosWithoutId.size());
	    // default values
	    if (annosWithoutId.size() > 0) {
		String apikeyId = authentication.getDetails().toString();
		String generatorId = AnnotationIdHelper.buildGeneratorUri(getConfiguration().getAnnoClientApiEndpoint(), apikeyId);
		String creatorId = AnnotationIdHelper.buildCreatorUri(getConfiguration().getAnnoUserDataEndpoint(), userId);

//				getAuthorizationService().authorizeUser(userId,authentication, Operations.CREATE);
		AnnotationDefaults annoDefaults = new AnnotationDefaults.Builder().setGenerator(buildAgent(generatorId))
			.setUser(buildAgent(creatorId)).build();
		getAnnotationService().insertNewAnnotations(uploadStatus, annosWithoutId.getAnnotations(), annoDefaults,
			webAnnoStoredAnnoAnnoMap);
	    }

	    // create result annotation page
	    AnnotationPage apRes = new AnnotationPageImpl();
	    List<Annotation> resList = new ArrayList<Annotation>();
	    // the "web annotation - stored annotation" map has preserved the order of
	    // submitted annotations
	    for (Annotation ann : webAnnoStoredAnnoAnnoMap.keySet())
		resList.add(webAnnoStoredAnnoAnnoMap.get(ann));
	    apRes.setAnnotations(resList);
	    apRes.setTotalInCollection(resList.size());
	    apRes.setTotalInPage(resList.size());
//			apRes.setCurrentPageUri("http://UNDEFINED");

	    String jsonLd = (new AnnotationPageSerializer(apRes, getConfiguration().getAnnotationBaseUrl())).serialize(SearchProfiles.STANDARD);
	    
	    MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>(3);
	    headers.add(HttpHeaders.VARY, HttpHeaders.ACCEPT);
	    headers.add(HttpHeaders.LINK, HttpHeaders.VALUE_LDP_RESOURCE);
	    headers.add(HttpHeaders.ALLOW, HttpHeaders.ALLOW_POST);

	    // build response
	    ResponseEntity<String> response = new ResponseEntity<String>(jsonLd, headers, HttpStatus.CREATED);

	    return response;

	} catch (AnnotationInstantiationException e) {
	    throw new HttpException("The submitted annotation body is invalid!", I18nConstants.ANNOTATION_INVALID_BODY,
		    null, HttpStatus.BAD_REQUEST, e);
	} catch (HttpException e) {
	    // avoid wrapping HttpExceptions
	    throw e;
	} catch (Exception e) {
	    throw new InternalServerException(e);
	}

    }

    /**
     * This method builds agent object
     * 
     * @param id agent id
     * @return agent as an Agent object
     */
    protected Agent buildAgent(String id) {
	Agent agent = AgentObjectFactory.getInstance().createObjectInstance(AgentTypes.PERSON);
	agent.setHttpUrl(id);
//		agent.setName(id);		
	return agent;
    }

    /**
     * This method retrieves annotation by ID optionally providing profile and
     * language
     * 
     * @param identifier
     * @param profileStr e.g. "dereference"
     * @param language   e.g.
     *                   "en,pl,de,nl,fr,it,da,sv,el,fi,hu,cs,sl,et,pt,es,lt,lv,bg,ro,sk,hr,ga,mt,no,ca,ru"
     * @return
     * @throws HttpException
     */
    protected ResponseEntity<String> getAnnotationById(long identifier, String profileStr, String language)
	    throws HttpException {

	try {

	    // 4. If annotation doesn’t exist respond with HTTP 404 (if provided
	    // annotation id doesn’t exists )
	    // 4.or 410 (if the user is not allowed to access the annotation);
	    Annotation annotation = getAnnotationService().getAnnotationById(identifier, null, true);

	    SearchProfiles searchProfile = SearchProfiles.getByStr(profileStr);
	    // will update body if dereference profile is used
	    getAnnotationService().dereferenceSemanticTags(annotation, searchProfile, language);

        JsonLd annotationLd = new AnnotationLdSerializer(annotation, getConfiguration().getAnnotationBaseUrl());
        String jsonLd = annotationLd.toString(4);

	    String apiVersion = getConfiguration().getAnnotationApiVersion();
	    String eTag = generateETag(annotation.getGenerated(), WebFields.FORMAT_JSONLD, apiVersion);

	    MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>(5);
	    headers.add(HttpHeaders.VARY, HttpHeaders.ACCEPT);
	    headers.add(HttpHeaders.ETAG, eTag);
	    headers.add(HttpHeaders.LINK, HttpHeaders.VALUE_LDP_RESOURCE);
	    headers.add(HttpHeaders.ALLOW, AnnotationHttpHeaders.ALLOW_GPuDOH);

	    ResponseEntity<String> response = new ResponseEntity<String>(jsonLd, headers, HttpStatus.OK);

	    return response;

	} catch (RuntimeException e) {
	    // not found ..
	    throw new InternalServerException(e);
	} catch (HttpException e) {
	    // avoid wrapping http exception
	    throw e;
	} catch (Exception e) {
	    throw new InternalServerException(e);
	}
    }

    protected ResponseEntity<String> getModerationReportSummary(String wsKey, long identifier) throws HttpException {

	try {

	    // 2. Check client access (a valid “wskey” must be provided)
//			validateApiKey(wsKey, WebAnnotationFields.READ_METHOD);

	    // 4. If annotation doesn’t exist respond with HTTP 404 (if provided
	    // moderation id doesn’t exists )
	    ModerationRecord moderationRecord = getAnnotationService().findModerationRecordById(identifier);
	    if (moderationRecord == null)
		moderationRecord = buildNewModerationRecord(identifier, null);

	    Gson gsonObj = new Gson();
	    String jsonString = gsonObj.toJson(moderationRecord.getSummary());

	    MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>(5);
	    headers.add(HttpHeaders.VARY, HttpHeaders.ACCEPT);
	    headers.add(HttpHeaders.ETAG, Integer.toString(hashCode()));
	    // headers.add(HttpHeaders.LINK, HttpHeaders.VALUE_LDP_RESOURCE);
	    headers.add(HttpHeaders.ALLOW, HttpHeaders.ALLOW_GET);

	    // build response
	    ResponseEntity<String> response = new ResponseEntity<String>(jsonString, headers, HttpStatus.OK);

	    return response;

	} catch (RuntimeException e) {
	    // not found ..
	    throw new InternalServerException(e);
	} catch (HttpException e) {
	    // avoid wrapping http exception
	    throw e;
	} catch (Exception e) {
	    throw new InternalServerException(e);
	}
    }

    /**
     * This method validates input values wsKey, identifier and userToken.
     * 
     * @param identifier
     * @param userId
     * @param enabled
     * @return
     * @return annotation object
     * @throws HttpException
     */
    private Annotation verifyOwnerOrAdmin(long identifier, Authentication authentication, boolean enabled) throws HttpException {

	String userId = AnnotationIdHelper.buildCreatorUri(getConfiguration().getAnnoUserDataEndpoint(), (String)authentication.getPrincipal());
	Annotation annotation = getAnnotationService().getAnnotationById(identifier, userId, enabled);
	
	//verify ownership
	boolean isOwner = annotation.getCreator().getHttpUrl().equals(userId);
	if(isOwner || hasAdminRights(authentication)) {
	    //approve owner or admin
	    return annotation;
	}else {
	    //not authorized
		//not authorized
	    throw new ApplicationAuthenticationException(I18nConstants.OPERATION_NOT_AUTHORIZED,
		    I18nConstants.OPERATION_NOT_AUTHORIZED, new String[] { "Only the creators of the annotation or admins are authorized to perform this operation."},
		    HttpStatus.FORBIDDEN);
	}
    }

    private boolean hasAdminRights(Authentication authentication) {
	
	for (Iterator<? extends GrantedAuthority> iterator = authentication.getAuthorities().iterator(); iterator.hasNext();) {
	    //role based authorization
	    String role = iterator.next().getAuthority();
	    if(UserRoles.admin.getName().equals(role)){
		return true;
	    }
	}
	return false;
    }

    /**
     * This method validates input values, retrieves annotation object and updates
     * it.
     * 
     * @param identifier
     * @param annotation
     * @param authentication Contains user name
     * @param action
     * @return response entity that comprises response body, headers and status code
     * @throws HttpException
     */
    protected ResponseEntity<String> updateAnnotation(long identifier, String annotation,
	    Authentication authentication, HttpServletRequest request) throws HttpException {

	try {
//	    String userId = authentication.getPrincipal().toString();

	    // 1. authorize user
	    // already performed in verify write access
	    // getAuthorizationService().authorizeUser(userId, authentication, annoId,
	    // Operations.UPDATE);
	    // check permissions for update
	    Annotation storedAnnotation = verifyOwnerOrAdmin(identifier, authentication, true);

	    // 2. check time stamp
	    // 3. validate new description for format and fields
	    // 4. generate new and replace existing time stamp for annotation

	    // Retrieve an annotation based on its identifier;
//			PersistentAnnotation storedAnnotation = getAnnotationForUpdate(getConfiguration().getAnnotationBaseUrl(), provider,
//					identifier);

	    // TODO: #431 update specification steps performed here
	    // 5. parse updated annotation
	    Annotation updateWebAnnotation = getAnnotationService().parseAnnotationLd(null, annotation);

	    // validate annotation
	    String apiVersion = getConfiguration().getAnnotationApiVersion();
	    String eTagOrigin = generateETag(storedAnnotation.getGenerated(), WebFields.FORMAT_JSONLD, apiVersion);

	    checkIfMatchHeader(eTagOrigin, request);
	    getAnnotationService().validateWebAnnotation(updateWebAnnotation);

	    // 6. apply updates - merge current and updated annotation
	    // 7. and call database update method
	    Annotation updatedAnnotation = getAnnotationService()
		    .updateAnnotation((PersistentAnnotation) storedAnnotation, updateWebAnnotation);

	    String eTag = generateETag(updatedAnnotation.getGenerated(), WebFields.FORMAT_JSONLD, apiVersion);

	    // serialize to jsonld
        JsonLd annotationLd = new AnnotationLdSerializer(updatedAnnotation, getConfiguration().getAnnotationBaseUrl());
        String jsonLd = annotationLd.toString(4);

	    // build response entity with headers
	    MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>(5);
	    headers.add(HttpHeaders.VARY, HttpHeaders.ACCEPT);
	    headers.add(HttpHeaders.ETAG, eTag);
	    headers.add(HttpHeaders.LINK, HttpHeaders.VALUE_LDP_RESOURCE);
	    headers.add(HttpHeaders.ALLOW, HttpHeaders.ALLOW_GPuD);

	    ResponseEntity<String> response = new ResponseEntity<String>(jsonLd, headers, HttpStatus.OK);

	    return response;

	} catch (JsonParseException e) {
	    throw new RequestBodyValidationException(annotation, I18nConstants.ANNOTATION_CANT_PARSE_BODY, e);
	} catch (AnnotationValidationException e) {
	    throw new RequestBodyValidationException(annotation, I18nConstants.ANNOTATION_CANT_PARSE_BODY, e);
	} catch (HttpException e) {
	    throw e;
	} catch (AnnotationInstantiationException e) {
	    throw new HttpException("The submitted annotation body is invalid!", I18nConstants.ANNOTATION_VALIDATION,
		    null, HttpStatus.BAD_REQUEST, e);
	}  catch (AnnotationServiceException e) {
      throw SearchServiceUtils.convertSearchException(annotation, e);
    } catch (Exception e) {
      throw new InternalServerException(e);
    }
    }

    /**
     * This method validates input values, retrieves annotation object and deletes
     * it.
     * 
     * @param wsKey
     * @param identifier
     * @param authentication Contains user name
     * @param action
     * @return response entity that comprises response body, headers and status code
     * @throws HttpException
     */
    protected ResponseEntity<String> deleteAnnotation(long identifier, Authentication authentication, HttpServletRequest request)
	    throws HttpException {

	try {
//	    String userId = authentication.getPrincipal().toString();

	    // 5. authorize user
	    // already performed in verify write access
//			getAuthorizationService().authorizeUser(userId, authentication, annoId, Operations.DELETE);

	    // Retrieve an annotation based on its id;
	    // Verify if user is allowed to perform the deletion.
	    Annotation storedAnno = verifyOwnerOrAdmin(identifier, authentication, true);

	    // validate annotation
	    String apiVersion = getConfiguration().getAnnotationApiVersion();
	    String eTagOrigin = generateETag(storedAnno.getGenerated(), WebFields.FORMAT_JSONLD, apiVersion);

	    checkIfMatchHeader(eTagOrigin, request);

	    // call database delete method that deactivates existing Annotation
	    // in Mongo
	    getAnnotationService().disableAnnotation(storedAnno);

	    MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>(5);
	    headers.add(HttpHeaders.VARY, HttpHeaders.ACCEPT);

	    ResponseEntity<String> response = new ResponseEntity<String>(null, headers, HttpStatus.NO_CONTENT);

	    return response;

	} catch (HttpException e) {
	    // avoid wrapping HttpExceptions
	    // TODO: change this when OAUTH is implemented and the user information is
	    // available in service
	    throw e;
	} catch (Exception e) {
	    throw new InternalServerException(e);
	}
    }
    
    /**
     * This method enables the disabled annotation. It validates the input values, 
     * updates the annotation object in the database and creates it in solr.
     * @param identifier
     * @param authentication Contains user name
     * @param request An HttpServletRequest
     * @return response entity that comprises response body, headers and status code
     * @throws HttpException
     */
    protected ResponseEntity<String> enableAnnotation(long identifier, Authentication authentication, HttpServletRequest request)
	    throws HttpException {

	try {
	    // Retrieve an annotation based on its id.
	    // Verify if user is allowed to perform the action.
	    Annotation storedAnno = verifyOwnerOrAdmin(identifier, authentication, false);

	    // validate annotation
	    String apiVersion = getConfiguration().getAnnotationApiVersion();
	    String eTagOrigin = generateETag(storedAnno.getGenerated(), WebFields.FORMAT_JSONLD, apiVersion);
	    checkIfMatchHeader(eTagOrigin, request);

	    getAnnotationService().enableAnnotation(storedAnno.getIdentifier());

	    MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>(5);
	    headers.add(HttpHeaders.VARY, HttpHeaders.ACCEPT);
	    
        JsonLd annotationLd = new AnnotationLdSerializer(storedAnno, getConfiguration().getAnnotationBaseUrl());
        String jsonLd = annotationLd.toString(4);

	    ResponseEntity<String> response = new ResponseEntity<String>(jsonLd, headers, HttpStatus.OK);

	    return response;

	} catch (HttpException e) {
	    // avoid wrapping HttpExceptions
	    // TODO: change this when OAUTH is implemented and the user information is
	    // available in service
	    throw e;
	} catch (Exception e) {
	    throw new InternalServerException(e);
	}
    }

    /**
     * @param wsKey
     * @param provider
     * @param identifier
     * @param authentication Contains user name
     * @return
     * @throws HttpException
     */
    protected ResponseEntity<String> storeAnnotationReport(long identifier, Authentication authentication)
	    throws HttpException {
	try {
	
	    // 1. authorize user
	    // already performed in verify write access
//			getAuthorizationService().authorizeUser(userId, authentication, annoId, Operations.REPORT);

	    // 2. build and verify annotation ID
	    String userId = authentication.getPrincipal().toString();
	    if(!getAnnotationService().existsInDb(identifier)) {
		throw new ParamValidationI18NException(ParamValidationI18NException.MESSAGE_ANNOTATION_ID_EXISTS,
			I18nConstants.ANNOTATION_VALIDATION,
			new String[] { "identifier", String.valueOf(identifier) });
	    }
	    	
	    // build vote
	    Date reportDate = new Date();
	    Vote vote = buildVote(buildAgent(userId), reportDate);

	    // 3. Check if the user has already reported this annotation
	    ModerationRecord moderationRecord = getAnnotationService().findModerationRecordById(identifier);
	    if (moderationRecord == null)
		moderationRecord = buildNewModerationRecord(identifier, reportDate);
	    else
		validateVote(moderationRecord, vote);

	    moderationRecord.addReport(vote);
	    moderationRecord.computeSummary();
	    moderationRecord.setLastUpdated(reportDate);

	    // update record in the database
	    ModerationRecord storedModeration = getAnnotationService().storeModerationRecord(moderationRecord);

	    // build response
	    MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>(5);
	    headers.add(HttpHeaders.VARY, HttpHeaders.ACCEPT);
	    headers.add(HttpHeaders.ETAG, Long.toString(storedModeration.getLastUpdated().hashCode()));
	    // headers.add(HttpHeaders.LINK,
	    // "<http://www.w3.org/ns/ldp#Resource>; rel=\"type\"");
	    headers.add(HttpHeaders.VARY, HttpHeaders.ACCEPT);

	    ResponseEntity<String> response = new ResponseEntity<String>(null, headers, HttpStatus.CREATED);
	    return response;

	} catch (HttpException e) {
	    // avoid wrapping HttpExceptions
	    throw e;
	} catch (Exception e) {
	    throw new InternalServerException(e);
	}
    }

    // TODO :consider moving to persistent moderation record service
    private void validateVote(ModerationRecord moderationRecord, Vote vote) throws OperationAuthorizationException {
	for (Vote existingVote : moderationRecord.getReportList()) {
	    if (vote.getUserId().equals(existingVote.getUserId()))
		throw new OperationAuthorizationException("A report from the same users exists in database!",
			I18nConstants.OPERATION_NOT_AUTHORIZED, new String[] { vote.getUserId() });
	}
    }

    protected ModerationRecord buildNewModerationRecord(long annoIdentifier, Date reportDate) {
	// create moderation record
	ModerationRecord moderationRecord = new BaseModerationRecord();
	moderationRecord.setIdentifier(annoIdentifier);

	// SET DEFAULTS
	moderationRecord.setCreated(reportDate);
	moderationRecord.setLastUpdated(reportDate);

	Summary summary = new BaseSummary();
	moderationRecord.setSummary(summary);
	return moderationRecord;
    }

    protected Vote buildVote(Agent user, Date reportDate) {
	Vote vote = new BaseVote();
	vote.setCreated(reportDate);
	vote.setUserId(user.getHttpUrl());
	return vote;
    }

}
