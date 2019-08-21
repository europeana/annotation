package eu.europeana.annotation.definitions.model.utils;

import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.impl.BaseAnnotationId;
import eu.europeana.annotation.definitions.model.resource.InternetResource;
import eu.europeana.annotation.definitions.model.target.impl.BaseTarget;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;

public class AnnotationIdHelper {

	/**
	 * This method extracts resourceId from passed httpUri.
	 * use the {@link #extractResoureIdPartsFromHttpUri(String)} method
	 * @param httpUri
	 * @return resourceId
	 */
	@Deprecated
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
	
	
	public String buildResourseId(String[] parts){
//		if (StringUtils.isNotEmpty(collection) && StringUtils.isNotEmpty(object))
//			res = WebAnnotationFields.SLASH + collection
//					+ WebAnnotationFields.SLASH + object;
		if(parts == null || parts.length < 2)
			return null;
		
		return WebAnnotationFields.SLASH + parts[0]
				+ WebAnnotationFields.SLASH + parts[1];
	}
	
	public String[] extractResoureIdPartsFromHttpUri(String httpUri) {
		String res[] = new String[2];
		if (StringUtils.isNotEmpty(httpUri)) {
	        String[] arrValue = httpUri.split(WebAnnotationFields.SLASH);
	        
	        //"http://data.europeana.eu/item/123/xyz"
	        //TODO: clarify resource identification policies
	        //must have at least a domain and type/collection and id in order to have a valid uri
	        if(!(arrValue.length > 2) )
	        	return null;
        	//computed from the end of the url
        	int collectionPosition = arrValue.length - 2;
        	int objectPosition = arrValue.length - 1;			
        	
			String collection = arrValue[collectionPosition];
        	String object     = arrValue[objectPosition].replace(".html", "");
        	res[0] = collection;
        	res[1] = object;
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
		if (((BaseTarget) newAnnotation.getTarget()).getSourceResource() != null) {
			resourceId = extractResoureIdFromHttpUri(
					((BaseTarget) newAnnotation.getTarget()).getSourceResource().getHttpUri());
		} else {
			resourceId = extractResoureIdFromHttpUri(
					newAnnotation.getTarget().getHttpUri());
		}
		return resourceId;
	}
		
	/**
     * Extracts provider name from the given identifier URL.
	 * @param uri The URL
	 * @return provider
	 */
	public String extractProviderFromUri(String uri) {
		int PROVIDER_TOKEN_POS = 2;
		return extractTokenFromUrl(uri, PROVIDER_TOKEN_POS);
	}
		
	/**
     * Extracts id from the given identifier URL.
	 * @param uri The URL
	 * @return id
	 */
	public String extractIdFromUri(String uri) {
		int ID_TOKEN_POS = 3;
		return extractTokenFromUrl(uri, ID_TOKEN_POS);
	}
	
			
	/**
     * Extracts token from the given URL.
	 * @param url The input URL
	 * @param pos The position of the token from the end
	 * @return token
	 */
	public String extractTokenFromUrl(String url, int tokenPos) {
		String res = "";
		int stringArrayPos;
		if (StringUtils.isNotEmpty(url)) {
			String[] arr = url.split("/");
			stringArrayPos = (arr.length - 1) - (3 - tokenPos);
			res = arr[stringArrayPos];
		}
		return res;
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
	 * This method automatically extracts and inserts the resourceIDs from field 'value' or if it is empty 
	 * from 'values' in InternetResource like Target or Body. 
	 * @param ir input InternetResource
	 * @return output InternetResource
	 */
	public InternetResource setResourceIds(InternetResource ir) {
		InternetResource res = ir;
		if (ir != null) {
			if (StringUtils.isNotEmpty(ir.getValue())) {
				String resourceId = extractResourceIdFromUri(ir.getValue());
				ir.setResourceId(resourceId);
			}
			if (ir.getValues() != null && ir.getValues().size() > 0) {
				Iterator<String> itr = ir.getValues().iterator();
				while (itr.hasNext()) {
					String value = itr.next();
					String resourceId = extractResourceIdFromUri(value);
					ir.addResourceId(resourceId);
				}
			}
			res = ir;
		}
		return res;
	}

	/**
	 * If given URI is an annotation URI extract resource ID.
	 * @param uri
	 * @return resource ID 
	 */
	private String extractResourceIdFromUri(String uri) {
		String resourceId = null;
		if (StringUtils.isNotEmpty(uri) 
				&& uri.contains(WebAnnotationFields.HTTP))
//			&& uri.contains(WebAnnotationFields.ANNOTATION_ID_PREFIX))
			resourceId = buildResourseId(
				extractResoureIdPartsFromHttpUri(uri));
		return resourceId;
	}
	
	/**
	 * This method validates europeana provider.
	 * @param annotation
	 * @param provider
	 * @return
	 */
//	public boolean validateEuropeanaProvider(Annotation annotation, String provider) {
//		boolean res = true;
//		if (StringUtils.isNotEmpty(annotation.getEquivalentTo())
//				&& annotation.getEquivalentTo().contains(WebAnnotationFields.PROVIDER_HISTORY_PIN)
//				&& StringUtils.isNotEmpty(provider) 
//				&& !provider.equals(WebAnnotationFields.PROVIDER_HISTORY_PIN)
//				)
//			res = false;
//		return res;
//	}

	/**
	 * This method initializes AnnotationId object by passed identifier 
	 * when identifier is an URL and contains provider.
	 * e.g. identifier 'http://data.europeana.eu/annotaion/base/1'
	 * @param uri
	 * @return AnnotationId object
	 */
	public AnnotationId parseAnnotationId(
			String uri) {
		
		return parseAnnotationId(uri, false);
		
	}
	
	public AnnotationId parseAnnotationId(
			String uri, boolean includeBaseUrl) {
		
		AnnotationId annotationId = new BaseAnnotationId();
		String provider = extractProviderFromUri(uri);
		String id = extractIdFromUri(uri);
		annotationId.setProvider(provider);
		annotationId.setIdentifier(id);

		if(includeBaseUrl){
			String baseUrl = StringUtils.remove(uri, annotationId.toRelativeUri());
			annotationId.setBaseUrl(baseUrl); 
		}
		
		
		return annotationId;
		
	}
	
//    private void processExternalId(AnnotationId annotationId, String provider, String sameAs) {
//    	if(WebAnnotationFields.PROVIDER_HISTORY_PIN.equals(provider) && sameAs.contains(WebAnnotationFields.PROVIDER_HISTORY_PIN)){
//    		String[] parts = sameAs.split(WebAnnotationFields.SLASH);
//    		annotationId.setProvider(provider);
//    		Long annotationNr = Long.parseLong(parts[parts.length -1]);
//			annotationId.setAnnotationNr(annotationNr);
//    	}
//	}


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
    
    /**
     * Get ID part form URI string
     * @param uriStr URI string
     * @return ID part
     */
    public String getIdPartFromUri(String uriStr) {
    	return uriStr.substring(uriStr.lastIndexOf('/')+1, uriStr.length());
    }
    

    /**
     * Create annotation ID based on via URL
     * @param baseUrl Base URL for annotation id
     * @param provider Provider
     * @param via via URL
     * @return new annotation ID
     */
    public BaseAnnotationId getAnnotationIdBasedOnVia(String baseUrl, String provider, String via) {
    	return new BaseAnnotationId(baseUrl, provider, getIdPartFromUri(via));
    }
    
	
}
