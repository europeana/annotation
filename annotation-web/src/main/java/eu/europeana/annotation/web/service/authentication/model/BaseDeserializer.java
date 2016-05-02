package eu.europeana.annotation.web.service.authentication.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class BaseDeserializer 
{
    public String deserializeStringField(JsonObject jsonObject, String fieldName)
    {
        String res = "";
        if (jsonObject.get(fieldName) != null)
        	res = jsonObject.get(fieldName).getAsString();
        return res;    	
    }
    
    
    public <T> Gson registerDeserializer(Class<T> cls, Object object) {
	    GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(cls, object);
	    return gsonBuilder.create();	    	
    }
        
}
	