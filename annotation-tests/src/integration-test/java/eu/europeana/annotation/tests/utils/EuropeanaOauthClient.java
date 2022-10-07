package eu.europeana.annotation.tests.utils;

import java.nio.charset.StandardCharsets;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.http.HttpHeaders;

public class EuropeanaOauthClient {

    public EuropeanaOauthClient() {
    }

    public static String getOauthToken(String user, String oauthUri, String oauthParams) throws Exception {
        PostMethod post = new PostMethod(oauthUri);
        if (StringUtils.isNotBlank(oauthParams)) {
//            post.setRequestBody(oauthParams);
            post.setRequestEntity(new StringRequestEntity(oauthParams, "text/plain", "UTF-8"));
            post.setRequestHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
        }
        
        HttpClient client = new HttpClient();
        try {
            client.executeMethod(post);
            if (post.getResponseBody() != null && post.getResponseBody().length > 0) {
                byte[] byteResponse = post.getResponseBody();
                String jsonResponse = new String(byteResponse, StandardCharsets.UTF_8);
                JSONObject jo = new JSONObject(jsonResponse);
                return "Bearer " + jo.getString("access_token");
            }
        } finally {
            post.releaseConnection();
        }
    
        return null;
    }

}