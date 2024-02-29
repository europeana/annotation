package eu.europeana.annotation.web.service.impl;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import eu.europeana.annotation.utils.HttpConnection;
import eu.europeana.annotation.utils.JsonUtils;

public class SearchApiClient {
  private final HttpConnection httpConnection = new HttpConnection();
  private final String baseUrl;
  private static final String PATTERN_QUERY_RECORD_PROVIDER =
      "%s&query=europeana_id:\"%s\"&qf=foaf_organization:\"%s\"&rows=0";

  public SearchApiClient(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public Map<String, Object> getSearchApiResponseMap(String recordId, String providerId) throws IOException {
    if (recordId == null || providerId == null) {
      return new HashMap<>();
    }

    String url = String.format(PATTERN_QUERY_RECORD_PROVIDER, baseUrl,
        URLEncoder.encode(recordId, StandardCharsets.UTF_8),
        URLEncoder.encode(providerId, StandardCharsets.UTF_8));
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
        httpConnection.getURLContentAsString(url.toString(), "Accept", "application/json");
    @SuppressWarnings("unchecked")
    Map<String, Object> searchApiRespMap =
        (Map<String, Object>) JsonUtils.getObjectMapper().readValue(searchApiResp, Map.class);
    return searchApiRespMap;
  }

  public boolean isRecordsContentProvider(String recordId, String providerId) throws IOException {
    Map<String, Object> searchApiResp = getSearchApiResponseMap(recordId, providerId);
    Integer searchApiRespResults = (Integer) searchApiResp.get("totalResults");
    return searchApiRespResults != null && searchApiRespResults > 0;
  }

}
