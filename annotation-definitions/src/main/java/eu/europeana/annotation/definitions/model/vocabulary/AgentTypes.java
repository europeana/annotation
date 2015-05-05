package eu.europeana.annotation.definitions.model.vocabulary;


public enum AgentTypes{

	PERSON, SOFTWARE, ORGANIZATION;
	
	public static String isRegisteredAs(String test) {

	    for (AgentTypes c : AgentTypes.values()) {
	        if (c.name().toLowerCase().equals(test.replace("_", "").toLowerCase())) {
	            return c.name();
	        }
	    }

	    return "";
	}		
	
}
