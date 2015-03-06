package eu.europeana.annotation.definitions.model.agent.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.definitions.model.vocabulary.AgentTypes;

public abstract class BaseAgent implements Agent {

//	private AgentTypes agentType;
//	private String agentType;
	private List<String> agentType = new ArrayList<String>(2);
	private String name;
	private String mbox;
	private String openId;
	private String homepage;
	
	public void addType(String newType) {
		if (!agentType.contains(newType)) {
			agentType.add(newType);
		}
	}
	
	public List<String> getAgentTypes() {
		return agentType;
	}
	
	public void setAgentTypes(List<String> agentTypes) {
		this.agentType = agentTypes;
	}

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
//	public AgentTypes getAgentType() {
//		return agentType;
//	}
	public String getAgentType() {
		String listStr = "";
		if (agentType.size() > 0) {
			listStr = "[";
			for (String s : agentType)
			{
				if (listStr.equals("[")) {
				    listStr += s;
				} else {
					listStr += "," + s;
				}
			}
			listStr += "]";
		}
		return listStr;
	}
	
	@Override
	public void setAgentTypeEnum(AgentTypes curAgentType) {
		agentType.add(curAgentType.name());
//		this.agentType = agentType;
	}
	
	@Override
	public void setAgentType(String agentTypeStr) {
		agentType.clear();
	    if (!StringUtils.isBlank(agentTypeStr)) { 
	    	agentTypeStr = agentTypeStr.replace("[", "").replace("]", "").replace(" ", "");
	        String[] tokens = agentTypeStr.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
	        for(String t : tokens) {
	        	agentType.add(t);
	        }
		}
		
//		setAgentTypeEnum(AgentTypes.valueOf(agentTypeStr));
	}
	
	protected BaseAgent(){}
	
	@Override
	public boolean equals(Object other) {
	    if (!(other instanceof Agent)) {
	        return false;
	    }

	    Agent that = (Agent) other;

	    boolean res = true;
	    
	    /**
	     * equality check for all relevant fields.
	     */
	    if ((this.getAgentType() != null) && (that.getAgentType() != null) &&
	    		(!this.getAgentType().equals(that.getAgentType()))) {
	    	System.out.println("Agent objects have different 'agentType' fields.");
	    	res = false;
	    }
	    
	    if ((this.getHomepage() != null) && (that.getHomepage() != null) &&
	    		(!this.getHomepage().equals(that.getHomepage()))) {
	    	System.out.println("Agent objects have different 'hompage' fields.");
	    	res = false;
	    }
	    
	    if ((this.getName() != null) && (that.getName() != null) &&
	    		(!this.getName().equals(that.getName()))) {
	    	System.out.println("Agent objects have different 'name' fields.");
	    	res = false;
	    }
	    
	    return res;
	}
			
	@Override
	public String toString() {
		String res = "\t### Agent ###\n";
		
		if (getAgentType() != null) 
			res = res + "\t\t" + "agentType:" + getAgentType().toString() + "\n";
		if (getName() != null) 
			res = res + "\t\t" + "name:" + getName() + "\n";
		if (getHomepage() != null) 
			res = res + "\t\t" + "homepage:" + getHomepage() + "\n";
		return res;
	}	
}
