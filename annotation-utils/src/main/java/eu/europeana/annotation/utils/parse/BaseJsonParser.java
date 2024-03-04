package eu.europeana.annotation.utils.parse;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Base class for json parsing
 */
public class BaseJsonParser {

  /**
   * object mapper with default configurations
   */
  public static final ObjectMapper objectMapper =
      new ObjectMapper().setVisibility(JsonMethod.FIELD, Visibility.ANY)
          .configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  protected static final JsonFactory jsonFactory = new JsonFactory();


  /**
   * Supports conversion of {["val1", "val2"]} or ["val1", "val2"]
   * 
   * @param value The input string
   * @deprecated {@link #extractAnnotationIds(String)} instead
   * @return
   */
  @Deprecated
  public static List<Long> toLongList(String json, boolean removeQuote) {
    String list = json.trim();
    String startCurly = "{";
    String endCurly = "}";
    if (list.startsWith(startCurly))
      list = list.substring(startCurly.length());
    if (list.endsWith(endCurly))
      list = list.substring(0, list.length() - endCurly.length());
    list = list.trim();

    String startBracket = "[";
    String endBracket = "]";

    if (list.startsWith(startBracket))
      list = list.substring(startBracket.length());
    if (list.endsWith(endBracket))
      list = list.substring(0, list.length() - endBracket.length());
    list = list.trim();
    if (removeQuote)
      list = list.replaceAll("\"", "");

    list = list.replaceAll("\n", "");
    list = list.replaceAll(",", " ");

    String[] values = StringUtils.split(list);
    Long[] result = new Long[values.length];
    for (int i = 0; i < values.length; i++)
      result[i] = Long.parseLong(values[i]);
    return Arrays.asList(result);
  }

  /**
   * Returns the list with the Long value for anno_id fields
   * 
   * @param json
   * @return
   * @throws JsonProcessingException
   * @throws IOException
   */
  public static List<Long> extractAnnotationIds(String json)
      throws JsonProcessingException, IOException {
    List<JsonNode> nodes = objectMapper.readTree(json).findValues("anno_id");
    return nodes.stream().map(node -> node.asLong()).collect(Collectors.toList());
  }

}
