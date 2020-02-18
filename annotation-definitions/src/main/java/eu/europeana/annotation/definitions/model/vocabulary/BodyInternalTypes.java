package eu.europeana.annotation.definitions.model.vocabulary;

import java.util.List;

import eu.europeana.annotation.definitions.model.utils.TypeUtils;

public enum BodyInternalTypes {
	TEXT, TAG, SEMANTIC_TAG, LINK, SEMANTIC_LINK, GEO_TAG, GRAPH, SPECIFIC_RESOURCE, FULL_TEXT_RESOURCE, AGENT, VCARD_ADDRESS;
	
	public static boolean isTagBody(String type) {
	    return isSimpleTagBody(type) || isSemanticTagBody(type);
	}

	
	@Deprecated
	public static boolean isTagBody(List<String> types) {
		return isSimpleTagBody(types) || isSemanticTagBody(types);
	}

	@Deprecated
	public static boolean isSimpleTagBody(List<String> types) {
		return TypeUtils.isTypeInList(TAG.name(), types);
	}
	
	public static boolean isSimpleTagBody(String internalType) {
		return TAG.name().equalsIgnoreCase(internalType);
	}
	
	@Deprecated
	public static boolean isSemanticTagBody(List<String> types) {
		return TypeUtils.isTypeInList(SEMANTIC_TAG.name(), types);
	}
	
	public static boolean isSemanticTagBody(String type) {
		return SEMANTIC_TAG.name().equalsIgnoreCase(type);
	}
	
	public static boolean isGeoTagBody(String type) {
		return GEO_TAG.name().equalsIgnoreCase(type);
	}
	
	public static boolean isGraphBody(String type) {
		return GRAPH.name().equalsIgnoreCase(type);
	}
	
	public static boolean isAgentBodyTag(String type) {
		return AGENT.name().equalsIgnoreCase(type);
	}
	
	public static boolean isVcardAddressTagBody(String type) {
		return VCARD_ADDRESS.name().equalsIgnoreCase(type);
	}
	
	
	public static boolean isFullTextResourceTagBody(String type) {
		return FULL_TEXT_RESOURCE.name().equalsIgnoreCase(type);
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
