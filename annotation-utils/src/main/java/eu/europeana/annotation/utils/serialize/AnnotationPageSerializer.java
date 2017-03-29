package eu.europeana.annotation.utils.serialize;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.stanbol.commons.jsonld.JsonLd;
import org.apache.stanbol.commons.jsonld.JsonLdProperty;
import org.apache.stanbol.commons.jsonld.JsonLdPropertyValue;
import org.apache.stanbol.commons.jsonld.JsonLdResource;
import org.codehaus.jettison.json.JSONArray;

import eu.europeana.annotation.definitions.exception.search.SearchRuntimeException;
import eu.europeana.annotation.definitions.model.search.SearchProfiles;
import eu.europeana.annotation.definitions.model.search.result.AnnotationPage;
import eu.europeana.annotation.definitions.model.search.result.FacetFieldView;
import eu.europeana.annotation.definitions.model.search.result.ResultSet;
import eu.europeana.annotation.definitions.model.utils.TypeUtils;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.view.AnnotationView;
import eu.europeana.annotation.definitions.model.vocabulary.ContextTypes;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;

public class AnnotationPageSerializer extends JsonLd {

	TypeUtils typeHelper = new TypeUtils();
	AnnotationPage protocolPage;

	public AnnotationPageSerializer(AnnotationPage protocolPage) {
		this.protocolPage = protocolPage;
	}

	public TypeUtils getTypeHelper() {
		return typeHelper;
	}

	private ResultSet<? extends AnnotationView> getPageItems() {
		return protocolPage.getItems();
	}

	private List<Annotation> getAnnotations() {
		return (List<Annotation>) protocolPage.getAnnotations();
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
		// annotation page
		jsonLdResource.putProperty(WebAnnotationFields.ID, protocolPage.getCurrentPageUri());
		jsonLdResource.putProperty(WebAnnotationFields.TYPE, "AnnotationPage");
		jsonLdResource.putProperty(WebAnnotationFields.TOTAL, protocolPage.getTotalInPage());

		// collection
		JsonLdProperty collectionProp = new JsonLdProperty(WebAnnotationFields.PART_OF);
		JsonLdPropertyValue collectionPropValue = new JsonLdPropertyValue();
		collectionPropValue.putProperty(new JsonLdProperty(WebAnnotationFields.ID, protocolPage.getCollectionUri()));
		collectionPropValue
				.putProperty(new JsonLdProperty(WebAnnotationFields.TOTAL, protocolPage.getTotalInCollection()));
		collectionProp.addValue(collectionPropValue);

		jsonLdResource.putProperty(collectionProp);

		// items
		serializeItems(jsonLdResource, profile);
		serializeFacets(jsonLdResource, profile);

		// nagivation
		if (protocolPage.getPrevPageUri() != null)
			jsonLdResource.putProperty(WebAnnotationFields.PREV, protocolPage.getPrevPageUri());
		if (protocolPage.getNextPageUri() != null)
			jsonLdResource.putProperty(WebAnnotationFields.NEXT, protocolPage.getNextPageUri());

		put(jsonLdResource);

		return toString(4);
	}

	protected void serializeFacets(JsonLdResource jsonLdResource, SearchProfiles profile) {
		if (getPageItems().getFacetFields() == null || getPageItems().getFacetFields().isEmpty())
			return;

		JsonLdProperty facetsProperty = new JsonLdProperty(WebAnnotationFields.SEARCH_RESP_FACETS);

		for (FacetFieldView view : getPageItems().getFacetFields())
			facetsProperty.addValue(buildFacetPropertyValue(view));

		if (facetsProperty.getValues() != null && !facetsProperty.getValues().isEmpty())
			jsonLdResource.putProperty(facetsProperty);

	}

	private JsonLdPropertyValue buildFacetPropertyValue(FacetFieldView view) {

		JsonLdPropertyValue facetViewEntry = new JsonLdPropertyValue();

		facetViewEntry.putProperty(new JsonLdProperty(WebAnnotationFields.SEARCH_RESP_FACETS_FIELD, view.getName()));

		// only if values for facet count are available
		if (view.getValueCountMap() != null && !view.getValueCountMap().isEmpty()) {

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
		}

		return facetViewEntry;
	}

	protected void serializeItems(JsonLdResource jsonLdResource, SearchProfiles profile) {

		switch (profile) {
		case FACET:
			// do not serialize items
			break;

		case STANDARD:
			putStandardItemsProperty(jsonLdResource);

			break;

		case MINIMAL:
			putMinimalItemsProperty(jsonLdResource);

			break;

		default:
			throw new SearchRuntimeException("Unsupported search profile: " + profile);
		}

	}

	protected void putMinimalItemsProperty(JsonLdResource jsonLdResource) {
		String[] items = new String[(int) getPageItems().getResults().size()];
		int i = 0;
		for (AnnotationView anno : getPageItems().getResults()) {
			items[i++] = anno.getId();
		}

		if (items.length > 0)
			putStringArrayProperty(WebAnnotationFields.ITEMS, items, jsonLdResource);
	}

	protected void putStandardItemsProperty(JsonLdResource jsonLdResource) {
		
		registerContainerProperty(WebAnnotationFields.ITEMS);
		
		if(protocolPage.getAnnotations() == null || protocolPage.getAnnotations().isEmpty())
			return;
		
		JsonLdProperty itemsProp = new JsonLdProperty(WebAnnotationFields.ITEMS);
		itemsProp.setForceMultivalued(true);
		AnnotationLdSerializer annoLdSerializer;
		for(Annotation annotation: protocolPage.getAnnotations()) {
			//transform annotation object to json-ld
			annoLdSerializer = new AnnotationLdSerializer();
			JsonLdResource annotationLd = annoLdSerializer.setAnnotation(annotation);
			
			//build property value for the given annotation
			JsonLdPropertyValue propertyValue = new JsonLdPropertyValue();
			propertyValue.getPropertyMap().putAll(annotationLd.getPropertyMap());
			itemsProp.addValue(propertyValue);
		}
		jsonLdResource.putProperty(itemsProp);

	}

}
