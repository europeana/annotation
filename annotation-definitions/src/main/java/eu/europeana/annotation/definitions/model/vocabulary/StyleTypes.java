package eu.europeana.annotation.definitions.model.vocabulary;


public enum StyleTypes{

	CSS;
	
	public static String isRegisteredAs(String test) {

	    for (StyleTypes c : StyleTypes.values()) {
	        if (c.name().toLowerCase().equals(test.replace("_", "").toLowerCase())) {
	            return c.name();
	        }
	    }

	    return "";
	}		
	
}
