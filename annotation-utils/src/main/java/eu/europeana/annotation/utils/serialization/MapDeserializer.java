package eu.europeana.annotation.utils.serialization;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.deser.std.StdDeserializer;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.type.TypeReference;

public class MapDeserializer extends StdDeserializer<Map<String, String>> {
	
	public MapDeserializer() {
		super(Map.class);		
	}

	@Override
	public Map<String, String> deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		
	    Map<String, String>  retMap =
	              new HashMap<String, String>();

	    TypeReference<HashMap<String,String>[]>  typeRef
	        = new TypeReference<
	            HashMap<String,String>[]>() {};
	             
	     ObjectMapper mapper = new ObjectMapper();
	     HashMap<String, String>[] maps
	     	= mapper.readValue(jp, typeRef); 	             
	     
	     for (HashMap<String, String> oneMap : maps) {
	    	 Map.Entry<String,String> entry = oneMap.entrySet().iterator().next();	    	 
             String name = entry.getKey();
             String value = entry.getValue();
             retMap.put(name, value);   
         }       
         return retMap;		
//		ObjectMapper mapper = (ObjectMapper) jp.getCodec();
//		ObjectNode root = (ObjectNode) mapper.readTree(jp);
//		Class<? extends Body> realClass = null;
//		
//		Iterator<Entry<String, JsonNode>> elementsIterator = root.getFields();
//		while (elementsIterator.hasNext()) {
//			Entry<String, JsonNode> element = elementsIterator.next();
//			if (ModelConst.BODY_TYPE.equals(element.getKey())) {
//				String typeValue = TypeUtils.getEuTypeFromTypeArrayStatic(element.getValue().getTextValue());
//				realClass = BodyObjectFactory.getInstance()
//						.getClassForType(typeValue);
//				break;
//			}
//		}
//		
//		if (realClass == null)
//			return null;
		
//		return mapper.readValue(root, typeRef);
	}
	
	
}
