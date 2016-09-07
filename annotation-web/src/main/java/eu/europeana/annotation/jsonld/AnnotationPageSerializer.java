package eu.europeana.annotation.jsonld;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.stanbol.commons.jsonld.JsonLd;
import org.apache.stanbol.commons.jsonld.JsonLdProperty;
import org.apache.stanbol.commons.jsonld.JsonLdPropertyValue;
import org.apache.stanbol.commons.jsonld.JsonLdResource;

import eu.europeana.annotation.definitions.model.search.SearchProfiles;
import eu.europeana.annotation.definitions.model.search.result.FacetFieldView;
import eu.europeana.annotation.definitions.model.search.result.ResultSet;
import eu.europeana.annotation.definitions.model.utils.TypeUtils;
import eu.europeana.annotation.definitions.model.view.AnnotationView;
import eu.europeana.annotation.definitions.model.vocabulary.ContextTypes;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.web.exception.FunctionalRuntimeException;
import eu.europeana.annotation.web.protocol.model.AnnotationPage;

public class AnnotationPageSerializer extends JsonLd {

	// private static final Logger logger =
	// LoggerFactory.getLogger(AnnotationLd.class);
	//ResultSet<? extends AnnotationView> annotationSet;
	
	TypeUtils typeHelper = new TypeUtils();
	AnnotationPage protocolPage;

	public  AnnotationPageSerializer(AnnotationPage protocolPage) {
		this.protocolPage = protocolPage;
	}

	public TypeUtils getTypeHelper() {
		return typeHelper;
	}
	
	private ResultSet<? extends AnnotationView> getPageItems() {
		return protocolPage.getItems();
	}
	

	/**
	 * Adds the given annotation to this JsonLd object using the resource's
	 * subject as key. If the key is NULL and there does not exist a resource
	 * with an empty String as key the resource will be added using an empty
	 * String ("") as key.
	 * 
	 * @param annotation
	 */
	public String serialize(SearchProfiles profile) {

		setUseTypeCoercion(false);
		setUseCuries(true);
		// addNamespacePrefix(WebAnnotationFields.OA_CONTEXT,
		// WebAnnotationFields.OA);
		// TODO: verify if the following check is needed
		// if(isApplyNamespaces())
		setUsedNamespaces(namespacePrefixMap);

		JsonLdResource jsonLdResource = new JsonLdResource();
		jsonLdResource.setSubject("");
		jsonLdResource.putProperty(WebAnnotationFields.AT_CONTEXT, ContextTypes.ANNO.getJsonValue());
		//annotation page
		jsonLdResource.putProperty(WebAnnotationFields.ID, protocolPage.getCurrentPageUri());
		jsonLdResource.putProperty(WebAnnotationFields.TYPE, "AnnotationPage");
		jsonLdResource.putProperty(WebAnnotationFields.TOTAL, protocolPage.getTotalInPage());
		
		//collection
		JsonLdProperty collectionProp = new JsonLdProperty(WebAnnotationFields.PART_OF);
		JsonLdPropertyValue collectionPropValue = new JsonLdPropertyValue();
		collectionPropValue.putProperty(
				new JsonLdProperty(WebAnnotationFields.ID, protocolPage.getCollectionUri()));
		collectionPropValue.putProperty(
				new JsonLdProperty(WebAnnotationFields.TOTAL, protocolPage.getTotalInCollection()));
		collectionProp.addValue(collectionPropValue);
		
		jsonLdResource.putProperty(collectionProp);
		
		//items
		serializeItems(jsonLdResource, profile);
		serializeFacets(jsonLdResource, profile);
		
		//nagivation
		if(protocolPage.getPrevPageUri() != null)
			jsonLdResource.putProperty(WebAnnotationFields.PREV, protocolPage.getPrevPageUri());
		if(protocolPage.getNextPageUri() != null)
			jsonLdResource.putProperty(WebAnnotationFields.NEXT, protocolPage.getNextPageUri());
		
		put(jsonLdResource);

		return toString(4);
	}

	protected void serializeFacets(JsonLdResource jsonLdResource, SearchProfiles profile) {
		if(getPageItems().getFacetFields() == null || getPageItems().getFacetFields().isEmpty())
			return;
		
		JsonLdProperty facetsProperty = new JsonLdProperty(WebAnnotationFields.SEARCH_RESP_FACETS);
//		JsonLdPropertyValue facetsPropertyValue = new JsonLdPropertyValue();
		//JsonLdProperty facetViewProperty = new JsonLdProperty(null);
		
		for (FacetFieldView view : getPageItems().getFacetFields()) 
			facetsProperty.addValue(buildFacetPropertyValue(view));
		
		jsonLdResource.putProperty(facetsProperty);
				
	}

