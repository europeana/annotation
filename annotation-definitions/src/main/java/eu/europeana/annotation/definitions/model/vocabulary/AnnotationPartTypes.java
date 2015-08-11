package eu.europeana.annotation.definitions.model.vocabulary;


public enum AnnotationPartTypes {

	AGENT, ANNOTATION, BODY, MOTIVATION, SELECTOR, SHAPE, STYLE, TAG, TARGET;  
	
	public static boolean contains(String test) {

	    for (AnnotationPartTypes c : AnnotationPartTypes.values()) {
	        if (c.name().equals(test)) {
	            return true;
	        }
	    }

	    return false;
	}	
	
}
