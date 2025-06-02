package eu.europeana.annotation.definitions.model.agent.impl;

import java.util.Objects;
import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.definitions.model.vocabulary.AgentTypes;

public abstract class BaseAgent implements Agent {

  private String httpUrl;
  // private AgentTypes agentType;
  private String agentType;
  // private List<String> agentType = new ArrayList<String>(2);
  private String internalType;
  private String name;
  private String email;
  private String email_sha1;
  private String nickname;

  private String homepage;
  private String inputString;
  private String userGroup;

  @Override
  public String getHttpUrl() {
    return httpUrl;
  }

  @Override
  public void setHttpUrl(String httpUrl) {
    this.httpUrl = httpUrl;
  }

  // public void addType(String newType) {
  // if (!agentType.contains(newType)) {
  // agentType.add(newType);
  // }
  // }
  //
  // public List<String> getAgentType() {
  // return agentType;
  // }
  //
  // public void setAgentType(List<String> agentTypeList) {
  // this.agentType = agentTypeList;
  // }

  @Override
  public String getUserGroup() {
    return userGroup;
  }

  @Override
  public void setUserGroup(String userGroup) {
    this.userGroup = userGroup;
  }

  @Override
  public String getType() {
    return agentType;
  }

  @Override
  public void setType(String agentType) {
    this.agentType = agentType;
  }

  @Override
  public String getInternalType() {
    return internalType;
  }

  @Override
  public void setInternalType(String internalType) {
    this.internalType = internalType;
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
  public String getEmail() {
    return email;
  }

  @Override
  public void setEmail(String email) {
    this.email = email;
  }

  @Override
  public String getEmail_Sha1() {
    return email_sha1;
  }

  @Override
  public void setEmail_Sha1(String email_sha1) {
    this.email_sha1 = email_sha1;
  }

  @Override
  public String getNickname() {
    return nickname;
  }

  @Override
  public void setNickname(String nickname) {
    this.nickname = nickname;
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
  public String getInputString() {
    return inputString;
  }

  @Override
  public void setInputString(String inputString) {
    this.inputString = inputString;
  }

  // @Override
  // public AgentTypes getAgentType() {
  // return agentType;
  //

  // public String getAgentTypeStr() {
  // String listStr = "";
  // if (agentType.size() > 0) {
  // listStr = "[";
  // for (String s : agentType)
  // {
  // if (listStr.equals("[")) {
  // listStr += s;
  // } else {
  // listStr += "," + s;
  // }
  // }
  // listStr += "]";
  // }
  // return listStr;
  // }

  @Override
  public void setAgentTypeEnum(AgentTypes curAgentType) {
    // agentType.add(curAgentType.name());
    agentType = curAgentType.name();
    // this.agentType = agentType;
  }

  @Override
  public void setAgentTypeAsString(String agentTypeStr) {
    agentType = agentTypeStr;
    // agentType.clear();
    // if (!StringUtils.isBlank(agentTypeStr)) {
    // agentTypeStr = agentTypeStr.replace("[", "").replace("]", "").replace(" ", "");
    // String[] tokens = agentTypeStr.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
    // for(String t : tokens) {
    // agentType.add(t);
    // }
    // }

    //// setAgentTypeEnum(AgentTypes.valueOf(agentTypeStr));
  }

  protected BaseAgent() {}

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
    if (!Objects.equals(this.getType(), that.getType())) {
      res = false;
    }

    if (!Objects.equals(this.getHttpUrl(), that.getHttpUrl())) {
      res = false;
    }

    if (!Objects.equals(this.getHomepage(), that.getHomepage())) {
      res = false;
    }

    if (!Objects.equals(this.getName(), that.getName())) {
      res = false;
    }

    return res;
  }

  public boolean equalsContent(Object other) {
    return equals(other);
  }

  @Override
  public String toString() {
    String res = "{";

    if (getType() != null)
      res +=  "agentType:" + getType();
    if (getHttpUrl() != null)
      res +=  ", id:" + getHttpUrl();
    if (getName() != null)
      res += ", name:" + getName();
    if (getHomepage() != null)
      res += ", homepage:" + getHomepage();
    res +=  "}";
    return res;
  }
}
