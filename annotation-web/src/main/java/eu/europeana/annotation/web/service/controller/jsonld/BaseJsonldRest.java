package eu.europeana.annotation.web.service.controller.jsonld;

import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.apache.stanbol.commons.jsonld.JsonLd;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.google.gson.Gson;

import eu.europeana.annotation.definitions.exception.AnnotationAttributeInstantiationException;
import eu.europeana.annotation.definitions.exception.AnnotationValidationException;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.definitions.model.factory.impl.AgentObjectFactory;
import eu.europeana.annotation.definitions.model.impl.BaseAnnotationId;
import eu.europeana.annotation.definitions.model.moderation.ModerationRecord;
import eu.europeana.annotation.definitions.model.moderation.Summary;
import eu.europeana.annotation.definitions.model.moderation.Vote;
import eu.europeana.annotation.definitions.model.moderation.impl.BaseModerationRecord;
import eu.europeana.annotation.definitions.model.moderation.impl.BaseSummary;
import eu.europeana.annotation.definitions.model.moderation.impl.BaseVote;
import eu.europeana.annotation.definitions.model.vocabulary.AgentTypes;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.solr.exceptions.AnnotationStateException;
import eu.europeana.annotation.utils.StringUtils;
import eu.europeana.annotation.utils.serialize.AnnotationLdSerializer;
import eu.europeana.annotation.web.exception.HttpException;
import eu.europeana.annotation.web.exception.InternalServerException;
import eu.europeana.annotation.web.exception.authorization.OperationAuthorizationException;
import eu.europeana.annotation.web.exception.authorization.UserAuthorizationException;
import eu.europeana.annotation.web.exception.request.ParamValidationException;
import eu.europeana.annotation.web.exception.request.RequestBodyValidationException;
import eu.europeana.annotation.web.exception.response.AnnotationNotFoundException;
import eu.europeana.annotation.web.http.AnnotationHttpHeaders;
import eu.europeana.annotation.web.http.HttpHeaders;
import eu.europeana.annotation.web.model.vocabulary.Operations;
import eu.europeana.annotation.web.service.authentication.model.Application;
import eu.europeana.annotation.web.service.controller.BaseRest;

public class BaseJsonldRest extends BaseRest {
	
	Logger logger = Logger.getLogger(getClass());

	protected ResponseEntity<String> storeAnnotation(String wsKey, MotivationTypes motivation, String provider,
			String identifier, boolean indexOnCreate, String annotation, String userToken) throws HttpException {
		try {

			// SET DEFAULTS
			Application app = getAuthenticationService().getByApiKey(wsKey);

			if (provider == null)
				provider = app.getProvider();

			// 0. annotation id
			AnnotationId annoId = buildAnnotationId(provider, identifier);

			// 1. authorize user
			Agent user = getAuthorizationService().authorizeUser(userToken, wsKey, annoId, Operations.CREATE);

			// parse
			Annotation webAnnotation = getAnnotationService().parseAnnotationLd(motivation, annotation);

			// SET DEFAULTS
			if (webAnnotation.getGenerator() == null)
				webAnnotation.setGenerator(buildDefaultGenerator(app));

			if (webAnnotation.getCreator() == null)
				webAnnotation.setCreator(user);

			// 2. validate
			// check whether annotation with the given provider and identifier
			// already exist in the database
			if (annoId.getIdentifier() != null && getAnnotationService().existsInDb(annoId))
				throw new ParamValidationException(ParamValidationException.MESSAGE_ANNOTATION_ID_EXISTS,
						"/provider/identifier", annoId.toUri());
			// 2.1 validate annotation properties
			getAnnotationService().validateWebAnnotation(webAnnotation);

			// 3-6 create ID and annotation + backend validation
			webAnnotation.setAnnotationId(annoId);

			// validate api key ... and request limit only if the request is
			// correct (avoid useless DB requests)
			// Done in authorize user
			// validateApiKey(wsKey);

			Annotation storedAnnotation = getAnnotationService().storeAnnotation(webAnnotation, indexOnCreate);

			// serialize to jsonld
			JsonLd annotationLd = new AnnotationLdSerializer(storedAnnotation);
			String jsonLd = annotationLd.toString(4);
			// return JsonWebUtils.toJson(jsonLd, null);

			// build response entity with headers
			// TODO: clarify serialization ETag: "_87e52ce126126"
			// TODO: clarify Allow: PUT,GET,DELETE,OPTIONS,HEAD,PATCH

			MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>(5);
			headers.add(HttpHeaders.VARY, HttpHeaders.ACCEPT);
			headers.add(HttpHeaders.ETAG, "" + storedAnnotation.getLastUpdate().hashCode());
			headers.add(HttpHeaders.LINK, HttpHeaders.VALUE_LDP_RESOURCE);
			headers.add(HttpHeaders.ALLOW, HttpHeaders.ALLOW_POST);

			ResponseEntity<String> response = new ResponseEntity<String>(jsonLd, headers, HttpStatus.CREATED);

			return response;

		} catch (JsonParseException e) {
			throw new RequestBodyValidationException(annotation, e);
		} catch (AnnotationValidationException e) { // TODO: transform to
													// checked annotation type
			throw new RequestBodyValidationException(annotation, e);
		} catch (AnnotationAttributeInstantiationException e) {
			throw new RequestBodyValidationException(annotation, e);
		} catch (HttpException e) {
			// avoid wrapping HttpExceptions
			throw e;
		} catch (Exception e) {
			throw new InternalServerException(e);
		}

	}

