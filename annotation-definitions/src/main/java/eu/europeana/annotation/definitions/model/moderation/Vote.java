package eu.europeana.annotation.definitions.model.moderation;

import java.util.Date;

import eu.europeana.annotation.definitions.model.agent.Agent;

public interface Vote {

	public String getType();

	public void setType(String type);

	public Date getCreated();

	public void setCreated(Date created);

	public String getUserName();

	public void setUserName(String username);

	public String getUserId();

	public void setUserId(String userId);

	public Agent getUser();

	public void setUser(Agent user);

	public boolean equalsContent(Object other);

}
