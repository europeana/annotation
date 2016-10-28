package eu.europeana.annotation.definitions.model.vocabulary;


public enum IdGenerationTypes {

	  PROVIDED("provided")                                    // historypin
	, GENERATED_BY_PROVIDER("generated_by_provider")          // webanno
	, GENERATED_BY_RESOURCE_ID("generated_by_resource_id");   // [/collection/object/provider]
	
	private String idType;
	
	IdGenerationTypes(String idType){
		this.idType = idType;
	}
	
	public String getIdType(){
		return idType;
	}
	
	public static String isRegisteredAs(String test) {

	    for (IdGenerationTypes c : IdGenerationTypes.values()) {
	        if (c.getIdType().toLowerCase().equals(test.toLowerCase())) {
	            return c.name();
	        }
	    }

	    return "";
	}		
	
	public static IdGenerationTypes getValueByType(String test) {

	    for (IdGenerationTypes c : IdGenerationTypes.values()) {
	        if (c.getIdType().toLowerCase().equals(test.toLowerCase())) {
	            return c;
	        }
	    }

	    return null;
	}		
	
	public static boolean isRegistered(String test) {

		boolean res = false;
	    for (IdGenerationTypes c : IdGenerationTypes.values()) {
	        if (c.getIdType().toLowerCase().equals(test.toLowerCase())) {
	            res = true;
	            break;
	        }
	    }

	    return res;
	}		
	
	public static String printTypes() {

		String res = " Current ID generation types are: ";
	    for (IdGenerationTypes c : IdGenerationTypes.values()) {
	    	res = res + "'" + c.getIdType() + "',";
	    }

	    return res.substring(0, res.length() - 1) + ".";
	}		
	
}
