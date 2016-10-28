package eu.europeana.annotation.web.service.authentication.model;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import eu.europeana.annotation.definitions.model.agent.Agent;

public class ApplicationDeserializer extends BaseDeserializer implements JsonDeserializer<Application>
{
    @Override
    public Application deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
        throws JsonParseException
    {

    	final JsonObject jsonObject = je.getAsJsonObject();
    	// add string fields
        final String apiKey = deserializeStringField(jsonObject, "apiKey");
        final String name = deserializeStringField(jsonObject, "name");
        final String provider = deserializeStringField(jsonObject, "provider");
        final String organization = deserializeStringField(jsonObject, "organization");
        final String homepage = deserializeStringField(jsonObject, "homepage");
        // add agents
        final Agent anonymousUser = deserializeAgent("anonymousUser", je);
        final Agent adminUser = deserializeAgent("admin", je);
        
        // build application object
        final Application app = new ClientApplicationImpl();
        app.setApiKey(apiKey);
        app.setName(name);
        app.setProvider(provider);
        app.setOrganization(organization);
        app.setHomepage(homepage);
        app.setAnonymousUser(anonymousUser);
        app.setAdminUser(adminUser);
        // add authenticated users
        JsonElement jsonAuthenitcatedUsers = jsonObject.get("authenticatedUsers");
        JsonObject jsonMap = jsonAuthenitcatedUsers.getAsJsonObject();
        Iterator<Entry<String,JsonElement>> itr = jsonMap.entrySet().iterator();
        while (itr.hasNext()) {
        	Entry<String,JsonElement> elem = itr.next();
            Gson gson = registerDeserializer(Agent.class, new AgentDeserializer());
            final Agent authenticatedUser = gson.fromJson(elem.getValue(), Agent.class);
            app.addAuthenticatedUser(elem.getKey(), authenticatedUser);
        }
        return app;    	
    }
    
    
    public Agent deserializeAgent(String fieldName, JsonElement je) {
    	
        JsonElement jsonAnonymousUser = je.getAsJsonObject().get(fieldName);
        Gson gson = registerDeserializer(Agent.class, new AgentDeserializer());
        return gson.fromJson(jsonAnonymousUser, Agent.class);    	
    }
    
}
	