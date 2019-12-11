package eu.europeana.annotation.utils.serialization;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.deser.std.StdDeserializer;
import org.codehaus.jackson.node.ObjectNode;

import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.impl.BaseAnnotationId;
import eu.europeana.annotation.definitions.model.utils.ModelConst;

public class AnnotationIdDeserializer extends StdDeserializer<AnnotationId> {
	
	public AnnotationIdDeserializer() {
		super(AnnotationId.class);
		
	}

	@Override
	public AnnotationId deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		
		ObjectMapper mapper = (ObjectMapper) jp.getCodec();
		ObjectNode root = (ObjectNode) mapper.readTree(jp);
		Class<? extends AnnotationId> realClass = null;
//		BaseAnnotationId realClass = null;
		
//		String resourceId = "";
		String identifier = null;
		Iterator<Entry<String, JsonNode>> elementsIterator = root.getFields();
		while (elementsIterator.hasNext()) {
			Entry<String, JsonNode> element = elementsIterator.next();
//			if (ModelConst.RESOURCE_ID.equals(element.getKey())) {
//				resourceId = element.getValue().toString();//.getTextValue();
//			}
			if (ModelConst.ANNOTATION_NR.equals(element.getKey())) {
				identifier = element.getValue().getTextValue();
			}
//			if (ModelConst.AGENT_TYPES.equals(element.getKey())) {
//				String textValue = element.getValue().toString();//.getTextValue();
//				String typeValue = TypeUtils.getEuTypeFromTypeArrayStatic(textValue).replace("\"", "");
//				realClass = AgentObjectFactory.getInstance()
//						.getClassForType(typeValue);
//				break;
//			}
		}
		
		AnnotationId annotationId = new BaseAnnotationId();
		annotationId.setIdentifier(identifier);
//		annotationId.setResourceId(resourceId);
		
//		realClass = (Class<? extends AnnotationId>) annotationId;
//		realClass = annotationId;
		
//		if (realClass == null)
//			return null;
		
		return mapper.readValue(root, realClass);
	}
	
	
}
