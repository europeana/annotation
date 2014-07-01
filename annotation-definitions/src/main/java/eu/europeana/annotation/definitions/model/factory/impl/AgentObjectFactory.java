package eu.europeana.annotation.definitions.model.factory.impl;

import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.definitions.model.agent.impl.Organization;
import eu.europeana.annotation.definitions.model.agent.impl.Person;
import eu.europeana.annotation.definitions.model.agent.impl.SoftwareAgent;
import eu.europeana.annotation.definitions.model.factory.AbstractModelObjectFactory;
import eu.europeana.annotation.definitions.model.vocabulary.AgentTypes;

public class AgentObjectFactory extends
		AbstractModelObjectFactory<Agent, AgentTypes> {

	private static AgentObjectFactory singleton;

	// force singleton usage
	private AgentObjectFactory() {
	};

	public static synchronized AgentObjectFactory getInstance() {

		if (singleton == null)
			singleton = new AgentObjectFactory();

		return singleton;

	}

	@Override
	public Class<? extends Agent> getClassForType(Enum<AgentTypes> modelType) {

		Class<? extends Agent> returnType = null;
		AgentTypes agentType = AgentTypes.valueOf(modelType.name());
		switch (agentType) {
		case PERSON:
			returnType = Person.class;
			break;

		case ORGANIZATION:
			returnType = Organization.class;
			break;

		case SOFTWARE_AGENT:
			returnType = SoftwareAgent.class; 
			break;

		default:
			break;
		}

		return returnType;
	}

	@Override
	public Class<AgentTypes> getEnumClass() {
		return AgentTypes.class;
	}
	
}
