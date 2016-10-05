package eu.europeana.annotation.utils.serialization;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.ObjectMapper;

import eu.europeana.annotation.utils.JsonUtils;

public class MapDeserializer extends JsonDeserializer<Map<String, String>> {

    @Override
    public Map<String, String> deserialize(JsonParser jp, DeserializationContext context)
            throws IOException {
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        Map<String, String> multilingualMap = new HashMap<String, String>();
        JsonNode jn = mapper.readTree(jp);
        if (jn != null) {
            String multilingualMapStr = jn.getTextValue();
            if (StringUtils.isNotEmpty(multilingualMapStr))
            	multilingualMap = JsonUtils.stringToMap(multilingualMapStr);
        }
        return multilingualMap;
    }
}