package eu.europeana.annotation.definitions.model.utils;

import org.apache.commons.lang3.StringUtils;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.impl.BaseAnnotationId;
import eu.europeana.annotation.definitions.model.target.impl.BaseTarget;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;

public class AnnotationIdHelper {

    /**
     * This method extracts resourceId from passed httpUri. 
     * 
     * @param httpUri
     * @return resourceId
     */
    public String extractResourceIdFromHttpUri(String httpUri) {
	//extract resource id only from europeana resources
	//TODO: make it configurable
	if(!httpUri.contains("europeana.eu/"))
	    return null;
	String[] resourceParts = extractResourceIdPartsFromHttpUri(httpUri);
	return buildResourceId(resourceParts);
    }

    String buildResourceId(String[] parts) {
	if (parts == null || parts.length < 2)
	    return null;

	return WebAnnotationFields.SLASH + parts[0] + WebAnnotationFields.SLASH + parts[1];
    }

    String[] extractResourceIdPartsFromHttpUri(String httpUri) {
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
    public String extractResourceId(Annotation newAnnotation) {
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
     * use {@link #extractResourceIdPartsFromHttpUri(String)}
     * @param resourceId
     * @return object
     */
    @Deprecated
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
     * This method initializes AnnotationId object by passed identifier when
     * identifier is an URL and contains provider. e.g. identifier
     * 'http://data.europeana.eu/annotation/1'
     * 
     * @param uri
     * @return AnnotationId object
     */
    public AnnotationId parseAnnotationId(String uri) {

	if(uri == null)
	    return null;
	
	AnnotationId annotationId = new BaseAnnotationId();
	String id = StringUtils.substringAfterLast(uri, WebAnnotationFields.SLASH);
	annotationId.setIdentifier(id);

	String baseUrl = StringUtils.substringBeforeLast(uri, WebAnnotationFields.SLASH);
	annotationId.setBaseUrl(baseUrl);

	//force generation of httpUrl
	annotationId.getHttpUrl();
	return annotationId;
    }

    /**
     * Get ID part form URI string
     * 
     * @param uriStr URI string
     * @return ID part
     */
    public String getIdPartFromUri(String uriStr) {
	return uriStr.substring(uriStr.lastIndexOf('/') + 1, uriStr.length());
    }

    /**
     * Create annotation ID based on via URL
     * 
     * @param baseUrl Base URL for annotation id
     * @param via     via URL
     * @return new annotation ID
     */
    public BaseAnnotationId getAnnotationIdBasedOnVia(String baseUrl, String via) {
	return new BaseAnnotationId(baseUrl, getIdPartFromUri(via));
    }

}
