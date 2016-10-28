package eu.europeana.annotation.definitions.model.agent.impl;

import eu.europeana.annotation.definitions.model.vocabulary.AgentTypes;

public class Person extends BaseAgent {

	public Person(){
		super();
		setAgentTypeEnum(AgentTypes.PERSON);
	}
}
