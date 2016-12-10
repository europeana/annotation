package eu.europeana.annotation.web.service.authentication.model;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.definitions.model.agent.impl.Person;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;

public class AgentDeserializer extends BaseDeserializer implements JsonDeserializer<Agent>
{
    @Override
    public Agent deserialize(JsonElement je, Type jsonType, JsonDeserializationContext jdc)
        throws JsonParseException
    {
        final JsonObject jsonObject = je.getAsJsonObject();

        final String homepage = deserializeStringField(jsonObject, WebAnnotationFields.HOMEPAGE);
        final String internalType = deserializeStringField(jsonObject, WebAnnotationFields.INTERNAL_TYPE);
        final String name = deserializeStringField(jsonObject, WebAnnotationFields.NAME);
        final String userGroup = deserializeStringField(jsonObject, "userGroup");
        final String type = deserializeStringField(jsonObject, WebAnnotationFields.TYPE);
        final String openId = deserializeStringField(jsonObject, "openId");
        final String mbox = deserializeStringField(jsonObject, "mbox");
        final String agentTypeStr = deserializeStringField(jsonObject, "agentType");

        final Agent agent = new Person();
        agent.setHomepage(homepage);
        agent.setInternalType(internalType);
        agent.setName(name);
        agent.setUserGroup(userGroup);
        agent.setType(type);
        //TODO: fix openId-httpUrl when the centralized API key management will be integrated
        agent.setHttpUrl(openId);
        agent.setEmail(mbox);
        agent.setInternalType(agentTypeStr);
        return agent;    	
    }
}
	