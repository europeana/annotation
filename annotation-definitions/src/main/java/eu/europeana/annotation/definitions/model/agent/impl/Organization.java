package eu.europeana.annotation.definitions.model.agent.impl;

import eu.europeana.annotation.definitions.model.vocabulary.AgentTypes;

public class Organization extends BaseAgent {

	public Organization(){
		super();
		setAgentTypeEnum(AgentTypes.ORGANIZATION);
	}
}
