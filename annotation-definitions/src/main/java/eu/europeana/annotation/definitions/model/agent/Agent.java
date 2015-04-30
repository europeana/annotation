package eu.europeana.annotation.definitions.model.agent;

import java.util.List;

import eu.europeana.annotation.definitions.model.vocabulary.AgentTypes;

public interface Agent {

	public abstract void setAgentType(List<String> agentType);
//	public abstract void setAgentType(String agentTypeStr);
	public abstract void setAgentTypeAsString(String agentTypeStr);

	public abstract void addType(String newType);

	public abstract void setAgentTypeEnum(AgentTypes agentType);

//	public abstract AgentTypes getAgentType();
	public abstract List<String> getAgentType();
//	public abstract String getAgentType();

	public abstract void setHomepage(String homepage);

	public abstract String getHomepage();

	public abstract void setOpenId(String openId);

	public abstract String getOpenId();

	public abstract void setMbox(String mbox);

	public abstract String getMbox();

	public abstract void setName(String name);

	public abstract String getName();
	
	public abstract void setInputString(String string);
	
	public abstract String getInputString();

}
