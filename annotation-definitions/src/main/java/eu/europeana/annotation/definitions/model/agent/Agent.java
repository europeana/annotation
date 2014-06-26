package eu.europeana.annotation.definitions.model.agent;

import eu.europeana.annotation.definitions.model.vocabulary.AgentTypes;

public interface Agent {

	public abstract void setAgentType(String agentTypeStr);

	public abstract void setAgentType(AgentTypes agentType);

	public abstract AgentTypes getAgentType();

	public abstract void setHomepage(String homepage);

	public abstract String getHomepage();

	public abstract void setOpenId(String openId);

	public abstract String getOpenId();

	public abstract void setMbox(String mbox);

	public abstract String getMbox();

	public abstract void setName(String name);

	public abstract String getName();

}
