package eu.europeana.annotation.definitions.model.vocabulary;

public enum TargetTypes {

	IMAGE, TEXT, ANNOTATION, WEB_PAGE;
	
	public static String isRegisteredAs(String test) {

	    for (TargetTypes c : TargetTypes.values()) {
	        if (c.name().toLowerCase().equals(test.replace("_", "").toLowerCase())) {
	            return c.name();
	        }
	    }

	    return "";
	}	

}
