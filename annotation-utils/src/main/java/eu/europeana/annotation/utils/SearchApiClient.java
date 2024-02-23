package eu.europeana.annotation.utils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SearchApiClient {
	private static final HttpConnection httpConnection=new HttpConnection();
	private static final String baseUrl="https://api.europeana.eu/record/search.json";
	private static Logger logger = LogManager.getLogger(SearchApiClient.class);
	
	public static Map<String, Object> getSearchApiResponseMap(String recordId, String affiliation) {
		if(recordId==null || affiliation==null) {
			return new HashMap<>();
		}
		StringBuilder url=new StringBuilder(baseUrl);
		url.append("?");
		url.append(String.join("", "query=europeana_id:", URLEncoder.encode("\"" + recordId + "\"", StandardCharsets.UTF_8)));
		url.append("&");
		url.append(String.join("", "qf=foaf_organization:", URLEncoder.encode("\"" + affiliation + "\"", StandardCharsets.UTF_8)));
		url.append("&");
		url.append("rows=0");
		url.append("&");
		url.append("wskey=apidemo");
		try {
			String searchApiResp = httpConnection.getURLContentAsString(url.toString(), "Accept", "application/json");
			@SuppressWarnings("unchecked")
			Map<String, Object> searchApiRespMap = (Map<String, Object>) JsonUtils.getObjectMapper().readValue(searchApiResp, Map.class);
			return searchApiRespMap;
		} catch (IOException e) {
			logger.warn("Exception during search api call!");
			return new HashMap<>();
		}
	}

}
