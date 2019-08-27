package eu.europeana.annotation.definitions.model.vocabulary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import eu.europeana.annotation.definitions.model.utils.TypeUtils;

public enum BodyInternalTypes {
	TEXT, TAG, SEMANTIC_TAG, LINK, SEMANTIC_LINK, GEO_TAG, GRAPH, SPECIFIC_RESOURCE;
	
	public static boolean isTagBody(String type) {
		boolean res = false;
		if (StringUtils.isNotEmpty(type))
			res = isTagBody(new ArrayList<String>(Arrays.asList(type)));
		return res;
	}

	public static boolean isTagBody(List<String> types) {
		return isSimpleTagBody(types) || isSemanticTagBody(types);
	}

	public static boolean isSimpleTagBody(List<String> types) {
		return TypeUtils.isTypeInList(TAG.name(), types);
	}
	
	public static boolean isSemanticTagBody(List<String> types) {
		return TypeUtils.isTypeInList(SEMANTIC_TAG.name(), types);
	}
	
	public static boolean isSemanticTagBody(String internalType) {
		return SEMANTIC_TAG.name().equalsIgnoreCase(internalType);
	}
	
	public static boolean isGeoTagBody(String internalType) {
		return GEO_TAG.name().equalsIgnoreCase(internalType);
	}
	
	public static boolean isGraphBody(String internalType) {
		return GRAPH.name().equalsIgnoreCase(internalType);
	}
	
	public static boolean contains(String test) {

	    for (BodyInternalTypes c : BodyInternalTypes.values()) {
	        if (c.name().equals(test)) {
	            return true;
	        }
	    }

	    return false;
	}	
	
}
