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
import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.definitions.model.factory.impl.AgentObjectFactory;
import eu.europeana.annotation.definitions.model.impl.BaseAnnotationId;
import eu.europeana.annotation.definitions.model.utils.ModelConst;
import eu.europeana.annotation.definitions.model.utils.TypeUtils;

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
		
		String resourceId = "";
		Integer annotationNr = 0;
		Iterator<Entry<String, JsonNode>> elementsIterator = root.getFields();
		while (elementsIterator.hasNext()) {
			Entry<String, JsonNode> element = elementsIterator.next();
			if (ModelConst.RESOURCE_ID.equals(element.getKey())) {
				resourceId = element.getValue().toString();//.getTextValue();
			}
//			if (ModelConst.ANNOTATION_NR.equals(element.getKey())) {
//				annotationNr = Integer.valueOf(element.getValue().getTextValue());
//			}
//			if (ModelConst.AGENT_TYPES.equals(element.getKey())) {
//				String textValue = element.getValue().toString();//.getTextValue();
//				String typeValue = TypeUtils.getEuTypeFromTypeArrayStatic(textValue).replace("\"", "");
//				realClass = AgentObjectFactory.getInstance()
//						.getClassForType(typeValue);
//				break;
//			}
		}
		
		BaseAnnotationId annotationId = new BaseAnnotationId();
		annotationId.setAnnotationNr(annotationNr);
		annotationId.setResourceId(resourceId);
		
//		realClass = (Class<? extends AnnotationId>) annotationId;
//		realClass = annotationId;
		
		if (realClass == null)
			return null;
		
		return mapper.readValue(root, realClass);
	}
	
	
}
