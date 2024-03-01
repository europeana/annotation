package eu.europeana.annotation.web.service.impl;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import eu.europeana.annotation.config.AnnotationConfiguration;
import eu.europeana.annotation.utils.HttpConnection;
import eu.europeana.annotation.utils.JsonUtils;

@Component(AnnotationConfiguration.BEAN_SEARCH_API_CLIENT)
@PropertySource(
    value = {"classpath:annotation.properties", "annotation.user.properties"},
    ignoreResourceNotFound = true)
public class SearchApiClient {
  
  private static final String PATTERN_QUERY_RECORD_PROVIDER =
      "%s&query=europeana_id:%s&qf=foaf_organization:%s&rows=0";

  @Value("${searchApi.baseUrl}")
  private String baseUrl;
  
  private final HttpConnection httpConnection = new HttpConnection();
  
  private Map<String, Object> getSearchApiResponseMap(String recordId, String providerId) throws IOException {
    
    String url = String.format(PATTERN_QUERY_RECORD_PROVIDER, baseUrl,
        URLEncoder.encode("\""+recordId+"\"", StandardCharsets.UTF_8),
        URLEncoder.encode("\""+providerId+"\"", StandardCharsets.UTF_8));
    // StringBuilder url=new StringBuilder(baseUrl);
    // url.append("?");
    // url.append(String.join("", "query=europeana_id:", URLEncoder.encode("\"" + recordId + "\"",
    // StandardCharsets.UTF_8)));
    // url.append("&");
    // url.append(String.join("", "qf=foaf_organization:", URLEncoder.encode("\"" + providerId +
    // "\"", StandardCharsets.UTF_8)));
    // url.append("&");
    // url.append("rows=0");
    // url.append("&");
    // url.append("wskey=apidemo");
    String searchApiResp =
        httpConnection.getURLContentAsString(url, "Accept", "application/json");
    @SuppressWarnings("unchecked")
    Map<String, Object> searchApiRespMap =
        (Map<String, Object>) JsonUtils.getObjectMapper().readValue(searchApiResp, Map.class);
    return searchApiRespMap;
  }

  /**
   * Verify if the provided providerId matches the content provider for the given record using the search api (search by using foaf_organization as filter)
   * @param recordId the data.europena.eu Id of an europeana record
   * @param providerId the data.europena.eu Id of and orgnaization
   * @return true if the search api indicates the provider id to match the content provider of the giver record, false otherwise
   * @throws IOException if a runctume exceptions occured during the invocation of the search api
   */
  public boolean isRecordsContentProvider(String recordId, String providerId) throws IOException {
    if (recordId == null || providerId == null) {
      return false;
    }
    Map<String, Object> searchApiResp = getSearchApiResponseMap(recordId, providerId);
    Integer searchApiRespResults = (Integer) searchApiResp.get("totalResults");
    return searchApiRespResults != null && searchApiRespResults > 0;
  }

}
