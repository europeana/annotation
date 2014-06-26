package eu.europeana.annotation.definitions.model.agent.impl;

import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.definitions.model.vocabulary.AgentTypes;

public abstract class BaseAgent implements Agent {

	private AgentTypes agentType;
	private String name;
	private String mbox;
	private String openId;
	private String homepage;
		
	@Override
	public String getName() {
		return name;
	}
	@Override
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String getMbox() {
		return mbox;
	}
	@Override
	public void setMbox(String mbox) {
		this.mbox = mbox;
	}
	@Override
	public String getOpenId() {
		return openId;
	}
	@Override
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	@Override
	public String getHomepage() {
		return homepage;
	}
	@Override
	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}
	@Override
	public AgentTypes getAgentType() {
		return agentType;
	}
	@Override
	public void setAgentType(AgentTypes agentType) {
		this.agentType = agentType;
	}
	
	@Override
	public void setAgentType(String agentTypeStr){
		setAgentType(AgentTypes.valueOf(agentTypeStr));
	}
	
	protected BaseAgent(){}
}
