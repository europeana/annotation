package eu.europeana.annotation.definitions.model.vocabulary;

public enum StatusTypes {
	PRIVATE, PUBLIC, DISABLED;
	
	public static boolean isRegistered(String type) {

	    for (StatusTypes c : StatusTypes.values()) {
	        if (c.name().toLowerCase().equals(type.toLowerCase())) {
	            return true;
	        }
	    }

	    return false;
	}		
	
}
