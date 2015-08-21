package eu.europeana.annotation.web.service.controller.jsonld;

import org.apache.commons.lang3.StringUtils;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.apache.stanbol.commons.jsonld.JsonLd;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import eu.europeana.annotation.definitions.exception.AnnotationValidationException;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.impl.BaseAnnotationId;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.jsonld.EuropeanaAnnotationLd;
import eu.europeana.annotation.solr.exceptions.AnnotationStateException;
import eu.europeana.annotation.utils.JsonUtils;
import eu.europeana.annotation.web.exception.HttpException;
import eu.europeana.annotation.web.exception.InternalServerException;
import eu.europeana.annotation.web.exception.authorization.UserAuthorizationException;
import eu.europeana.annotation.web.exception.request.ParamValidationException;
import eu.europeana.annotation.web.exception.request.RequestBodyValidationException;
import eu.europeana.annotation.web.exception.response.AnnotationNotFoundException;
import eu.europeana.annotation.web.http.HttpHeaders;
import eu.europeana.annotation.web.service.controller.BaseRest;

public class BaseJsonldRest extends BaseRest{

	protected ResponseEntity<String> storeAnnotation(String wsKey, MotivationTypes motivation,  String provider, String identifier, boolean indexOnCreate,
			String annotation, String userToken) throws HttpException {
		try {

			// 0. annotation id
			AnnotationId annoId = buildAnnotationId(provider, identifier);
			
			// 1. authorize user
			authorizeUser(userToken, annoId);

			// parse
			Annotation webAnnotation = getAnnotationService().parseAnnotationLd(motivation, annotation);

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
			headers.add(HttpHeaders.LINK, HttpHeaders.VALUE_LD_RESOURCE);

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
	
	void validateApiKey(String wsKey) throws UserAuthorizationException {
		getAnnotationService().validateApiKey(wsKey);
	}

	protected ResponseEntity<String> getAnnotationById(String wsKey, String provider, String identifier, String action) throws HttpException {

		try {
			
			//2. Check client access (a valid “wskey” must be provided)
			validateApiKey(wsKey);
			
			//3. Retrieve an annotation based on its identifier;
			AnnotationId annoId = new BaseAnnotationId(provider, identifier);
			Annotation annotation = getAnnotationService().getAnnotationById(annoId);
			
			
			//4. If annotation doesn’t exist respond with HTTP 404 (if provided annotation id doesn’t exists ) 
			if(annotation == null)
				throw new AnnotationNotFoundException(AnnotationNotFoundException.MESSAGE_ANNOTATION_NO_FOUND, annoId.toUri());
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
			headers.add(HttpHeaders.LINK, HttpHeaders.VALUE_LD_RESOURCE);
			//headers.add(HttpHeaders.ALLOW, "PUT,GET,DELETE,OPTIONS,HEAD,PATCH");
			headers.add(HttpHeaders.ALLOW, "PUT,GET,DELETE,OPTIONS,HEAD,PATCH");

			ResponseEntity<String> response = new ResponseEntity<String>(jsonLd, headers, HttpStatus.CREATED);

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
	
	
	protected ResponseEntity<String> updateAnnotation(String wsKey, String identifier, 
			String annotation, String userToken, String action) throws HttpException {
		try {
			
			// 1. build annotation id object
			AnnotationId annoId = buildAnnotationId(identifier);
			
			// 2. Retrieve an annotation based on its identifier;
			Annotation currentWebAnnotation = getAnnotationService().getAnnotationById(annoId);		
			
			// 3. If annotation doesn’t exist respond with HTTP 404 (if provided annotation id doesn’t exists ) 
			if (currentWebAnnotation == null)
				throw new AnnotationNotFoundException(AnnotationNotFoundException.MESSAGE_ANNOTATION_NO_FOUND, annoId.toUri());
						
			// check whether annotation with the given provider and identifier
			// already exist in the database
			if (annoId.getIdentifier() != null && !getAnnotationService().existsInDb(annoId))
				throw new ParamValidationException(ParamValidationException.MESSAGE_ANNOTATION_ID_NOT_EXISTS,
						"/provider/identifier", annoId.toUri());

			// 4. authorize user
			authorizeUser(userToken, annoId);
		
			// 5. extract and check current annotation type and motivation
			MotivationTypes currentMotivation = null;
			currentMotivation = MotivationTypes.getType(currentWebAnnotation.getMotivation());
			
			// 6. extract and check updated annotation type and motivation
			String updatedAnnotationType = JsonUtils.extractValueFromJsonString(
					WebAnnotationFields.AT_TYPE, annotation);
			MotivationTypes updatedMotivation = null;
			if (StringUtils.isNotEmpty(updatedAnnotationType)) {
				String updatedMotivationStr = JsonUtils.extractValueFromJsonString(
						WebAnnotationFields.MOTIVATION, annotation);
				if(updatedMotivationStr == null)
					throw new ParamValidationException(ParamValidationException.MESSAGE_INVALID_PARAMETER_VALUE, 
							WebAnnotationFields.PATH_PARAM_ANNO_TYPE, updatedAnnotationType, HttpStatus.NOT_ACCEPTABLE, null);
				else
					updatedMotivation = MotivationTypes.getType(updatedMotivationStr);
			}
			
			// 7. parse updated annotation
			if (updatedMotivation != null)
				currentMotivation = updatedMotivation;
			Annotation updatedWebAnnotation = getAnnotationService().parseAnnotationLd(
					currentMotivation, annotation);

			// 8. apply updates - merge current and updated annotation
			updateValues(currentWebAnnotation, updatedWebAnnotation);
						
			// validate api key ... and request limit only if the request is correct (avoid useless DB requests)
			validateApiKey(wsKey);

			// 9. call database update method
			Annotation updatedAnnotation = getAnnotationService().updateAnnotation(currentWebAnnotation);

			// serialize to jsonld
			JsonLd annotationLd = new EuropeanaAnnotationLd(updatedAnnotation);
			String jsonLd = annotationLd.toString(4);

			// build response entity with headers
			// TODO: clarify serialization ETag: "_87e52ce126126"
			// TODO: clarify Allow: PUT,GET,DELETE,OPTIONS,HEAD,PATCH

			MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>(5);
			headers.add(HttpHeaders.VARY, HttpHeaders.ACCEPT);
			headers.add(HttpHeaders.ETAG, "" + updatedAnnotation.getLastUpdate().hashCode());
			headers.add(HttpHeaders.LINK, HttpHeaders.VALUE_LD_RESOURCE);

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
	 * @param currentWebAnnotation
	 * @param updatedWebAnnotation
	 */
	private void updateValues(Annotation currentWebAnnotation, Annotation updatedWebAnnotation) {
		if (updatedWebAnnotation.getType() != null &&
				!currentWebAnnotation.getType().equals(updatedWebAnnotation.getType())) 
			currentWebAnnotation.setType(updatedWebAnnotation.getType());
		if (updatedWebAnnotation.getMotivation() != null &&
				!currentWebAnnotation.getMotivation().equals(updatedWebAnnotation.getMotivation())) 
			currentWebAnnotation.setMotivation(updatedWebAnnotation.getMotivation());
		if (updatedWebAnnotation.getAnnotatedAt() != null &&
				!currentWebAnnotation.getAnnotatedAt().equals(updatedWebAnnotation.getAnnotatedAt())) 
			currentWebAnnotation.setAnnotatedAt(updatedWebAnnotation.getAnnotatedAt());
		if (updatedWebAnnotation.getAnnotatedBy() != null &&
				!currentWebAnnotation.getAnnotatedBy().equals(updatedWebAnnotation.getAnnotatedBy())) 
			currentWebAnnotation.setAnnotatedBy(updatedWebAnnotation.getAnnotatedBy());
		if (updatedWebAnnotation.getSerializedAt() != null &&
				!currentWebAnnotation.getSerializedAt().equals(updatedWebAnnotation.getSerializedAt())) 
			currentWebAnnotation.setSerializedAt(updatedWebAnnotation.getSerializedAt());
		if (updatedWebAnnotation.getSerializedBy() != null &&
				!currentWebAnnotation.getSerializedBy().equals(updatedWebAnnotation.getSerializedBy())) 
			currentWebAnnotation.setSerializedBy(updatedWebAnnotation.getAnnotatedBy());
		if (updatedWebAnnotation.getBody() != null &&
				!currentWebAnnotation.getBody().equals(updatedWebAnnotation.getBody())) 
			currentWebAnnotation.setBody(updatedWebAnnotation.getBody());
		if (updatedWebAnnotation.getTarget() != null &&
				!currentWebAnnotation.getTarget().equals(updatedWebAnnotation.getTarget())) 
			currentWebAnnotation.setTarget(updatedWebAnnotation.getTarget());
		if (currentWebAnnotation.isDisabled() != updatedWebAnnotation.isDisabled()) 
			currentWebAnnotation.setDisabled(updatedWebAnnotation.isDisabled());
		if (updatedWebAnnotation.getEquivalentTo() != null &&
				!currentWebAnnotation.getEquivalentTo().equals(updatedWebAnnotation.getEquivalentTo())) 
			currentWebAnnotation.setEquivalentTo(updatedWebAnnotation.getEquivalentTo());
		if (updatedWebAnnotation.getInternalType() != null &&
				!currentWebAnnotation.getInternalType().equals(updatedWebAnnotation.getInternalType())) 
			currentWebAnnotation.setInternalType(updatedWebAnnotation.getInternalType());
		if (updatedWebAnnotation.getLastUpdate() != null &&
				!currentWebAnnotation.getLastUpdate().equals(updatedWebAnnotation.getLastUpdate())) 
			currentWebAnnotation.setLastUpdate(updatedWebAnnotation.getLastUpdate());
		if (updatedWebAnnotation.getSameAs() != null &&
				!currentWebAnnotation.getSameAs().equals(updatedWebAnnotation.getSameAs())) 
			currentWebAnnotation.setSameAs(updatedWebAnnotation.getSameAs());
		if (updatedWebAnnotation.getStatus() != null &&
				!currentWebAnnotation.getStatus().equals(updatedWebAnnotation.getStatus())) 
			currentWebAnnotation.setStatus(updatedWebAnnotation.getStatus());
		if (updatedWebAnnotation.getStyledBy() != null &&
				!currentWebAnnotation.getStyledBy().equals(updatedWebAnnotation.getStyledBy())) 
			currentWebAnnotation.setStyledBy(updatedWebAnnotation.getStyledBy());
	}
	
	
	protected ResponseEntity<String> deleteAnnotation(String wsKey, String identifier, 
			String userToken, String action) throws HttpException {
		try {
			
			// 1. build annotation id object
			AnnotationId annoId = buildAnnotationId(identifier);
			
			// 2. Check client access (a valid “wskey” must be provided)
			validateApiKey(wsKey);
			
			// 3. Retrieve an annotation based on its identifier;
			Annotation annotation = getAnnotationService().getAnnotationById(annoId);		
			
			// 4. If annotation doesn’t exist respond with HTTP 404 (if provided annotation id doesn’t exists ) 
			if(annotation == null)
				throw new AnnotationNotFoundException(AnnotationNotFoundException.MESSAGE_ANNOTATION_NO_FOUND, annoId.toUri());
			
			// check whether annotation with the given provider and identifier
			// already exist in the database
			if (annoId.getIdentifier() != null && !getAnnotationService().existsInDb(annoId))
				throw new ParamValidationException(ParamValidationException.MESSAGE_ANNOTATION_ID_NOT_EXISTS,
						"/provider/identifier", annoId.toUri());

			// 5. authorize user
			authorizeUser(userToken, annoId);
	
			// validate api key ... and request limit only if the request is correct (avoid useless DB requests)
			validateApiKey(wsKey);

			// 6. call database delete method that deactivates existing Annotation in Mongo
			getAnnotationService().disableAnnotation(annotation);
//					annoId.getProvider(), annoId.getIdentifier());

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

	
}
