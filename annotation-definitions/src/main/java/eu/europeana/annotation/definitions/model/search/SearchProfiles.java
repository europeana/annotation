package eu.europeana.annotation.definitions.model.search;

public enum SearchProfiles {
	
	FACETS, STANDARD, MINIMAL, DEREFERENCE, DEBUG;

	public static boolean contains(String value) {

	    for (SearchProfiles c : SearchProfiles.values()) {
	        if(c.name().equalsIgnoreCase((value)))
	        	return true;
	    }

	    return false;
	}
	
	public static SearchProfiles getByStr(String value) {

	    for (SearchProfiles c : SearchProfiles.values()) {
	        if(c.name().equalsIgnoreCase((value)))
	        	return c;
	    }

	    return STANDARD;
	}
	
	@Override
	public String toString() {
		return name().toLowerCase();
	}

}