	private JsonLdPropertyValue buildFacetPropertyValue(FacetFieldView view) {
		
		JsonLdPropertyValue facetViewEntry = new JsonLdPropertyValue();
		
		facetViewEntry.putProperty(new JsonLdProperty(WebAnnotationFields.SEARCH_RESP_FACETS_FIELD, view.getName()));
		
		JsonLdProperty values = new JsonLdProperty(WebAnnotationFields.SEARCH_RESP_FACETS_VALUES);
		JsonLdPropertyValue labelCountValue;
		Map<String, String> valueMap;
		
		for (Map.Entry<String, Long> valueCount : view.getValueCountMap().entrySet()) {
			labelCountValue = new JsonLdPropertyValue();
			valueMap = new TreeMap<String, String>();
			valueMap.put(WebAnnotationFields.SEARCH_RESP_FACETS_LABEL, valueCount.getKey());
			valueMap.put(WebAnnotationFields.SEARCH_RESP_FACETS_COUNT, valueCount.getValue().toString());
			labelCountValue.setValues(valueMap);
			
			values.addValue(labelCountValue);
		}
		
		facetViewEntry.putProperty(values);
		
		return facetViewEntry;
	}

	protected void serializeItems(JsonLdResource jsonLdResource, SearchProfiles profile) {

		if (SearchProfiles.FACET.equals(profile))
			return;

		// switch(profile)
		if (SearchProfiles.STANDARD.equals(profile)) {

			String[] items = new String[(int) getPageItems().getResults().size()];
			int i = 0;
			for (AnnotationView anno : getPageItems().getResults()) {
				items[i++] = anno.getId();
			}
			
			if(items.length > 0 )
				jsonLdResource.putProperty(buildArrayProperty(WebAnnotationFields.CONTAINS, items));
			
			return;//needs until updated to switch construct
		}
		
		throw new FunctionalRuntimeException("Unsupported search profile: " + profile);
	}

	/**
	 * TODO: move this to base class build appropriate property representation
	 * for string arrays
	 * 
	 * @param propertyName
	 * @param valueList
	 * @return
	 */
	protected JsonLdProperty buildArrayProperty(String propertyName, String[] values) {

		if (values == null)
			return null;

		JsonLdProperty arrProperty = new JsonLdProperty(propertyName);
		JsonLdPropertyValue propertyValue;
		for (int i = 0; i < values.length; i++) {
			propertyValue = new JsonLdPropertyValue();
			propertyValue.setValue(values[i]);
			arrProperty.addValue(propertyValue);
		}

		return arrProperty;
	}
	
	
	/**
	 * TODO: move this to base class build appropriate property representation
	 * for string arrays
	 * 
	 * @param propertyName
	 * @param valueList
	 * @return
	 */
	protected JsonLdProperty buildValueArrayProperty(String propertyName, String[] values) {

		if (values == null)
			return null;

		JsonLdProperty arrProperty = new JsonLdProperty(propertyName);
		JsonLdPropertyValue propertyValue;
		for (int i = 0; i < values.length; i++) {
			propertyValue = new JsonLdPropertyValue();
			propertyValue.setValue(values[i]);
			arrProperty.addValue(propertyValue);
		}

		return arrProperty;
	}
	
	/**
	 * @param map
	 * @param propertyValue
	 * @param field
	 */
	private void addMapToProperty(Map<String, String> map, JsonLdPropertyValue propertyValue, String field) {
        JsonLdProperty fieldProperty = new JsonLdProperty(field);
        JsonLdPropertyValue fieldPropertyValue = new JsonLdPropertyValue();
        
	    Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<String, String> pairs = (Map.Entry<String, String>) it.next();
	        String curValue = pairs.getValue();
        	if (!StringUtils.isBlank(curValue)) 
        		fieldPropertyValue.getValues().put(pairs.getKey(), pairs.getValue());
	        it.remove(); // avoids a ConcurrentModificationException
	    }
        if (fieldPropertyValue.getValues().size() != 0) {
         	fieldProperty.addValue(fieldPropertyValue);        
         	propertyValue.putProperty(fieldProperty);
    	}
	}
	

}
