package eu.europeana.annotation.web.service.controller.jsonld;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.apache.stanbol.commons.jsonld.JsonLd;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import eu.europeana.annotation.definitions.exception.AnnotationValidationException;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.definitions.model.factory.impl.AgentObjectFactory;
import eu.europeana.annotation.definitions.model.impl.BaseAnnotationId;
import eu.europeana.annotation.definitions.model.vocabulary.AgentTypes;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.jsonld.EuropeanaAnnotationLd;
import eu.europeana.annotation.solr.exceptions.AnnotationStateException;
import eu.europeana.annotation.web.exception.HttpException;
import eu.europeana.annotation.web.exception.InternalServerException;
import eu.europeana.annotation.web.exception.authorization.UserAuthorizationException;
import eu.europeana.annotation.web.exception.request.ParamValidationException;
import eu.europeana.annotation.web.exception.request.RequestBodyValidationException;
import eu.europeana.annotation.web.exception.response.AnnotationNotFoundException;
import eu.europeana.annotation.web.http.HttpHeaders;
import eu.europeana.annotation.web.service.authentication.model.Application;
import eu.europeana.annotation.web.service.controller.BaseRest;

public class BaseJsonldRest extends BaseRest{

	protected ResponseEntity<String> storeAnnotation(String wsKey, MotivationTypes motivation,  String provider, String identifier, boolean indexOnCreate,
			String annotation, String userToken) throws HttpException {
		try {

			//SET DEFAULTS 
			Application app = getAuthenticationService().getByApiKey(wsKey);
			
			if(provider == null)
				provider = app.getProvider();
			
			// 0. annotation id
			AnnotationId annoId = buildAnnotationId(provider, identifier);
			
			// 1. authorize user
			authorizeUser(userToken, wsKey, annoId);

			// parse
			Annotation webAnnotation = getAnnotationService().parseAnnotationLd(motivation, annotation);
			
			//SET DEFAULTS 
			if(webAnnotation.getSerializedBy() == null)
				webAnnotation.setSerializedBy(buildDefaultSerializedBy(app));
			
			if(webAnnotation.getAnnotatedBy() == null)
				webAnnotation.setAnnotatedBy(buildDefaultAnnotatedBy(app));
			

			// 2. validate
			// check whether annotation with the given provider and identifier
			// already exist in the database
			if (annoId.getIdentifier() != null && getAnnotationService().existsInDb(annoId))
				throw new ParamValidationException(ParamValidationException.MESSAGE_ANNOTATION_ID_EXISTS,
						"/provider/identifier", annoId.toUri());

			// 3-6 create ID and annotation + backend validation
			webAnnotation.setAnnotationId(annoId);
			
			//validate api key ... and request limit only if the request is correct (avoid useless DB requests)
			validateApiKey(wsKey);

			Annotation storedAnnotation = getAnnotationService().storeAnnotation(webAnnotation, indexOnCreate);
			
			if(getConfiguration().isIndexingEnabled())
				System.out.println("Must implement annotation indexing here");

			// serialize to jsonld
			JsonLd annotationLd = new EuropeanaAnnotationLd(storedAnnotation);
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
		} catch(AnnotationValidationException e){ //TODO: transform to checked annotation type
			throw new RequestBodyValidationException(annotation, e);
		} catch(HttpException e){
			//avoid wrapping HttpExceptions
			throw e;
		} catch (Exception e) {
			throw new InternalServerException(e);
		}
	}
	
	Agent buildDefaultSerializedBy(Application app) {
		Agent serializer = AgentObjectFactory.getInstance().createObjectInstance(AgentTypes.SOFTWARE);
		serializer.setName(app.getName());
		serializer.setHomepage(app.getHomepage());
		serializer.setOpenId(app.getOpenId());
		
		return serializer;
	}

	Agent buildDefaultAnnotatedBy(Application app) {
		Agent annotator = AgentObjectFactory.getInstance().createObjectInstance(AgentTypes.PERSON);
		
		annotator.setName(app.getAnonymousUser().getName());
		annotator.setHomepage(app.getAnonymousUser().getHomepage());
		annotator.setOpenId(app.getAnonymousUser().getOpenId());
		
		return annotator;
	}

