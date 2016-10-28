package eu.europeana.annotation.definitions.model.moderation.impl;

import java.util.Date;

import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.definitions.model.moderation.Vote;


/**
 * This class comprises information about voting.
 *
 */
public class BaseVote implements Vote {

	private String type;
	private Date created;
	private String userName;
	private String userId;
	private Agent user;
	
	
	public BaseVote(){}
	
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Agent getUser() {
		return user;
	}

	public void setUser(Agent user) {
		this.user = user;
	}

	@Override
	public boolean equalsContent(Object other) {
	    if (!(other instanceof Vote)) {
	        return false;
	    }

	    Vote that = (Vote) other;

	    boolean res = true;
	    
	    /**
	     * equality check for all relevant fields.
	     */
	    if ((this.getType() != null) && (that.getType() != null) &&
	    		(!this.getType().equals(that.getType()))) {
	    	System.out.println("Vote objects have different 'type' fields.");
	    	res = false;
	    }
	    
	    if ((this.getUserName() != null) && (that.getUserName() != null) &&
	    		(!this.getUserName().equals(that.getUserName()))) {
	    	System.out.println("Vote objects have different 'userName' fields.");
	    	res = false;
	    }
	    
	    if ((this.getUserId() != null) && (that.getUserId() != null) &&
	    		(!this.getUserId().equals(that.getUserId()))) {
	    	System.out.println("Vote objects have different 'userId' fields.");
	    	res = false;
	    }
	    
	    return res;
	}
	
	
	@Override
	public String toString() {
		String res = "\t### Vote ###\n";
		
		if (getType() != null) 
			res = res + "\t\t" + "type:" + getType() + "\n";
		if (getUserId() != null) 
			res = res + "\t\t" + "user Id:" + getUserId() + "\n";
		if (getUserName() != null) 
			res = res + "\t\t" + "user name:" + getUserName() + "\n";
		return res;
	}	
}
