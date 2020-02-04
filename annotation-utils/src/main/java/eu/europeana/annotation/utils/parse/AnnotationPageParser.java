package eu.europeana.annotation.utils.parse;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.apache.stanbol.commons.jsonld.JsonLdCommon;
import org.apache.stanbol.commons.jsonld.JsonLdParser;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import eu.europeana.annotation.definitions.exception.AnnotationPageValidationException;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.search.result.AnnotationPage;
import eu.europeana.annotation.definitions.model.search.result.ResultSet;
import eu.europeana.annotation.definitions.model.search.result.impl.AnnotationPageImpl;
import eu.europeana.annotation.definitions.model.view.AnnotationView;
import eu.europeana.annotation.definitions.model.view.impl.AnnotationViewResourceListItem;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.utils.QueryUtils;

/**
 * Annotation page parser
 * 
 * @author Sven Schlarb @ait
 */
public class AnnotationPageParser extends JsonLdParser {

    protected final Logger logger = LogManager.getLogger(this.getClass());

    /**
     * Maps URIs to namespace prefixes.
     */
    protected Map<String, String> namespacePrefixMap = new HashMap<String, String>();

    /**
     * Internal map to hold the namespaces and prefixes that were actually used.
     */
    protected Map<String, String> usedNamespaces = new HashMap<String, String>();

    public AnnotationPage parseAnnotationPage(String jsonLdString) throws JsonParseException {
	AnnotationPage annotationPage = null;

	JSONObject jo;
	try {
	    jo = parseJson(jsonLdString);
	} catch (JSONException e) {
	    throw new JsonParseException("Invalid serialization of AnnotationPage (json): " + jsonLdString, e);
	}
	try {
	    annotationPage = parseAnnotationPageJsonLd(jo);
	} catch (RuntimeException e) {
	    throw new AnnotationPageValidationException("Cannot parse AnnotationPage! " + e.getMessage(), e);
	}

	return annotationPage;
    }

    /**
     * Adds a new namespace and its prefix to the list of used namespaces for this
     * JSON-LD instance.
     * 
     * @param namespace A namespace IRI.
     * @param prefix    A prefix to use and identify this namespace in serialized
     *                  JSON-LD.
     */
    public void addNamespacePrefix(String namespace, String prefix) {
	namespacePrefixMap.put(namespace, prefix);
    }

    /**
     * Parse annotation page
     * 
     * @param jo JSONObject
     * @return Annotation page object
     * @throws JsonParseException
     */
    private AnnotationPage parseAnnotationPageJsonLd(JSONObject jo) throws JsonParseException {
	AnnotationPage ap = new AnnotationPageImpl();

	try {
	    logger.debug(jo.toString(4));
	    if (jo.has(JsonLdCommon.CONTEXT)) {

		Object context = jo.get(JsonLdCommon.CONTEXT);
		if (context instanceof String) {
		    // default context, no namespace parsing is needed ...
		} else if (context instanceof JSONObject) {
		    JSONObject contextObject = (JSONObject) context;
		    // parse namespaces
		    for (int i = 0; i < contextObject.names().length(); i++) {
			String name = contextObject.names().getString(i).toLowerCase();
			addNamespacePrefix(contextObject.getString(name), name);
		    }
		} else if (context instanceof JSONObject) {
		    // TODO: extract namespaces from multiple contexts if
		    // required
		}

		jo.remove(JsonLdCommon.CONTEXT);
	    }
	    if (jo.names() != null && jo.names().length() > 0) {
		for (int i = 0; i < jo.names().length(); i++) {
		    String property = jo.names().getString(i);
		    // logger.debug(property);
		    handleProperty(ap, jo, property);
		}
		// logger.debug(jo.names().length());
	    }
	} catch (JSONException e) {
	    throw new AnnotationPageValidationException("Parsing AnnotationPage object failed! " + e.getMessage(), e);
	} catch (MalformedURLException e) {
	    throw new AnnotationPageValidationException("Malformed URL! " + e.getMessage(), e);
	}

	return ap;
    }

