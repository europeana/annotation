package eu.europeana.annotation.jsonld;

import java.util.Iterator;
import java.util.List;

import org.apache.stanbol.commons.jsonld.JsonLd;
import org.apache.stanbol.commons.jsonld.JsonLdProperty;
import org.apache.stanbol.commons.jsonld.JsonLdPropertyValue;
import org.apache.stanbol.commons.jsonld.JsonLdResource;

import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.search.SearchProfiles;
import eu.europeana.annotation.definitions.model.search.result.ResultSet;
import eu.europeana.annotation.definitions.model.utils.TypeUtils;
import eu.europeana.annotation.definitions.model.view.AnnotationView;

public class AnnotationSetSerializer extends JsonLd {

	// private static final Logger logger =
	// LoggerFactory.getLogger(AnnotationLd.class);

	TypeUtils typeHelper = new TypeUtils();
	ResultSet<? extends AnnotationView> annotationSet;

	public ResultSet<? extends AnnotationView> getAnnotationSet() {
		return annotationSet;
	}

	public void setAnnotationSet(ResultSet<? extends AnnotationView> annotationSet) {
		this.annotationSet = annotationSet;
	}

	public TypeUtils getTypeHelper() {
		return typeHelper;
	}

	/**
	 * @param annotationSet
	 */
	public AnnotationSetSerializer(ResultSet<? extends AnnotationView> annotationSet) {
		setAnnotationSet(annotationSet);
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
		jsonLdResource.putProperty(WebAnnotationFields.CONTEXT, WebAnnotationFields.OA_CONTEXT);
		String[] oaType = new String[] { "BasicContainer", "Collection" };
		jsonLdResource.putProperty(buildArrayProperty(WebAnnotationFields.AT_TYPE, oaType));
		jsonLdResource.putProperty(WebAnnotationFields.TOTAL_ITEMS, getAnnotationSet().getResultSize());
		
		String[] contains = serializeItems(profile);
		if(contains != null && contains.length > 0)
			jsonLdResource.putProperty(buildArrayProperty(WebAnnotationFields.CONTAINS, contains));

		put(jsonLdResource);

		return toString(4);
	}

	protected String[] serializeItems(SearchProfiles profile) {
		// switch(profile)
		String[] items = new String[(int) getAnnotationSet().getResults().size()];
		int i = 0;
		for (AnnotationView anno : getAnnotationSet().getResults()) {
			items[i++] = anno.getId();
		}

		return items;
	}
	
	
	 /**
	  * TODO: move this to base class
     * build appropriate property representation for string arrays 
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

}