	protected ResponseEntity<String> getAnnotationById(
			String wsKey, String provider, String identifier, String action) throws HttpException {

		try {
			
			//2. Check client access (a valid “wskey” must be provided)
			validateApiKey(wsKey);
			
			//SET DEFAULTS 
			Application app = getAuthenticationService().getByApiKey(wsKey);
			
			if(provider == null)
				provider = app.getProvider();
			
			
			//3. Retrieve an annotation based on its identifier;
			AnnotationId annoId = new BaseAnnotationId(getConfiguration().getAnnotationBaseUrl(), provider, identifier);
			Annotation annotation = getAnnotationService().getAnnotationById(annoId);
			
			
			//4. If annotation doesn’t exist respond with HTTP 404 (if provided annotation id doesn’t exists ) 
			if(annotation == null)
				throw new AnnotationNotFoundException(AnnotationNotFoundException.MESSAGE_ANNOTATION_NO_FOUND, annoId.toHttpUrl());
			//4.or 410 (if the user is not allowed to access the annotation);
			try{
				//check visibility
				getAnnotationService().checkVisibility(annotation, null);
			} catch (AnnotationStateException e) {
				if(annotation.isDisabled())
					throw new UserAuthorizationException(UserAuthorizationException.MESSAGE_ANNOTATION_STATE_NOT_ACCESSIBLE, annotation.getStatus(), HttpStatus.GONE, e);
				else
					throw new UserAuthorizationException(UserAuthorizationException.MESSAGE_USER_NOT_AUTHORIZED, wsKey, e);
			} 

			JsonLd annotationLd = new EuropeanaAnnotationLd(annotation);
			String jsonLd = annotationLd.toString(4);

			int etag;
			if(annotation.getLastUpdate() != null)
				etag = annotation.getLastUpdate().hashCode();
			else
				etag = annotation.getSerializedAt().hashCode();
				
			MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>(5);
			headers.add(HttpHeaders.VARY, HttpHeaders.ACCEPT);
			headers.add(HttpHeaders.ETAG, "" + etag);
			headers.add(HttpHeaders.LINK, HttpHeaders.VALUE_LDP_RESOURCE);
			headers.add(HttpHeaders.ALLOW, HttpHeaders.ALLOW_GPuD);

			ResponseEntity<String> response = new ResponseEntity<String>(jsonLd, headers, HttpStatus.OK);

			return response;
			
		}catch (RuntimeException e) {
			//not found .. 
			throw new InternalServerException(e);
		} catch (HttpException e) {
			//avoid wrapping http exception
			throw e;
		} catch (Exception e) {
			throw new InternalServerException(e);
		}
	}
	
	
	/**
	 * This method validates input values wsKey, identifier and userToken.
	 * @param wsKey
	 * @param identifier
	 * @param userToken
	 * @return annotation object
	 * @throws HttpException
	 */
	private void validateInputsForUpdateDelete(
			String wsKey, String provider, String identifier, String userToken) throws HttpException {
		
		// check identifier
		if (provider == null || identifier == null)
			throw new ParamValidationException(ParamValidationException.MESSAGE_IDENTIFIER_WRONG,
					"/provider/identifier", identifier, HttpStatus.NOT_FOUND, null);

		// 1. build annotation id object
		AnnotationId annoId = new BaseAnnotationId(getConfiguration().getAnnotationBaseUrl(), provider, identifier);
		
//		if (annoId == null)
//			throw new ParamValidationException(ParamValidationException.MESSAGE_IDENTIFIER_WRONG,
//					"/provider/identifier", identifier, HttpStatus.NOT_FOUND, null);

		// 2. Check client access (a valid “wskey” must be provided)
		validateApiKey(wsKey);
		
		// 3. Retrieve an annotation based on its identifier;
		//Annotation annotation = getAnnotationService().getAnnotationById(annoId);		
		
		// 4. If annotation doesn’t exist respond with HTTP 404 (if provided annotation id doesn’t exists ) 
		//	throw new AnnotationNotFoundException(AnnotationNotFoundException.MESSAGE_ANNOTATION_NO_FOUND, annoId.toUri());
		
		// check whether annotation with the given provider and identifier
		// already exist in the database
//		if (annoId.getIdentifier() != null && !getAnnotationService().existsInDb(annoId))
//			throw new ParamValidationException(ParamValidationException.MESSAGE_ANNOTATION_ID_NOT_EXISTS,
//					"/provider/identifier", annoId.toUri());
	
		// 5. authorize user
		authorizeUser(userToken, wsKey, annoId);
	
		//return annotation;
	}

	
	/**
	 * This method validates input values, retrieves annotation object and
	 * updates it.
	 * @param wsKey
	 * @param identifier
	 * @param annotation
	 * @param userToken
	 * @param action
	 * @return response entity that comprises response body, headers and status code
	 * @throws HttpException
	 */
	protected ResponseEntity<String> updateAnnotation(String wsKey, String provider, String identifier, 
			String annotation, String userToken, String action) throws HttpException {

		try {
			
			//SET DEFAULTS 
			Application app = getAuthenticationService().getByApiKey(wsKey);
						
			validateInputsForUpdateDelete(wsKey, provider, identifier, userToken);
			
			// Retrieve an annotation based on its identifier;
			Annotation storedAnnotation = getAnnotationForUpdate(getConfiguration().getAnnotationBaseUrl(), provider, identifier);
						
			// 6. extract and check current annotation type and motivation
			MotivationTypes currentMotivation = storedAnnotation.getMotivationType();
			
			//TODO: the motivation should be verified during parsing. If motivation is provided within the annotation object it must match the provided one
			Annotation updateWebAnnotation = getAnnotationService().parseAnnotationLd(
					currentMotivation, annotation);
			
//			// 7. parse updated annotation
//			if (updatedMotivation != null)
//				currentMotivation = updatedMotivation;
//			
			// 8. apply updates - merge current and updated annotation
			updateValues(storedAnnotation, updateWebAnnotation);
						
			// 9. call database update method
			Annotation updatedAnnotation = getAnnotationService().updateAnnotation(storedAnnotation);

			// serialize to jsonld
			JsonLd annotationLd = new EuropeanaAnnotationLd(updatedAnnotation);
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
		} catch(AnnotationValidationException e){ //TODO: transform to checked annotation type
			throw new RequestBodyValidationException(annotation, e);
		} catch(HttpException e){
			//avoid wrapping HttpExceptions
			throw e;
		} catch (Exception e) {
			throw new InternalServerException(e);
		}
	}


