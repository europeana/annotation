package eu.europeana.annotation.utils.serialization;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.deser.std.StdDeserializer;
import org.codehaus.jackson.node.ObjectNode;

import eu.europeana.annotation.definitions.model.body.Body;
import eu.europeana.annotation.definitions.model.factory.impl.BodyObjectFactory;
import eu.europeana.annotation.definitions.model.utils.ModelConst;
import eu.europeana.annotation.definitions.model.utils.TypeUtils;
import eu.europeana.annotation.definitions.model.vocabulary.BodyTypes;

public class BodyDeserializer extends StdDeserializer<Body> {
	
	public BodyDeserializer() {
		super(Body.class);
		
	}

	@Override
	public Body deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		
		ObjectMapper mapper = (ObjectMapper) jp.getCodec();
		ObjectNode root = (ObjectNode) mapper.readTree(jp);
		Class<? extends Body> realClass = null;
		
		Iterator<Entry<String, JsonNode>> elementsIterator = root.getFields();
		while (elementsIterator.hasNext()) {
			Entry<String, JsonNode> element = elementsIterator.next();
			if (ModelConst.BODY_TYPE.equals(element.getKey())) {
				String value = element.getValue().getTextValue();
				String typeValue = TypeUtils.getInternalTypeFromTypeArrayStatic(value);
				if (StringUtils.isEmpty(value)) { 
					Iterator<JsonNode> itr = element.getValue().getElements();
					while (itr.hasNext()) {
						JsonNode jn = itr.next();
						String curElement = jn.toString().replace("\"", "");
					    if (BodyTypes.contains(curElement)) {
					    	value = curElement;
					    	break;
					    }
					}
					typeValue = value;
				}
				realClass = BodyObjectFactory.getInstance()
						.getClassForType(typeValue);
				break;
			}
		}
		
		if (realClass == null)
			return null;
		
		return mapper.readValue(root, realClass);
	}
	
	
}
