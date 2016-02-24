package eu.europeana.annotation.definitions.model.vocabulary;


public enum AgentTypes implements WebAnnoationKeyword{

	PERSON("Person"), SOFTWARE("SoftwareAgent"), ORGANIZATION("Organization");
	
	private String jsonValue;

	AgentTypes(String jsonValue){
		this.jsonValue = jsonValue; 
	}
	
	/**
	 * Identifying agent type by the json value.
	 * For user friendliness the the comparison is case insensitive  
	 * @param jsonValue
	 * @return
	 */
	public static AgentTypes getByJsonValue(String jsonValue){
		for(AgentTypes agentType : AgentTypes.values()){
			if(agentType.getJsonValue().equalsIgnoreCase(jsonValue))
				return agentType;
		}
		return null;
	}
	
	@Override
	public String getJsonValue() {
		return jsonValue;
	}
	
	@Override
	public String toString() {
		return getJsonValue();
	}

	
}
