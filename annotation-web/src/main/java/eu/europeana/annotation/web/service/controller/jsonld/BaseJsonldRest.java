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
import eu.europeana.annotation.definitions.model.impl.BaseAnnotationId;
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
		if(!"apidemo".equals(wsKey))
			throw new UserAuthorizationException(UserAuthorizationException.MESSAGE_USER_NOT_AUTHORIZED, wsKey);
		
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
}
