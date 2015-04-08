package eu.europeana.annotation.definitions.model.utils;

import org.apache.commons.lang.StringUtils;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.impl.BaseAnnotationId;
import eu.europeana.annotation.definitions.model.target.impl.BaseTarget;

public class AnnotationIdHelper {

	/**
	 * This method extracts resourceId from passed httpUri.
	 * @param httpUri
	 * @return resourceId
	 */
	public String extractResoureIdFromHttpUri(String httpUri) {
		String res = "";
		if (StringUtils.isNotEmpty(httpUri)) {
	        String[] arrValue = httpUri.split(WebAnnotationFields.SLASH);
        	//computed from the end of the url
        	int collectionPosition = arrValue.length - 2;
        	int objectPosition = arrValue.length - 1;			
        	
			String collection = arrValue[collectionPosition];
        	String object     = arrValue[objectPosition].replace(".html", "");
			if (StringUtils.isNotEmpty(collection) && StringUtils.isNotEmpty(object))
				res = WebAnnotationFields.SLASH + collection
						+ WebAnnotationFields.SLASH + object;
		}
		return res;
	}
		
	
	/**
     * Extract resourceId from target.source.httpUri if source exists
	 * otherwise from target.httpUri.
	 * @param newAnnotation
	 * @return resourceId string
	 */
	public String extractResourceId(Annotation newAnnotation) {
		String resourceId = "";
		if (((BaseTarget) newAnnotation.getTarget()).getSource() != null) {
			resourceId = extractResoureIdFromHttpUri(
					((BaseTarget) newAnnotation.getTarget()).getSource().getHttpUri());
		} else {
			resourceId = extractResoureIdFromHttpUri(
					newAnnotation.getTarget().getHttpUri());
		}
		return resourceId;
	}
		
	/**
     * Extract collection from resourceId.
	 * @param resourceId
	 * @return collection
	 */
	public String extractCollectionFromResourceId(String resourceId) {
		String res = "";
		int COLLECTION_CHUNK_POS = 1;
		if (StringUtils.isNotEmpty(resourceId)) {
			String[] arr = resourceId.split("/");
			res = arr[COLLECTION_CHUNK_POS];
		}
		return res;
	}
		
	/**
     * Extract object from resourceId.
	 * @param resourceId
	 * @return object
	 */
	public String extractObjectFromResourceId(String resourceId) {
		String res = "";
		int OBJECT_CHUNK_POS = 2;
		if (StringUtils.isNotEmpty(resourceId)) {
			String[] arr = resourceId.split("/");
			res = arr[OBJECT_CHUNK_POS];
		}
		return res;
	}
		
	/**
	 * This method validates ResourceId.
	 * @param annotation
	 * @param collection
	 * @param object
	 * @return
	 */
	public boolean validateResouceId(Annotation annotation, String collection, String object) {
		boolean res = false;
		String objectResourceId = extractResourceId(annotation);
		if (objectResourceId.equals(WebAnnotationFields.SLASH + collection + WebAnnotationFields.SLASH + object))
			res = true;
		return res;
	}

	/**
	 * This method initializes AnnotationId object by passed
	 * collection, object and provider.
	 * @param collection
	 * @param object
	 * @param provider
	 * @return AnnotationId object
	 */
	public AnnotationId initializeAnnotationId(String collection, String object,
			String provider) {
		
		String resourceId = "";
		if (!StringUtils.isEmpty(collection) && !StringUtils.isEmpty(object)) 
			resourceId = createResourceId(collection, object);
		return initializeAnnotationId(resourceId, provider);
	}
	
	/**
	 * This method initializes AnnotationId object by passed
	 * resourceId and provider. 
	 * @param resourceId
	 * @param provider
	 * @return AnnotationId object
	 */
	public AnnotationId initializeAnnotationId(String resourceId, String provider) {
		
		AnnotationId annotationId = new BaseAnnotationId();
		if (StringUtils.isEmpty(provider)) 
			provider = WebAnnotationFields.PROVIDER_WEBANNO;
		annotationId.setProvider(provider);
		if (!StringUtils.isEmpty(resourceId)) 
			annotationId.setResourceId(resourceId);
		
		return annotationId;
	}
	
    /**
     * This method creates ResourceId string from collection and object.
     * @param collection
     * @param object
     * @return ResourceId
     */
    public String createResourceId(String collection, String object) {
    	String res = "";
		if (StringUtils.isNotEmpty(collection) && StringUtils.isNotEmpty(object))
			res = WebAnnotationFields.SLASH + collection + WebAnnotationFields.SLASH + object;
    	return res;
    }
	
}
