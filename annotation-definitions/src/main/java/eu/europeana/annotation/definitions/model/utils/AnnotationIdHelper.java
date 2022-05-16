package eu.europeana.annotation.definitions.model.utils;

import org.apache.commons.lang3.StringUtils;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.target.impl.BaseTarget;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;

public class AnnotationIdHelper {
  
    /**
     * This method extracts resourceId from passed httpUri. 
     * 
     * @param httpUri
     * @return resourceId
     */
    public static String extractResourceIdFromHttpUri(String httpUri) {
	//extract resource id only from europeana resources
	//TODO: make it configurable
	if(!httpUri.contains("europeana.eu/"))
	    return null;
	String[] resourceParts = extractResourceIdPartsFromHttpUri(httpUri);
	return buildResourceId(resourceParts);
    }

    static String buildResourceId(String[] parts) {
	if (parts == null || parts.length < 2)
	    return null;

	return WebAnnotationFields.SLASH + parts[0] + WebAnnotationFields.SLASH + parts[1];
    }

    static String[] extractResourceIdPartsFromHttpUri(String httpUri) {
	String res[] = new String[2];
	if (StringUtils.isNotEmpty(httpUri)) {
	    String[] arrValue = httpUri.split(WebAnnotationFields.SLASH);

	    // "http://data.europeana.eu/item/123/xyz"
	    // TODO: clarify resource identification policies
	    // must have at least a domain and type/collection and id in order to have a
	    // valid uri
	    if (!(arrValue.length > 2))
		return null;
	    // computed from the end of the url
	    int collectionPosition = arrValue.length - 2;
	    int objectPosition = arrValue.length - 1;

	    String collection = arrValue[collectionPosition];
	    String object = arrValue[objectPosition].replace(".html", "");
	    res[0] = collection;
	    res[1] = object;
	}
	return res;
    }

    /**
     * Extract resourceId from target.source.httpUri if source exists otherwise from
     * target.httpUri.
     * 
     * @param newAnnotation
     * @return resourceId string
     */
    public static String extractResourceId(Annotation newAnnotation) {
	String resourceId = "";
	if (((BaseTarget) newAnnotation.getTarget()).getSourceResource() != null) {
	    resourceId = extractResourceIdFromHttpUri(
		    ((BaseTarget) newAnnotation.getTarget()).getSourceResource().getHttpUri());
	} else {
	    resourceId = extractResourceIdFromHttpUri(newAnnotation.getTarget().getHttpUri());
	}
	return resourceId;
    }

    /**
     * Extract collection from resourceId.
     * use {@link #extractResourceIdPartsFromHttpUri(String)}
     * @param resourceId
     * @return collection
     */
    @Deprecated
    public static String extractCollectionFromResourceId(String resourceId) {
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
     * use {@link #extractResourceIdPartsFromHttpUri(String)}
     * @param resourceId
     * @return object
     */
    @Deprecated
    public static String extractObjectFromResourceId(String resourceId) {
	String res = "";
	int OBJECT_CHUNK_POS = 2;
	if (StringUtils.isNotEmpty(resourceId)) {
	    String[] arr = resourceId.split("/");
	    res = arr[OBJECT_CHUNK_POS];
	}
	return res;
    }

    /**
     * Get ID part form URI string
     * 
     * @param uriStr URI string
     * @return ID part
     */
    public static String getIdPartFromUri(String uriStr) {
	return uriStr.substring(uriStr.lastIndexOf('/') + 1, uriStr.length());
    }

    public static String buildCreatorUri(String userEndpointBaseUrl, String userId) {
      return userEndpointBaseUrl + "/" + userId;
    }

    public static String buildGeneratorUri(String clientEndpointBaseUrl, String apikeyId) {
      return clientEndpointBaseUrl + "/" + apikeyId;
    }
    
    public static String buildAnnotationUri(String annoBaseUrl, long annoIdentifier) {
      return annoBaseUrl + "/" + annoIdentifier;
    }
    
}