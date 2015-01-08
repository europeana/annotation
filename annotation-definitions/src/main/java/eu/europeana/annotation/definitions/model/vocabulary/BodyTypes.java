package eu.europeana.annotation.definitions.model.vocabulary;

public enum BodyTypes {
	TEXT, TAG, SEMANTIC_TAG, SEMANTIC_LINK;
	
	public static boolean isTagBody(String type){
		return isSimpleTagBody(type) || isSemanticTagBody(type);
	}

	public static boolean isSimpleTagBody(String type){
		return TAG.name().equals(type);
	}
	
	public static boolean isSemanticTagBody(String type){
		return SEMANTIC_TAG.name().equals(type);
	}
	
	public static boolean contains(String test) {

	    for (BodyTypes c : BodyTypes.values()) {
	        if (c.name().equals(test)) {
	            return true;
	        }
	    }

	    return false;
	}	
	
}
