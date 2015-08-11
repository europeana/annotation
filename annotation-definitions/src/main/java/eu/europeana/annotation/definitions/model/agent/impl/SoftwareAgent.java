package eu.europeana.annotation.definitions.model.agent.impl;

import eu.europeana.annotation.definitions.model.vocabulary.AgentTypes;

public class SoftwareAgent extends BaseAgent{

	public SoftwareAgent(){
		super();
		setAgentTypeEnum(AgentTypes.SOFTWARE);
	}
}
