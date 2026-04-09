package eu.europeana.annotation.web.service.impl;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import eu.europeana.api.commons.auth.AuthenticationHandler;
import eu.europeana.api.commons.http.HttpConnection;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.springframework.http.MediaType;
import eu.europeana.annotation.config.AnnotationConfiguration;
import eu.europeana.annotation.utils.parse.BaseJsonParser;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service(AnnotationConfiguration.BEAN_SEARCH_API_CLIENT)
public class SearchApiClient {

  @Resource
  AnnotationConfiguration configuration;

  @Resource(name = "searchApiAccess")
  AuthenticationHandler searchApiAccess;
  
  private static final String PATTERN_QUERY_RECORD_PROVIDER = "%s?query=europeana_id:%s&qf=foaf_organization:%s&rows=0";

  private final HttpConnection httpConnection = new HttpConnection();

  /**
   * Verify if the provided providerId matches the content provider for the given record using the search api (search by using foaf_organization as filter)
   * @param recordId the data.europena.eu Id of an europeana record
   * @param providerId the data.europena.eu Id of and organization
   * @return true if the search api indicates the provider id to match the content provider of the giver record, false otherwise
   * @throws IOException if a runtime exceptions occured during the invocation of the search api
   */
  public boolean isRecordsContentProvider(String recordId, String providerId) throws IOException {
    if (recordId == null || providerId == null) {
      return false;
    }
    Integer totalResults = getSearchApiResponse(recordId, providerId);
    return totalResults != null && totalResults > 0;
  }


  /**
   * Retrieves the total number of results from the Search API for a given record ID and provider ID.
   * Returns the total number of results as an integer or 0 if the response status is not HTTP OK.
   *
   * Query : <baseurl>?query=europeana_id:<recordId>&qf=foaf_organization:<providerId>&rows=0
   *
   * @param recordId the unique identifier of the europeana record
   * @param providerId the unique identifier of the content provider organization
   * @return the total number of results matching the record ID and provider ID
   * @throws IOException if an error occurs during the HTTP request or processing the response
   */
  private Integer getSearchApiResponse(String recordId, String providerId) throws IOException {
    String url = String.format(PATTERN_QUERY_RECORD_PROVIDER, configuration.getSearchApiBaseUrl(),
            URLEncoder.encode("\""+recordId+"\"", StandardCharsets.UTF_8),
            URLEncoder.encode("\""+providerId+"\"", StandardCharsets.UTF_8));

    try (CloseableHttpResponse response = httpConnection.get(url,
            Collections.singletonMap(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE),
            searchApiAccess)) {
      if (response.getCode() == HttpStatus.SC_OK) {
        Map<String, Object> res = BaseJsonParser.objectMapper.readValue(response.getEntity().getContent(), Map.class);
        //ensure a non null response, or fail
        Objects.nonNull(res);
        return (Integer) res.get("totalResults");
      }
      else return 0;
    }
  }
}
