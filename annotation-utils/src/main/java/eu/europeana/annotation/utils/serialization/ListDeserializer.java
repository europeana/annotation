package eu.europeana.annotation.utils.serialization;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.ObjectMapper;

import eu.europeana.annotation.utils.JsonUtils;

public class ListDeserializer extends JsonDeserializer<List<String>> {

    @Override
    public List<String> deserialize(JsonParser jp, DeserializationContext context)
            throws IOException {
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        List<String> valueList = new ArrayList<String>();
        JsonNode jn = mapper.readTree(jp);
        if (jn != null) {
            String valueListStr = jn.getTextValue();
            valueList = JsonUtils.stringToList(valueListStr);
        }
        return valueList;
    }
}