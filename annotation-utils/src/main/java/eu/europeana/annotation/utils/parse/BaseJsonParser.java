package eu.europeana.annotation.utils.parse;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

public class BaseJsonParser {

	protected static ObjectMapper objectMapper = new ObjectMapper().setVisibility(JsonMethod.FIELD, Visibility.ANY)
			.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    protected static final JsonFactory jsonFactory = new JsonFactory();

    
    /**
     * Supports conversion of {["val1", "val2"]} or ["val1", "val2"] 
     * This method converts JSON string to List<String>.
     * @param value The input string
     * @return resulting List<String>
     */
    public static List<String> toStringList(String json, boolean removeQuote) {
    		String list = json.trim();
    		String startCurly = "{";
    		String endCurly = "}";
 		if(list.startsWith(startCurly))
    			list = list.substring(startCurly.length());
    		if(list.endsWith(endCurly))
    			list = list.substring(0, list.length() - endCurly.length());
 		list = list.trim();
 		
 		String startBracket = "[";
 		String endBracket = "]";
 		
 		if(list.startsWith(startBracket))
    			list = list.substring(startBracket.length());
    		if(list.endsWith(endBracket))
    			list = list.substring(0, list.length() - endBracket.length());
 		list = list.trim();
 		if(removeQuote)
 			list = list.replaceAll("\"", "");
 		
 		list = list.replaceAll("\n", "");
 		list = list.replaceAll(",", " ");
 		
 		String[] values = StringUtils.split(list);
 		
        return Arrays.asList(values);
    }
}