    /**
     * Handle property
     * 
     * @param ap       AnnotationPage object, properties are set for the
     *                 corresponding property
     * @param jo       The JSONObject which contains property values
     * @param property Property name
     * @throws JSONException
     * @throws MalformedURLException
     * @throws JsonParseException
     */
    private void handleProperty(AnnotationPage ap, JSONObject jo, String property)
	    throws JSONException, MalformedURLException, JsonParseException {

	Object valueObject = jo.get(property);

	switch (property) {

	// AnnotationPage.PrevPageUri
	case WebAnnotationFields.PREV:
	    String prevPageUri = valueObject.toString();
	    logger.debug("prevPageUri: " + prevPageUri);
	    ap.setPrevPageUri(prevPageUri);
	    break;

	// AnnotationPage.NextPageUri
	case WebAnnotationFields.NEXT:
	    String nextPageUri = valueObject.toString();
	    logger.debug("nextPageUri: " + nextPageUri);
	    ap.setNextPageUri(nextPageUri);
	    break;

	// AnnotationPage.CurrentPageUri
	case WebAnnotationFields.ID:
	    String currentPageUri = valueObject.toString();
	    logger.debug("currentPageUri: " + currentPageUri);
	    ap.setCurrentPageUri(currentPageUri);
	    // AnnotationPage.CurrentPage (int)
	    String currentPageVal = QueryUtils.getQueryParamValue(valueObject.toString(), WebAnnotationFields.PAGE);
	    int currentPageNum = Integer.parseInt(currentPageVal);
	    logger.debug("currentPage: " + currentPageNum);
	    ap.setCurrentPage(currentPageNum);

	    break;

	// AnnotationPage.items
	case WebAnnotationFields.ITEMS:
	    Object items = jo.get(WebAnnotationFields.ITEMS);

	    ResultSet<? extends AnnotationView> annViewResSet = null;
	    JSONArray itemsJsonArr = (JSONArray) items;
	    parseItems(ap, itemsJsonArr);
	    break;

	case WebAnnotationFields.PART_OF:
	    JSONObject partOfObj = jo.getJSONObject(WebAnnotationFields.PART_OF);
	    // AnnotationPage.CollectionUri
	    String collectionUri = partOfObj.get(WebAnnotationFields.ID).toString();
	    logger.debug("collectionUri: " + collectionUri);
	    ap.setCollectionUri(collectionUri);
	    // AnnotationPage.TotalInCollection
	    long totalInCollection = Long.parseLong(partOfObj.get(WebAnnotationFields.TOTAL).toString());
	    logger.debug("totalInCollection: " + totalInCollection);
	    ap.setTotalInCollection(totalInCollection);
	    break;

	case WebAnnotationFields.TOTAL:
	    // AnnotationPage.TotalInPage
	    long totalInPage = Long.parseLong(valueObject.toString());
	    logger.debug("totalInPage: " + totalInPage);
	    ap.setTotalInPage(totalInPage);
	    break;

	case WebAnnotationFields.TYPE:
	    break;

	default:
	    logger.warn("Unsupported Property: " + property);
	    break;
	}
    }

    /**
     * Parse items and set either the items list or the annotations list depending
     * on the profile. In case of the minimal profile, the AnnotationPage object
     * holds a list of resource IDs, and if the standard profile is used, it holds a
     * list of annotations.
     * 
     * @param annoPage AnnotationPage object holding a list of items or the
     *                 annotations depending on the profile.
     * @param jsonArr  List of items
     * @throws JSONException
     * @throws JsonParseException
     */
    private void parseItems(AnnotationPage annoPage, JSONArray jsonArr) throws JSONException, JsonParseException {
	List<AnnotationViewResourceListItem> avItemList = new ArrayList<AnnotationViewResourceListItem>();
	List<Annotation> annoList = new ArrayList<Annotation>();

	AnnotationViewResourceListItem avrItem;
	Annotation annotation;
	JSONObject annoJson;
	for (int i = 0, size = jsonArr.length(); i < size; i++) {
	    Object item = jsonArr.get(i);
	    if (item instanceof String) {
		// minimal profile
		avrItem = new AnnotationViewResourceListItem();
		avrItem.setId((String) item);
		// avrItem.setTimestampUpdated(new Date(0));
		avItemList.add(avrItem);
	    } else {
		// standard profile
		annoJson = (JSONObject) item;
		annotation = (new AnnotationLdParser()).parseAnnotation(null, annoJson);
		annoList.add(annotation);
	    }

	}
	// if minimal profile build items (ResultSet)
	if (!avItemList.isEmpty()) {
	    ResultSet<AnnotationViewResourceListItem> items = new ResultSet<AnnotationViewResourceListItem>();
	    items.setResultSize(avItemList.size());
	    annoPage.setItems(items);
	}
	// if standard profile set annotations list
	if (!annoList.isEmpty()) {
	    annoPage.setAnnotations(annoList);
	}
    }

}