	Agent buildDefaultGenerator(Application app) {
		Agent serializer = AgentObjectFactory.getInstance().createObjectInstance(AgentTypes.SOFTWARE);
		serializer.setName(app.getName());
				
		serializer.setHomepage(app.getHomepage());
		
		// Set "id" attribute of Agent only if it is a valid URL
		String agentId = app.getOpenId();
		if(StringUtils.isUrl(agentId)) {
			logger.warn("The agent's 'id' attribute value is not set because the value is not a valid URL: " + agentId);
			serializer.setHttpUrl(agentId);
		}
		
		return serializer;
	}

	// Agent buildDefaultCreator(Application app, Agent user) {
	// Agent annotator =
	// AgentObjectFactory.getInstance().createObjectInstance(AgentTypes.PERSON);
	//
	// annotator.setName(app.getAnonymousUser().getName());
	// annotator.setHomepage(app.getAnonymousUser().getHomepage());
	// annotator.setOpenId(app.getAnonymousUser().getOpenId());
	//
	// return annotator;
	// }

	protected ResponseEntity<String> getAnnotationById(String wsKey, String provider, String identifier, String action)
			throws HttpException {

		try {

			// 2. Check client access (a valid “wskey” must be provided)
			validateApiKey(wsKey);

			// SET DEFAULTS
			Application app = getAuthenticationService().getByApiKey(wsKey);

			if (provider == null)
				provider = app.getProvider();

			// 3. Retrieve an annotation based on its identifier;
			AnnotationId annoId = new BaseAnnotationId(getConfiguration().getAnnotationBaseUrl(), provider, identifier);

			// CHECK IF Required
			// authorizeUser(userToken, wsKey, annoId, Operations.RETRIEVE);

			// 4. If annotation doesn’t exist respond with HTTP 404 (if provided
			// annotation id doesn’t exists )
			Annotation annotation = getAnnotationService().getAnnotationById(annoId);

			// 4.or 410 (if the user is not allowed to access the annotation);
			try {
				// check visibility
				getAnnotationService().checkVisibility(annotation, null);
			} catch (AnnotationStateException e) {
				if (annotation.isDisabled())
					throw new UserAuthorizationException(
							UserAuthorizationException.MESSAGE_ANNOTATION_STATE_NOT_ACCESSIBLE, annotation.getStatus(),
							HttpStatus.GONE, e);
				else
					throw new UserAuthorizationException(UserAuthorizationException.MESSAGE_USER_NOT_AUTHORIZED, wsKey,
							e);
			}

			JsonLd annotationLd = new AnnotationLdSerializer(annotation);
			String jsonLd = annotationLd.toString(4);

			int etag;
			if (annotation.getLastUpdate() != null)
				etag = annotation.getLastUpdate().hashCode();
			else
				etag = annotation.getGenerated().hashCode();

			MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>(5);
			headers.add(HttpHeaders.VARY, HttpHeaders.ACCEPT);
			headers.add(HttpHeaders.ETAG, "" + etag);
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

	/**
	 * 
	 * @param wskey
	 * @param provider
	 * @param identifier
	 * @param userToken
	 * @return
	 */
	protected ResponseEntity<String> optionsForCorsPreflight(String wsKey, String userToken, String provider,
			String identifier) throws HttpException {
		// Similar behaviour as GET method but different response code
		try {

			// 2. Check client access (a valid “wskey” must be provided)
			validateApiKey(wsKey);

			// CHECK user autorization
			getAuthorizationService().authorizeUser(userToken, wsKey, Operations.RETRIEVE);

			// SET DEFAULTS
			Application app = getAuthenticationService().getByApiKey(wsKey);

			if (provider == null)
				provider = app.getProvider();

			if (identifier != null) {
				// if OPTIONS /annotation/provider/identifier request
				// 3. Retrieve an annotation based on its identifier;
				AnnotationId annoId = new BaseAnnotationId(getConfiguration().getAnnotationBaseUrl(), provider,
						identifier);

				// 4. If annotation doesn’t exist respond with HTTP 404 (if
				// provided
				// annotation id doesn’t exists )
				if (!getAnnotationService().existsInDb(annoId))
					throw new AnnotationNotFoundException(AnnotationNotFoundException.MESSAGE_ANNOTATION_NO_FOUND,
							annoId.toUri());
			}

			MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>(5);
			headers.add(HttpHeaders.VARY, HttpHeaders.ACCEPT);
			
			headers.add(HttpHeaders.ALLOW, AnnotationHttpHeaders.ALLOW_GPuDOH);
//			headers.add(HttpHeaders.ALLOW, HttpHeaders.ALLOW_PGDOHP);
//			headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, HttpHeaders.ALLOW_PGDOHP);
			headers.add(HttpHeaders.ACCEPT_POST, HttpHeaders.VALUE_LDP_CONTENT_TYPE);

			ResponseEntity<String> response = new ResponseEntity<String>(null, headers, HttpStatus.NO_CONTENT);

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

	protected ResponseEntity<String> getModerationReportSummary(String wsKey, String provider, String identifier,
			String action) throws HttpException {

		try {

			// 2. Check client access (a valid “wskey” must be provided)
			validateApiKey(wsKey);

			// SET DEFAULTS
			Application app = getAuthenticationService().getByApiKey(wsKey);

			if (provider == null)
				provider = app.getProvider();

			// 3. Retrieve an annotation based on its identifier;
			AnnotationId annoId = new BaseAnnotationId(getConfiguration().getAnnotationBaseUrl(), provider, identifier);

			// 4. If annotation doesn’t exist respond with HTTP 404 (if provided
			// moderation id doesn’t exists )
			ModerationRecord moderationRecord = getAnnotationService().findModerationRecordById(annoId);
			if (moderationRecord == null)
				moderationRecord = buildNewModerationRecord(annoId, null);

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
	 * @param wsKey
	 * @param identifier
	 * @param userToken
	 * @return
	 * @return annotation object
	 * @throws HttpException
	 */
	private AnnotationId validateInputsForUpdateDelete(String wsKey, String provider, String identifier,
			String userToken) throws HttpException {

		// First check the API key as prio 0 check
		validateApiKey(wsKey);

		// check identifier
		if (provider == null || identifier == null)
			throw new ParamValidationException(ParamValidationException.MESSAGE_IDENTIFIER_WRONG,
					"/provider/identifier", identifier, HttpStatus.NOT_FOUND, null);

		// 1. build annotation id object
		AnnotationId annoId = new BaseAnnotationId(getConfiguration().getAnnotationBaseUrl(), provider, identifier);

		// 3. Retrieve an annotation based on its identifier;
		// Annotation annotation =
		// getAnnotationService().getAnnotationById(annoId);

		// 4. If annotation doesn’t exist respond with HTTP 404 (if provided
		// annotation id doesn’t exists )
		// throw new
		// AnnotationNotFoundException(AnnotationNotFoundException.MESSAGE_ANNOTATION_NO_FOUND,
		// annoId.toUri());

		// check whether annotation with the given provider and identifier
		// already exist in the database
		if (annoId.getIdentifier() != null && !getAnnotationService().existsInDb(annoId))
			throw new ParamValidationException(ParamValidationException.MESSAGE_ANNOTATION_ID_NOT_EXISTS,
					"/provider/identifier", annoId.toUri(), HttpStatus.NOT_FOUND, null);

		return annoId;
	}

	/**
	 * This method validates input values, retrieves annotation object and
	 * updates it.
	 * 
	 * @param wsKey
	 * @param identifier
	 * @param annotation
	 * @param userToken
	 * @param action
	 * @return response entity that comprises response body, headers and status
	 *         code
	 * @throws HttpException
	 */
	protected ResponseEntity<String> updateAnnotation(String wsKey, String provider, String identifier,
			String annotation, String userToken, String action) throws HttpException {

		try {

			// SET DEFAULTS
			getAuthenticationService().getByApiKey(wsKey);

			AnnotationId annoId = validateInputsForUpdateDelete(wsKey, provider, identifier, userToken);

			// 5. authorize user
			getAuthorizationService().authorizeUser(userToken, wsKey, annoId, Operations.UPDATE);

			// Retrieve an annotation based on its identifier;
			Annotation storedAnnotation = getAnnotationForUpdate(getConfiguration().getAnnotationBaseUrl(), provider,
					identifier);

			// 6. extract and check current annotation type and motivation
			MotivationTypes currentMotivation = storedAnnotation.getMotivationType();

			// TODO: the motivation should be verified during parsing. If
			// motivation is provided within the annotation object it must match
			// the provided one
			Annotation updateWebAnnotation = getAnnotationService().parseAnnotationLd(currentMotivation, annotation);

			// // 7. parse updated annotation
			// if (updatedMotivation != null)
			// currentMotivation = updatedMotivation;
			//
			// 8. apply updates - merge current and updated annotation
			updateValues(storedAnnotation, updateWebAnnotation);

			// 9. call database update method
			Annotation updatedAnnotation = getAnnotationService().updateAnnotation(storedAnnotation);

			// serialize to jsonld
			JsonLd annotationLd = new AnnotationLdSerializer(updatedAnnotation);
			String jsonLd = annotationLd.toString(4);

			// build response entity with headers
			MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>(5);
			headers.add(HttpHeaders.VARY, HttpHeaders.ACCEPT);
			headers.add(HttpHeaders.ETAG, "" + updatedAnnotation.getLastUpdate().hashCode());
			headers.add(HttpHeaders.LINK, HttpHeaders.VALUE_LDP_RESOURCE);
			headers.add(HttpHeaders.ALLOW, HttpHeaders.ALLOW_GPuD);

			ResponseEntity<String> response = new ResponseEntity<String>(jsonLd, headers, HttpStatus.OK);

			return response;

		} catch (JsonParseException e) {
			throw new RequestBodyValidationException(annotation, e);
		} catch (AnnotationValidationException e) { // TODO: transform to
													// checked annotation type
			throw new RequestBodyValidationException(annotation, e);
		} catch (HttpException e) {
			// avoid wrapping HttpExceptions
			throw e;
		} catch (Exception e) {
			throw new InternalServerException(e);
		}
	}

	/**
	 * This method updates existing annotation by replacing of the old values
	 * with a new values.
	 * 
	 * @param storedAnnotation
	 * @param updatedWebAnnotation
	 */
	private void updateValues(Annotation storedAnnotation, Annotation updatedWebAnnotation) {

		if (updatedWebAnnotation.getType() != null)
			storedAnnotation.setType(updatedWebAnnotation.getType());

		// Motivation must not be changed at least for now
		if (updatedWebAnnotation.getMotivationType() != null
				&& updatedWebAnnotation.getMotivationType() != storedAnnotation.getMotivationType())
			throw new RuntimeException("Cannot change motivation type from: " + storedAnnotation.getMotivationType()
					+ " to: " + updatedWebAnnotation.getMotivationType());
		// if (updatedWebAnnotation.getMotivation() != null)
		// currentWebAnnotation.setMotivation(updatedWebAnnotation.getMotivation());
		if (updatedWebAnnotation.getCreated() != null)
			storedAnnotation.setCreated(updatedWebAnnotation.getCreated());
		if (updatedWebAnnotation.getCreator() != null)
			storedAnnotation.setCreator(updatedWebAnnotation.getCreator());
		if (updatedWebAnnotation.getGenerated() != null)
			storedAnnotation.setGenerated(updatedWebAnnotation.getGenerated());
		if (updatedWebAnnotation.getGenerator() != null)
			storedAnnotation.setGenerator(updatedWebAnnotation.getGenerator());
		if (updatedWebAnnotation.getBody() != null)
			storedAnnotation.setBody(updatedWebAnnotation.getBody());
		if (updatedWebAnnotation.getTarget() != null)
			storedAnnotation.setTarget(updatedWebAnnotation.getTarget());
		if (storedAnnotation.isDisabled() != updatedWebAnnotation.isDisabled())
			storedAnnotation.setDisabled(updatedWebAnnotation.isDisabled());
		if (updatedWebAnnotation.getEquivalentTo() != null)
			storedAnnotation.setEquivalentTo(updatedWebAnnotation.getEquivalentTo());
		if (updatedWebAnnotation.getInternalType() != null)
			storedAnnotation.setInternalType(updatedWebAnnotation.getInternalType());
		if (updatedWebAnnotation.getLastUpdate() != null)
			storedAnnotation.setLastUpdate(updatedWebAnnotation.getLastUpdate());
		if (updatedWebAnnotation.getSameAs() != null)
			storedAnnotation.setSameAs(updatedWebAnnotation.getSameAs());
		if (updatedWebAnnotation.getStatus() != null)
			storedAnnotation.setStatus(updatedWebAnnotation.getStatus());
		if (updatedWebAnnotation.getStyledBy() != null)
			storedAnnotation.setStyledBy(updatedWebAnnotation.getStyledBy());
	}

	/**
	 * This method validates input values, retrieves annotation object and
	 * deletes it.
	 * 
	 * @param wsKey
	 * @param identifier
	 * @param userToken
	 * @param action
	 * @return response entity that comprises response body, headers and status
	 *         code
	 * @throws HttpException
	 */
	protected ResponseEntity<String> deleteAnnotation(String wsKey, String provider, String identifier,
			String userToken, String action) throws HttpException {

		try {
			// SET DEFAULTS
			Application app = getAuthenticationService().getByApiKey(wsKey);

			if (provider == null)
				provider = app.getProvider();

			AnnotationId annoId = validateInputsForUpdateDelete(wsKey, provider, identifier, userToken);

			// 5. authorize user
			getAuthorizationService().authorizeUser(userToken, wsKey, annoId, Operations.DELETE);

			// Retrieve an annotation based on its id;
			Annotation annotation = getAnnotationForUpdate(getConfiguration().getAnnotationBaseUrl(), provider,
					identifier);

			// TODO: Verify if user is allowed to perform the deletion.

			// call database delete method that deactivates existing Annotation
			// in Mongo
			getAnnotationService().disableAnnotation(annotation);

			MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>(5);
			headers.add(HttpHeaders.VARY, HttpHeaders.ACCEPT);

			ResponseEntity<String> response = new ResponseEntity<String>(null, headers, HttpStatus.NO_CONTENT);

			return response;

		} catch (HttpException e) {
			// avoid wrapping HttpExceptions
			throw e;
		} catch (Exception e) {
			throw new InternalServerException(e);
		}
	}

	protected Annotation getAnnotationForUpdate(String baseUrl, String provider, String identifier)
			throws ParamValidationException, AnnotationNotFoundException {
		AnnotationId annoId = new BaseAnnotationId(getConfiguration().getAnnotationBaseUrl(), provider, identifier);
		Annotation annotation = getAnnotationService().getAnnotationById(annoId);
		if (annotation == null)
			throw new ParamValidationException(ParamValidationException.MESSAGE_IDENTIFIER_WRONG,
					"/provider/identifier", annoId.toUri(), HttpStatus.NOT_FOUND, null);

		if (annotation.isDisabled())
			throw new AnnotationNotFoundException("The Annotation is known to have existed in the past and was deleted",
					annoId.toUri(), HttpStatus.GONE, null);
		return annotation;
	}

	/**
	 * @param wsKey
	 * @param provider
	 * @param identifier
	 * @param userToken
	 * @return
	 * @throws HttpException
	 */
	protected ResponseEntity<String> storeAnnotationReport(String wsKey, String provider, String identifier,
			String userToken, String action) throws HttpException {
		try {

			// SET DEFAULTS and validates apikey
			// 1.
			getAuthenticationService().getByApiKey(wsKey);

			// 2. build and verify annotation ID
			AnnotationId annoId = validateInputsForUpdateDelete(wsKey, provider, identifier, userToken);

			// 1. authorize user
			Agent user = getAuthorizationService().authorizeUser(userToken, wsKey, annoId, Operations.REPORT);

			// build vote
			Date reportDate = new Date();
			Vote vote = buildVote(user, reportDate);

			// 3. Check if the user has already reported this annotation
			ModerationRecord moderationRecord = getAnnotationService().findModerationRecordById(annoId);
			if (moderationRecord == null)
				moderationRecord = buildNewModerationRecord(annoId, reportDate);
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
				throw new OperationAuthorizationException(
						OperationAuthorizationException.MESSAGE_OPERATION_NOT_AUTHORIZED,
						"A report from the same users exists in database!" + vote.getUserId());
		}
	}

	protected ModerationRecord buildNewModerationRecord(AnnotationId annoId, Date reportDate) {
		// create moderation record
		ModerationRecord moderationRecord = new BaseModerationRecord();
		moderationRecord.setAnnotationId(annoId);

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
