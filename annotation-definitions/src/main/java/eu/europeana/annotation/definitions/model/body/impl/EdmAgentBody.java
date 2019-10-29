package eu.europeana.annotation.definitions.model.body.impl;

import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.definitions.model.agent.impl.EdmAgent;
import eu.europeana.annotation.definitions.model.body.AgentBody;

public class EdmAgentBody extends BaseBody implements AgentBody {

	Agent agent = new EdmAgent();

	@Override
	public Agent getAgent() {
		return agent;
	}

	@Override
	public void setAgent(Agent agent) {
		this.agent = agent;
	}
			
	@Override
	public boolean equalsContent(Object other) {
		// TODO Auto-generated method stub
		return super.equalsContent(other);
	}
	
	@Override
	public boolean equals(Object other) {
		
		if(!super.equals(other))
			return false;
		
		if (!(other instanceof EdmAgentBody))
		        return false;
		
		EdmAgentBody that = (EdmAgentBody) other;
		
		if(this.getAgent() == null)
			return that.getAgent() == null; 
		else 
			return this.getAgent().equals(that.getAgent()); 
		
	}
	
}
