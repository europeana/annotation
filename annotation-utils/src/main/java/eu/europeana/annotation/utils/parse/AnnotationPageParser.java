package eu.europeana.annotation.utils.parse;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import eu.europeana.annotation.utils.GeneralUtils;

/**
 * Annotation page parser
 * 
 * @author Sven Schlarb @ait
 */
public class AnnotationPageParser extends JsonLdParser {

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

	annotationPage = parseAnnotationPageJsonLd(jo);	

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
	    ap.setPrevPageUri(prevPageUri);
	    break;

	// AnnotationPage.NextPageUri
	case WebAnnotationFields.NEXT:
	    String nextPageUri = valueObject.toString();
	    ap.setNextPageUri(nextPageUri);
	    break;

	// AnnotationPage.CurrentPageUri
	case WebAnnotationFields.ID:
	    String currentPageUri = valueObject.toString();
	    ap.setCurrentPageUri(currentPageUri);
	    // AnnotationPage.CurrentPage (int)
	    String currentPageVal = GeneralUtils.getQueryParamValue(valueObject.toString(), WebAnnotationFields.PAGE);
	    int currentPageNum = Integer.parseInt(currentPageVal);
	    ap.setCurrentPage(currentPageNum);

	    break;

	// AnnotationPage.items
	case WebAnnotationFields.ITEMS:
	    Object items = jo.get(WebAnnotationFields.ITEMS);

	    ResultSet<? extends AnnotationView> annViewResSet = null;
	    JSONArray itemsJsonArr = (JSONArray) items;
	    parseItems(ap, itemsJsonArr);
	    break;

	// AnnotationPage.facets
    case WebAnnotationFields.SEARCH_RESP_FACETS:
        Object facets = jo.get(WebAnnotationFields.SEARCH_RESP_FACETS);
        parseFacets(ap, facets);
        break;

	case WebAnnotationFields.PART_OF:
	    JSONObject partOfObj = jo.getJSONObject(WebAnnotationFields.PART_OF);
	    // AnnotationPage.CollectionUri
	    String collectionUri = partOfObj.get(WebAnnotationFields.ID).toString();
	    ap.setCollectionUri(collectionUri);
	    // AnnotationPage.TotalInCollection
	    long totalInCollection = Long.parseLong(partOfObj.get(WebAnnotationFields.TOTAL).toString());
	    ap.setTotalInCollection(totalInCollection);
	    break;

	case WebAnnotationFields.TOTAL:
	    // AnnotationPage.TotalInPage
	    long totalInPage = Long.parseLong(valueObject.toString());
	    ap.setTotalInPage(totalInPage);
	    break;

	case WebAnnotationFields.TYPE:
	    break;

	default:
	    throw new JSONException("Unsupported Property: " + property + " during parsing of the AnnotationPage object.");
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
	    items.setResults(avItemList);
	    annoPage.setItems(items);
	}
	// if standard profile set annotations list
	if (!annoList.isEmpty()) {
	    annoPage.setAnnotations(annoList);
	}
    }

    /**
     * Parsing facets.
     * 
     * @param annoPage
     * @param facetsJson
     * @throws JSONException
     * @throws JsonParseException
     */
    private void parseFacets(AnnotationPage annoPage, Object facetsJson) throws JSONException {
      Map<String,Map<String,Integer>> facets = new HashMap<>();
      if(facetsJson instanceof JSONArray) {
        JSONArray facetsArr = (JSONArray) facetsJson;
        for (int i = 0, size = facetsArr.length(); i < size; i++) {  
            parseFacetObj((JSONObject) facetsArr.get(i),facets);
        }
      }
      else {
        parseFacetObj((JSONObject) facetsJson,facets);        
      }
      annoPage.setFacets(facets);
    }
    
    private void parseFacetObj(JSONObject facetObj, Map<String,Map<String,Integer>> facets) throws JSONException {
      String field = facetObj.getString(WebAnnotationFields.SEARCH_RESP_FACETS_FIELD);
      Map<String,Integer> values = new HashMap<>();
      Object valuesJson = facetObj.get(WebAnnotationFields.SEARCH_RESP_FACETS_VALUES);
      if(valuesJson instanceof JSONArray) {
        JSONArray valuesArr = (JSONArray) valuesJson;
        for (int j = 0, sizeValues = valuesArr.length(); j < sizeValues; j++) {
          JSONObject valueObj = (JSONObject) valuesArr.get(j); 
          String label = valueObj.getString(WebAnnotationFields.SEARCH_RESP_FACETS_LABEL);
          int count = valueObj.getInt(WebAnnotationFields.SEARCH_RESP_FACETS_COUNT);
          values.put(label, count);
        }
      }
      else {
        JSONObject valueObj = (JSONObject) valuesJson;
        String label = valueObj.getString(WebAnnotationFields.SEARCH_RESP_FACETS_LABEL);
        int count = valueObj.getInt(WebAnnotationFields.SEARCH_RESP_FACETS_COUNT);
        values.put(label, count);    
      }           
      facets.put(field, values);

    }

}
