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
	 * This method validates provider.
	 * @param annotation
	 * @param provider
	 * @return
	 */
	public boolean validateProvider(Annotation annotation, String provider) {
		boolean res = true;
		if (StringUtils.isNotEmpty(annotation.getSameAs())
				&& annotation.getSameAs().contains(WebAnnotationFields.PROVIDER_HISTORY_PIN)
				&& StringUtils.isNotEmpty(provider) 
				&& !provider.equals(WebAnnotationFields.PROVIDER_HISTORY_PIN)
				)
			res = false;
		return res;
	}

	/**
	 * This method validates europeana provider.
	 * @param annotation
	 * @param provider
	 * @return
	 */
	public boolean validateEuropeanaProvider(Annotation annotation, String provider) {
		boolean res = true;
		if (StringUtils.isNotEmpty(annotation.getEquivalentTo())
				&& annotation.getEquivalentTo().contains(WebAnnotationFields.PROVIDER_HISTORY_PIN)
				&& StringUtils.isNotEmpty(provider) 
				&& !provider.equals(WebAnnotationFields.PROVIDER_HISTORY_PIN)
				)
			res = false;
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
			String provider, String sameAs) {
		
		AnnotationId annotationId = new BaseAnnotationId();
		annotationId.setResourceId(createResourceId(collection, object));
		
		if (StringUtils.isEmpty(sameAs)
				|| StringUtils.isEmpty(provider)) { 
			annotationId.setProvider(WebAnnotationFields.PROVIDER_WEBANNO);
		}else if (!StringUtils.isEmpty(sameAs)){
			processExternalId(annotationId, provider, sameAs);
		}
		
		return annotationId;
		
	}
	
    private void processExternalId(AnnotationId annotationId, String provider, String sameAs) {
    	if(WebAnnotationFields.PROVIDER_HISTORY_PIN.equals(provider) && sameAs.contains(WebAnnotationFields.PROVIDER_HISTORY_PIN)){
    		String[] parts = sameAs.split(WebAnnotationFields.SLASH);
    		annotationId.setProvider(provider);
    		int annotationNr = Integer.parseInt(parts[parts.length -1]);
			annotationId.setAnnotationNr(annotationNr);
    	}
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