	/**
	 * This method updates existing annotation by replacing of the old values with a
	 * new values.
	 * @param storedAnnotation
	 * @param updatedWebAnnotation
	 */
	private void updateValues(Annotation storedAnnotation, Annotation updatedWebAnnotation) {
		
		if (updatedWebAnnotation.getType() != null) 
			storedAnnotation.setType(updatedWebAnnotation.getType());

		//Motivation must not be changed at least for now
		if(updatedWebAnnotation.getMotivationType() != null && updatedWebAnnotation.getMotivationType() != storedAnnotation.getMotivationType())
			throw new RuntimeException("Cannot change motivation type from: " + storedAnnotation.getMotivationType() 
			+ " to: " + updatedWebAnnotation.getMotivationType());
//		if (updatedWebAnnotation.getMotivation() != null) 
//			currentWebAnnotation.setMotivation(updatedWebAnnotation.getMotivation());
		if (updatedWebAnnotation.getAnnotatedAt() != null) 
			storedAnnotation.setAnnotatedAt(updatedWebAnnotation.getAnnotatedAt());
		if (updatedWebAnnotation.getAnnotatedBy() != null) 
			storedAnnotation.setAnnotatedBy(updatedWebAnnotation.getAnnotatedBy());
		if (updatedWebAnnotation.getSerializedAt() != null) 
			storedAnnotation.setSerializedAt(updatedWebAnnotation.getSerializedAt());
		if (updatedWebAnnotation.getSerializedBy() != null) 
			storedAnnotation.setSerializedBy(updatedWebAnnotation.getAnnotatedBy());
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
	 * @param wsKey
	 * @param identifier
	 * @param userToken
	 * @param action
	 * @return response entity that comprises response body, headers and status code
	 * @throws HttpException
	 */
	protected ResponseEntity<String> deleteAnnotation(String wsKey, String provider, String identifier, 
			String userToken, String action) throws HttpException {
		
		try {
			//SET DEFAULTS 
			Application app = getAuthenticationService().getByApiKey(wsKey);
			
			if(provider == null)
				provider = app.getProvider();
			
			validateInputsForUpdateDelete(wsKey, provider, identifier, userToken);
			
			// Retrieve an annotation based on its id;
			Annotation annotation = getAnnotationForUpdate(getConfiguration().getAnnotationBaseUrl(), provider, identifier);
				
			//TODO: Verify if user is allowed to perform the deletion.
			
			// call database delete method that deactivates existing Annotation in Mongo
			getAnnotationService().disableAnnotation(annotation);

			MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>(5);
			headers.add(HttpHeaders.VARY, HttpHeaders.ACCEPT);

			ResponseEntity<String> response = new ResponseEntity<String>(null, headers, HttpStatus.NO_CONTENT);

			return response;

		} catch(HttpException e){
			//avoid wrapping HttpExceptions
			throw e;
		} catch (Exception e) {
			throw new InternalServerException(e);
		}
	}

	protected Annotation getAnnotationForUpdate(String baseUrl, String provider, String identifier)
			throws ParamValidationException, AnnotationNotFoundException {
		AnnotationId annoId = new BaseAnnotationId(getConfiguration().getAnnotationBaseUrl(), provider, identifier);
		Annotation annotation = getAnnotationService().getAnnotationById(annoId);
		if(annotation == null) 
			throw new ParamValidationException(ParamValidationException.MESSAGE_IDENTIFIER_WRONG,
				"/provider/identifier", annoId.toUri(), HttpStatus.NOT_FOUND, null);
		
		if(annotation.isDisabled())
			throw new AnnotationNotFoundException("The Annotation is known to have existed in the past and was deleted", annoId.toUri(), HttpStatus.GONE, null);
		return annotation;
	}

	
}
